package com.ral.young.metrics.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.ral.young.metrics.model.DialogueFormat;
import com.ral.young.metrics.model.KeywordResult;
import com.ral.young.metrics.util.IdGenerator;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author renyunhui
 * @description 这是一个MinioService类
 * @date 2024-12-04 11-15-16
 * @since 1.0.0
 */
@Service
@Slf4j
public class MinioService implements ApplicationRunner {

    @Resource
    private MinioClient minioClient;
    @Resource
    private IdGenerator idGenerator;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.oss.bucket-name}")
    private String ossBucketName;

    @Value("${minio.source.bucket-name}")
    private String sourceBucketName;

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 16, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10000), new ThreadFactoryBuilder().setNamePrefix("minio-oss-").build(), new ThreadPoolExecutor.DiscardOldestPolicy());

    static {
        // 线程预热
        executor.prestartAllCoreThreads();
    }

    /**
     * 异步上传文件
     * 小文件(<5MB)使用普通上传
     * 大文件(>=5MB)使用分片上传
     *
     * @param files 文件
     * @return 文件访问路径
     */
    public List<String> asyncUploadFile(MultipartFile[] files) {
        // 定义大文件阈值为5MB
        final long LARGE_FILE_THRESHOLD = 5 * 1024 * 1024;
        List<String> uploadedFiles = new ArrayList<>();
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (MultipartFile file : files) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    String fileName = generateFileName(file.getOriginalFilename());
                    if (file.getSize() >= LARGE_FILE_THRESHOLD) {
                        // 大文件使用分片上传
                        return uploadLargeFile(file, fileName, startTime);
                    } else {
                        // 小文件使用普通上传
                        return uploadSmallFile(file, fileName, startTime);
                    }
                } catch (Exception e) {
                    log.error("文件上传失败", e);
                    throw new RuntimeException("文件上传失败", e);
                }
            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenAccept(v -> futures.forEach(future -> {
            try {
                uploadedFiles.add(future.get());
            } catch (Exception e) {
                log.error("获取上传结失败", e);
                throw new RuntimeException("获取上传结果失败", e);
            }
        })).join();
        return uploadedFiles;
    }

    /**
     * 上传小文件
     */
    private String uploadSmallFile(MultipartFile file, String fileName, long startTime) throws Exception {
        InputStream inputStream = file.getInputStream();
        PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(inputStream, file.getSize(), -1).contentType(file.getContentType()).build();

        minioClient.putObject(objectArgs);
        log.info("小文件 [{}] 上传完成，大小: {}KB, 耗时: {}ms", fileName, file.getSize() / 1024, System.currentTimeMillis() - startTime);
        return fileName;
    }

    /**
     * 上传大文件
     */
    private String uploadLargeFile(MultipartFile file, String fileName, long startTime) throws Exception {
        InputStream inputStream = file.getInputStream();
        long partSize = 5 * 1024 * 1024; // 设置分片大小为5MB
        long objectSize = file.getSize();
        int partCount = (int) (objectSize / partSize + (objectSize % partSize != 0 ? 1 : 0));

        List<CompletableFuture<Void>> partFutures = new ArrayList<>();

        for (int partNumber = 1; partNumber <= partCount; partNumber++) {
            // 计算需要跳过的字节数，这是为了确定当前分片的起始位置
            long skipBytes = (partNumber - 1) * partSize;
            // 计算当前分片的大小，不能超过剩余的文件大小
            long size = Math.min(partSize, objectSize - skipBytes);
            // 创建一个新的缓冲输入流，用于读取文件的指定部分
            InputStream partStream = new BufferedInputStream(inputStream);
            // 跳过已经处理过的部分，定位到当前分片始位置
            partStream.skip(skipBytes);

            int finalPartNumber = partNumber;
            CompletableFuture<Void> partFuture = CompletableFuture.runAsync(() -> {
                try {
                    PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(sourceBucketName).object(fileName + "-" + finalPartNumber) // 为每个分片创建唯一的文件名
                            .stream(partStream, size, -1).build();
                    minioClient.putObject(objectArgs);
                } catch (Exception e) {
                    log.error("分片上传失败", e);
                    throw new RuntimeException("分片上传失败", e);
                } finally {
                    try {
                        partStream.close(); // 确保每个分片流在使用后被关闭
                    } catch (IOException ex) {
                        log.error("分片流关闭失败", ex);
                    }
                }
            });
            partFutures.add(partFuture);
        }

        CompletableFuture.allOf(partFutures.toArray(new CompletableFuture[0])).join();
        log.info("大文件 [{}] 上传完成，大小: {}KB, 耗时: {}ms", fileName, file.getSize() / 1024, System.currentTimeMillis() - startTime);
        return fileName;
    }

    /**
     * 生成唯一文件名
     */
    private String generateFileName(String originalFilename) {
        return IdUtil.fastSimpleUUID() + getFileExtension(originalFilename);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 上传ZIP文件中的JSON文件到Minio
     *
     * @param zipFile ZIP文件
     * @param format 对话格式
     * @return 上传的文件ID列表
     */
    public List<Long> uploadZipJsonFiles(MultipartFile zipFile, DialogueFormat format) {
        List<Long> uploadedFiles = Collections.synchronizedList(new ArrayList<>());
        List<JSONArray> parsedContentList = Collections.synchronizedList(new ArrayList<>());
        long tag = idGenerator.nextId();

        StopWatch stopWatch = new StopWatch("上传ZIP文件");
        stopWatch.start("解析ZIP文件");

        try {
            // 解析ZIP文件
            AtomicInteger processedFiles = parseZipFile(zipFile, format, parsedContentList);
            stopWatch.stop();

            // 上传解析后的内容到Minio
            stopWatch.start("异步上传文件到Minio");
            uploadParsedContent(parsedContentList, uploadedFiles, tag);
            stopWatch.stop();

            // todo 1.上传源文件到 Minio  2.存储本次数据集的 tag 以及 上传的文件ID 到数据库，方便后续的分页查询以及文件内容的修改
            stopWatch.start("上传源文件到Minio");
            uploadLargeFile(zipFile, tag + ".zip", System.currentTimeMillis());
            stopWatch.stop();

            // 输出统计信息
            log.info("ZIP文件处理完成 - 总文件数: {}, 总对话数: {}, 总耗时: {}ms\n任务详情:\n{}", processedFiles.get(), uploadedFiles.size(), stopWatch.getTotalTimeMillis(), stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            log.error("处理ZIP文件失败", e);
            throw new RuntimeException("处理ZIP文件失败", e);
        }

        return uploadedFiles;
    }

    /**
     * 解析ZIP文件内容
     */
    private AtomicInteger parseZipFile(MultipartFile zipFile, DialogueFormat format, List<JSONArray> parsedContentList) throws IOException {
        AtomicInteger processedFiles = new AtomicInteger(0);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        try (ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream(), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".json")) {
                    processJsonEntry(entry, zipInputStream, format, parsedContentList, processedFiles, futures);
                }
                zipInputStream.closeEntry();
            }
        }

        // 等待所有解析任务完成
        waitForFutures(futures, "文件解析");

        return processedFiles;
    }

    /**
     * 处理单个JSON文件
     */
    private void processJsonEntry(ZipEntry entry, ZipInputStream zipInputStream, DialogueFormat format, List<JSONArray> parsedContentList, AtomicInteger processedFiles, List<CompletableFuture<Void>> futures) throws IOException {
        String fileName = entry.getName();
        log.info("开始处理文件: {}", fileName);

        byte[] entryContent = readZipEntryToBytes(zipInputStream);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                long startTime = System.currentTimeMillis();
                JSONArray parsedContentArray = format.parseStream(new ByteArrayInputStream(entryContent));
                parsedContentList.add(parsedContentArray);

                int fileCount = processedFiles.incrementAndGet();
                log.info("文件 [{}] 解析完成 - {}, 大小: {}KB, 耗时: {}ms", fileName, fileCount, entry.getSize() / 1024, System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                log.error("处理文件 {} 失败: ", fileName, e);
                throw new CompletionException(e);
            }
        }, executor);
        futures.add(future);
    }

    /**
     * 上传解析后的内容到Minio
     */
    private void uploadParsedContent(List<JSONArray> parsedContentList, List<Long> uploadedFiles, long tag) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (JSONArray parsedContentArray : parsedContentList) {
            for (int i = 0; i < parsedContentArray.size(); i++) {
                int finalI = i;
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        Long id = uploadJsonWithTag(parsedContentArray.getJSONObject(finalI), tag);
                        uploadedFiles.add(id);
                    } catch (Exception e) {
                        log.error("批量上传失败", e);
                        throw new CompletionException(e);
                    }
                }, executor);
                futures.add(future);
            }
        }

        waitForFutures(futures, "文件上传");
    }

    /**
     * 等待所有Future完成
     */
    private void waitForFutures(List<CompletableFuture<Void>> futures, String operationType) {
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (CompletionException e) {
            log.error("{}过程发生错误", operationType, e);
            throw new RuntimeException(operationType + "失败", e.getCause());
        }
        futures.clear();
    }

    /**
     * 读取ZIP文件条目内容到字节数组
     */
    private byte[] readZipEntryToBytes(ZipInputStream zipInputStream) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = zipInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        }
    }

    public void checkBucketExist() {
        // 检查并创建必要的bucket
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(ossBucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(ossBucketName).build());
                log.info("创建bucket: {}", ossBucketName);
            }

            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(sourceBucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(sourceBucketName).build());
                log.info("创建bucket: {}", sourceBucketName);
            }
        } catch (Exception e) {
            log.error("检查/创建bucket失败: ", e);
        }
    }

    /**
     * 上传JSON内容并添加标签
     */
    private Long uploadJsonWithTag(JSONObject jsonObject, Long tag) {
        long id = idGenerator.nextId();
        try {
            // 使用tag作为目录名
            String directory = String.format("tag_%d/", tag);
            String fileName = directory + id + ".json";

            byte[] jsonBytes = jsonObject.toString().getBytes(StandardCharsets.UTF_8);

            Map<String, String> tags = new HashMap<>();
            tags.put("type", String.valueOf(tag));

            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(ossBucketName).object(fileName)  // 文件路径现在包含目录
                    .stream(new ByteArrayInputStream(jsonBytes), jsonBytes.length, -1).contentType("application/json").tags(tags).build();

            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            log.error("上传JSON文件失败: ", e);
            throw new RuntimeException("上传JSON文件失败", e);
        }
        return id;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        checkBucketExist();
    }

    /**
     * 分页查询指定tag的对象
     */
    public PageResult<JSONObject> listObjectsByTag(String tag, int page, int size) {
        StopWatch stopWatch = new StopWatch("查询标签对象");
        PageResult<JSONObject> pageResult = new PageResult<>();

        try {
            String prefix = String.format("tag_%s/", tag);

            // 获取分页的文件名列表
            stopWatch.start("获取文件列表");
            List<String> fileNameList = getPagedFileNames(prefix, page, size);
            stopWatch.stop();

            // 并行加载文件内容
            // todo 将这一步放在数据库去执行
            stopWatch.start("加载文件内容");
            List<JSONObject> resultList = loadFileContents(fileNameList);
            stopWatch.stop();

            // 设置分页结果
            pageResult.setList(resultList);
            pageResult.setPage(page);
            pageResult.setSize(size);
            pageResult.setTotal(0);

            log.info("查询标签对象完成 - tag={}, page={}, size={}, total={}\n耗时统计:\n{}", tag, page, size, 0, stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            log.error("查询标签对象失败: tag={}, page={}, size={}", tag, page, size, e);
            throw new RuntimeException("查询标签对象失败", e);
        }
        return pageResult;
    }

    /**
     * 获取分页的文件名列表
     */
    private List<String> getPagedFileNames(String prefix, int page, int size) throws Exception {
        List<String> fileNameList = new ArrayList<>();
        int count = 0;
        int skip = (page - 1) * size;

        ListObjectsArgs listArgs = ListObjectsArgs.builder().bucket(ossBucketName).prefix(prefix).recursive(true).build();

        Iterable<Result<Item>> results = minioClient.listObjects(listArgs);
        for (Result<Item> result : results) {
            Item item = result.get();

            if (count < skip) {
                count++;
                continue;
            }

            if (fileNameList.size() >= size) {
                break;
            }

            fileNameList.add(item.objectName());
        }
        return fileNameList;
    }

    /**
     * 并行加载文件内容
     */
    private List<JSONObject> loadFileContents(List<String> fileNameList) {
        List<CompletableFuture<JSONObject>> futures = fileNameList.stream().map(objectName -> CompletableFuture.supplyAsync(() -> loadSingleFile(objectName), executor)).collect(Collectors.toList());

        return futures.stream().map(this::getFutureResult).filter(Objects::nonNull).sorted(Comparator.comparing(obj -> obj.getStr("fileId"))).collect(Collectors.toList());
    }

    /**
     * 加载单个文件内容
     */
    private JSONObject loadSingleFile(String objectName) {
        try {
            GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder().bucket(ossBucketName).object(objectName).build());

            String content = IoUtil.read(response, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject();
            String fileId = objectName.substring(objectName.lastIndexOf('/') + 1, objectName.lastIndexOf('.'));
            jsonObject.set("fileId", fileId);
            jsonObject.set("content", JSONUtil.parseObj(content));
            return jsonObject;
        } catch (Exception e) {
            log.error("加载对象内容失败: objectName={}", objectName, e);
            throw new CompletionException(e);
        }
    }

    /**
     * 获取Future结果
     */
    private JSONObject getFutureResult(CompletableFuture<JSONObject> future) {
        try {
            return future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("获取异步任务结果失败", e);
            return null;
        }
    }

    /**
     * 获取对象总数
     */
    private long getObjectCount(String prefix) {
        return StreamSupport.stream(minioClient.listObjects(ListObjectsArgs.builder().bucket(ossBucketName).prefix(prefix).recursive(true).build()).spliterator(), false).count();
    }

    /**
     * 通过tag和fileId获取并提取JSON中的关键字
     * @param tag 标签
     * @param fileId 文件ID
     * @return KeywordResult 各部分的关键字列表
     */
    public KeywordResult getKeywordsByTagAndFileId(String tag, String fileId) {
        KeywordResult result = new KeywordResult();
        StopWatch stopWatch = new StopWatch("获取文件关键字");
        try {
            // 构建文件路径
            String objectName = String.format("tag_%s/%s.json", tag, fileId);

            // 获取文件内容
            stopWatch.start("从Minio获取文件");
            GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder().bucket(ossBucketName).object(objectName).build());
            String content = IoUtil.read(response, StandardCharsets.UTF_8);
            stopWatch.stop();

            // 解析JSON内容
            stopWatch.start("解析JSON内容");
            JSONObject contentObj = JSONUtil.parseObj(content);
            stopWatch.stop();

            // 提取System部分的关键字
            stopWatch.start("提取System关键字");
            String systemContent = contentObj.getStr("System");
            if (systemContent != null) {
                result.setSystemKeywords(extractSystemKeywords(systemContent));
            }
            stopWatch.stop();

            // 提取Conversations中的Response关键字
            stopWatch.start("提取Response关键字");
            JSONArray conversations = contentObj.getJSONArray("Conversations");
            List<List<KeywordResult.KeyValue>> responseKeywords = new ArrayList<>();
            if (CollUtil.isNotEmpty(conversations)) {
                for (int i = 0; i < conversations.size(); i++) {
                    JSONObject conversation = conversations.getJSONObject(i);
                    String responseStr = conversation.getStr("Response");
                    if (StrUtil.isNotBlank(responseStr)) {
                        responseKeywords.add(extractResponseKeywords(responseStr));
                    }
                }
                result.setResponseKeywords(responseKeywords);
            }
            stopWatch.stop();

            // 输出耗时统计
            log.info("获取文件关键字完成 - tag={}, fileId={}\n耗时统计:\n{}", tag, fileId, stopWatch.prettyPrint(TimeUnit.MILLISECONDS));

        } catch (Exception e) {
            log.error("获取关键字失败: tag={}, fileId={}", tag, fileId, e);
            throw new RuntimeException("获取关键字失败", e);
        }
        return result;
    }

    /**
     * 删除标签下的文件
     * @param tag 标签
     * @param fileIds 文件ID列表(为空时删除该标签下所有文件)
     * @return 删除的文件数量
     */
    public int deleteTagFiles(String tag, List<String> fileIds) {
        StopWatch stopWatch = new StopWatch("删除标签文件");
        AtomicInteger deleteCount = new AtomicInteger(0);

        try {
            String prefix = String.format("tag_%s/", tag);
            List<String> objectNames = new ArrayList<>();

            // 获取要删除的文件列表
            stopWatch.start("获取文件列表");
            if (CollUtil.isEmpty(fileIds)) {
                // 删除所有文件时，获取标签下所有文件
                Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(ossBucketName).prefix(prefix).recursive(true).build());
                for (Result<Item> result : results) {
                    objectNames.add(result.get().objectName());
                }
            } else {
                // 删除指定文件时，构建文件路径
                objectNames = fileIds.stream().map(fileId -> prefix + fileId + ".json").collect(Collectors.toList());
            }
            stopWatch.stop();

            if (objectNames.isEmpty()) {
                log.info("没有需要删除的文件: tag={}, fileIds={}", tag, fileIds);
                return 0;
            }

            // 并行删除文件
            stopWatch.start("删除文件");

            CompletableFuture.allOf(objectNames.stream().map(objectName -> CompletableFuture.runAsync(() -> {
                try {
                    minioClient.removeObject(RemoveObjectArgs.builder().bucket(ossBucketName).object(objectName).build());
                    deleteCount.incrementAndGet();
                    log.debug("删除文件成功: {}", objectName);
                } catch (Exception e) {
                    log.error("删除文件失败: {}", objectName, e);
                    throw new CompletionException(e);
                }
            }, executor)).toArray(CompletableFuture[]::new)).join();
            stopWatch.stop();

            log.info("删除标签文件完成 - tag={}, fileIds={}, 删除文件数={}\n耗时统计:\n{}", tag, fileIds, deleteCount.get(), stopWatch.prettyPrint(TimeUnit.MILLISECONDS));

        } catch (Exception e) {
            log.error("删除标签文件失败: tag={}, fileIds={}", tag, fileIds, e);
            throw new RuntimeException("删除标签文件失败", e);
        }
        return deleteCount.get();
    }

    public static void main(String[] args) {
        String data = "{\n" + "\t\t\t\t\"System\": \"\\n## 场景与角色\\n\\n这是一个政务问答场景，其中涉及3个角色，用户——来政府办事的普通公民，业务分拣员——细化明确用户的政务问题并将其分类到具体业务，业务办理员——负责实际解答具体的业务问题。**注意**你是业务分拣员，只负责对用户的政务问题进行细化明确和分类到具体业务，并不负责解答具体的业务问题！\\n\\n## 业务范围\\n\\n你需要分类的业务范围由以下JSON描述：\\n\\n[{\\\"资质认证\\\": [{\\\"房地产开发企业二级资质核定\\\": [\\\"开发资质注销\\\", \\\"开发资质延续\\\", \\\"开发资质变更\\\", \\\"开发资质重新核定\\\", \\\"开发资质新办\\\", \\\"开发资质延续（含重新核定）\\\"]}, {\\\"建筑施工企业资质认定\\\": [\\\"施工资质注销\\\", \\\"施工资质增项\\\", \\\"施工资质延续\\\", \\\"施工资质重组、合并、分立\\\", \\\"施工资质变更\\\", \\\"施工资质新办\\\"]}, \\\"测绘资质单位名称、法人、地址变更登记与资质注销\\\", \\\"测绘资质单位分支机构登记\\\", \\\"文物保护工程施工二级资质认定或增加二级资质业务范围的审批\\\", \\\"升放无人驾驶自由气球、系留气球单位资质认定\\\", \\\"文物保护工程监理乙级资质认定或增加乙级资质业务范围审批\\\", \\\"异地开展电梯维护保养单位相应资质证明告知\\\", \\\"全国残疾人按比例就业情况联网认证\\\", \\\"文物保护工程勘察设计乙级资质认定或增加乙级资质业务范围的审批\\\"]}, {\\\"行政缴费\\\": [\\\"缴费人员减少\\\", \\\"达到法定退休年龄缴费不满15年社保权益申请\\\"]}, {\\\"安全生产\\\": [{\\\"危险化学品安全使用许可证核发\\\": [\\\"危险化学品安全使用许可证变更审查\\\", \\\"危险化学品安全使用许可证延期审查\\\", \\\"危险化学品安全使用许可证首次申请\\\"]}, {\\\"省级范围内危险化学品生产企业安全生产许可证核发（非中央企业及其直接控股涉及危险化学品生产企业〔总部〕）\\\": [\\\"危险化学品生产企业安全生产许可证变更\\\", \\\"危险化学品生产企业安全生产许可证延期\\\", \\\"危险化学品生产企业安全生产许可首次申请\\\"]}, {\\\"危险化学品（含仓储经营）经营许可\\\": [\\\"从事剧毒、易制爆、汽油加油站、专用危化品仓储的企业，中央企业所属省级、设区的市级公司（分公司）从事危...\\\", \\\"危险化学品经营许可证核发\\\", \\\"危险化学品经营许可证延期审查\\\"]}, {\\\"烟花爆竹经营（批发）许可证核发\\\": [\\\"烟花爆竹经营（批发）许可证延期申请\\\", \\\"烟花爆竹经营（批发）许可证变更申请\\\", \\\"烟花爆竹经营（批发）许可证首次申请\\\"]}, {\\\"非煤矿矿山企业安全生产许可证核发\\\": [\\\"非煤矿山企业安全生产许可证变更\\\", \\\"非煤矿山企业安全生产许可证首次发证\\\", \\\"非煤矿山企业安全生产许可证延期\\\"]}, \\\"金属冶炼建设项目安全设施设计审查\\\", \\\"生产、储存烟花爆竹建设项目安全设施设计审查\\\", \\\"二类非药品类易制毒化学品经营备案\\\", \\\"危险化学品建设项目安全许可（安全条件审查）\\\", \\\"危险物品的生产、经营、储存单位以及矿山、金属冶炼单位主要负责人和安全生产管理人员安全生产知识和管理能力考核发证\\\", \\\"危险化学品建设项目安全许可（安全设施设计审查）\\\"]}, {\\\"职业资格\\\": [{\\\"公证员执业、变更许可\\\": [\\\"公证员执业变更许可\\\", \\\"公证员执业证换发\\\", \\\"公证员执业许可\\\", \\\"公证员执业证补办\\\"]}, {\\\"道路危险货物运输驾驶员从业资格证核发\\\": [\\\"道路危险货物运输驾驶员从业资格证注销\\\", \\\"道路危险货物运输驾驶员从业资格证核发\\\", \\\"道路危险货物运输驾驶员从业资格证变更\\\", \\\"道路危险货物运输驾驶员从业资格证转籍\\\", \\\"道路危险货物运输驾驶员从业资格证补发\\\", \\\"道路危险货物运输驾驶员从业资格证换证\\\"]}, {\\\"基层法律服务工作者执业、变更、注销许可\\\": [\\\"基层法律服务工作者执业注销\\\", \\\"基层法律服务工作者执业许可\\\", \\\"基层法律服务工作者执业变更\\\"]}, {\\\"网络预约出租汽车驾驶员证发放\\\": [\\\"网络预约出租汽车驾驶员从业资格证补办\\\", \\\"网络预约出租汽车驾驶员从业资格证件注销\\\", \\\"网络预约出租汽车驾驶员从业资格证件变更\\\", \\\"网络预约出租汽车驾驶员从业资格证换发\\\"]}, {\\\"经营性道路货物运输驾驶员从业资格证核发\\\": [\\\"经营性道路货物运输驾驶员从业资格证注销\\\", \\\"经营性道路货物运输驾驶员从业资格证转籍\\\", \\\"经营性道路货物运输驾驶员从业资格证变更\\\", \\\"经营性道路货物运输驾驶员从业资格证核发\\\", \\\"经营性道路货物运输驾驶员从业资格证补发\\\", \\\"经营性道路货物运输驾驶员从业资格证换证\\\"]}, \\\"港澳医师来内地短期执业审批\\\", \\\"执业药师注销注册\\\", \\\"领取一级注册消防工程师资格证书\\\", \\\"遗失技能人员职业资格证书补发申请\\\", \\\"申报职业技能鉴定\\\", \\\"领取一、二级注册建筑师资格证书\\\", \\\"执业药师首次注册\\\", \\\"执业药师延续注册\\\", \\\"教师资格认定\\\", \\\"职业技能鉴定补贴申领\\\", \\\"外籍医师来华短期执业许可\\\", \\\"会计专业技术人员继续教育信息查询\\\", \\\"船员特殊培训资格认定\\\", \\\"律师执业注销许可\\\", \\\"境外就业和对外劳务合作人员换发技能人员职业资格证书申请\\\", \\\"领取注册测绘师资格证书\\\", \\\"中小学教师职称评聘政策咨询\\\", \\\"更正职业资格证书信息申请\\\", \\\"领取助理社会工作师、社会工作师、高级社会工作师资格证书\\\", \\\"注册测绘师考试报名资格审查\\\", \\\"执业药师变更注册\\\", \\\"领取勘察设计注册工程师资格证书\\\", \\\"会计专业技术人员继续教育办理\\\", \\\"领取注册设备监理师资格证书\\\", \\\"领取注册城乡规划师资格证书\\\", \\\"律师执业许可\\\", {\\\"巡游出租汽车驾驶员从业资格证件核发\\\": [\\\"巡游出租汽车驾驶员从业资格证件换证\\\", \\\"巡游出租汽车驾驶员从业资格证件变更\\\", \\\"巡游出租汽车驾驶员从业资格证件补发\\\", \\\"巡游出租汽车驾驶员从业资格证件注销\\\", \\\"巡游出租汽车驾驶员从业资格证件核发\\\"]}, {\\\"司法鉴定人执业、变更、延续、注销登记\\\": [\\\"司法鉴定人执业登记\\\", \\\"司法鉴定人执业证换发、补办\\\", \\\"司法鉴定人变更执业机构\\\"]}, {\\\"律师执业变更许可\\\": [\\\"律师执业变更许可\\\", \\\"律师执业证换发、补办\\\"]}, {\\\"道路危险货物运输装卸管理人员和押运人员从业资格证核发\\\": [\\\"道路危险货物运输装卸管理人员和押运人员从业资格证核发\\\", \\\"道路危险货物运输装卸管理人员和押运人员从业资格证注销\\\", \\\"道路危险货物运输装卸管理人员和押运人员从业资格证转籍\\\", \\\"道路危险货物运输装卸管理人员和押运人员从业资格证变更\\\", \\\"道路危险货物运输装卸管理人员和押运人员从业资格证补发\\\", \\\"道路危险货物运输装卸管理人员和押运人员从业资格证换证\\\"]}, {\\\"法律职业资格考试业务咨询\\\": [\\\"应届毕业生法律职业资格认定（享受放宽条件政策的除外）\\\", \\\"法律职业资格考试业务咨询\\\"]}, {\\\"医疗机构执业登记（人体器官移植除外）\\\": [\\\"医疗机构执业变更（承诺件）（市级）\\\", \\\"医疗机构执业审批（注销）（市级）\\\", \\\"医疗机构执业审批（新办）（市级）\\\", \\\"医疗机构执业审批（校验）（市级）\\\", \\\"医疗机构执业许可证遗失补办(市级)\\\"]}, {\\\"经营性道路旅客运输驾驶员从业资格证核发\\\": [\\\"经营性道路旅客运输驾驶员从业资格证换证\\\", \\\"经营性道路旅客运输驾驶员从业资格证补发\\\", \\\"经营性道路旅客运输驾驶员从业资格证转籍\\\", \\\"经营性道路旅客运输驾驶员从业资格证核发\\\", \\\"经营性道路旅客运输驾驶员从业资格证注销\\\", \\\"经营性道路旅客运输驾驶员从业资格证变更\\\"]}, {\\\"特种设备作业人员资格认定\\\": [\\\"特种设备作业人员资格认定取证\\\", \\\"特种设备作业人员资格认定补发\\\", \\\"特种设备作业人员资格认定复审\\\"]}]}]\\n\\n以上JSON结构代表了业务的分类和层次关系，形成一个类树状结构，JSON中每个字符串是一项业务，从“树”的“根”到“叶子”，业务逐渐明晰。例如，\\\"住房公积金提取\\\"不是一项叶子业务，因为它还可以进一步细分为很多子项，而\\\"正常退休提取住房公积金\\\"是一项叶子业务，已不可再细分。\\n## 输出规则\\n\\n1.如果用户的问题与政务完全无关，或者语义完全不通，请以{\\\"角色\\\":\\\"业务分拣员\\\", \\\"回复\\\":\\\"XXX\\\"}格式进行解答或者拒绝，并给出合理的引导。例如：{\\\"角色\\\":\\\"业务分拣员\\\", \\\"回复\\\":\\\"我是考拉悠然开发的政务助手，您可以问我以下政务问题：xxx\\\"}。\\n\\n2.如果用户问的是关于业务分类相关的问题或者是跨多个业务的问题，请以{\\\"角色\\\":\\\"业务分拣员\\\", \\\"回复\\\":\\\"XXX\\\"}格式解答，并对用户做出合理的引导，指引用户可以问的问题，并且指引用户应该一件一件的办理业务。\\n\\n3.如果用户想问的是业务问题，并且从用户问题结合上下文历史无法完全确定用户要办理的叶子业务，请按{\\\"角色\\\":\\\"业务分拣员\\\",\\\"可能的业务\\\":[\\\"业务1XX\\\",\\\"业务2XX\\\"],\\\"追问\\\":\\\"您是想办理XX还是XX?\\\"}JSON格式输出最可能的业务列表（即结合上下文判断用户最可能想办理的业务，列表中元素不一定是叶子业务），并针对其中尚不明确的点向用户追问，请通过从抽象到具体，从根到叶子逐层次递进的方式，通过多轮次的交互来逐步确定到叶子业务，而不要一次性问用户太多问题，也不要在一次追问中涉及多个层次的业务。注意：\\\"可能的业务\\\"中要至少包含两项业务，且不超过10项，不能重复，并且其中业务的层次应该与追问中的业务层次相一致。例如:\\nuser: \\\"我想办理公积金\\\"\\nassistant: {\\\"角色\\\":\\\"业务分拣员\\\",\\\"可能的业务\\\" : [\\\"住房公积金贷款\\\", \\\"住房公积金提取\\\", \\\"住房公积金汇缴\\\", ...], \\\"追问\\\":\\\"你是想办理公积金贷款、提取还是汇缴？\\\"}}\\nuser: \\\"提取\\\"\\nassistant: {\\\"角色\\\":\\\"业务分拣员\\\",\\\"可能的业务\\\" : [\\\"提前退休提取住房公积金\\\", \\\"xxxx\\\", \\\"xxx\\\", ...], \\\"追问\\\":\\\"xxx\\\"}。通过如上多轮逐渐明晰、细化业务，直到确定到一项叶子业务。只要不是完全确定，就应该继续追问澄清。\\n\\n4.如果用户想问的是业务问题，并且从用户问题结合上下文历史已经能够完全推断用户想要办理的叶子业务，请以{\\\"角色\\\":\\\"业务分拣员\\\", \\\"业务\\\":\\\"xx\\\", \\\"改写后问题\\\":\\\"xx\\\"}JSON格式输出要办理的叶子业务，以及改写后的问题。注意（1）其中 \\\"业务\\\":\\\"xx\\\"必须是一项叶子业务（2）改写后的问题首先应该能够直接明确叶子业务，其次应该仍然符合用户原始关心的问题（比如：上文用户问“办理流程”，后面补充是业务是“XXX”，则改写的问题应该是“XXX的办理流程是怎样的？”），不要无中生有，再者改写后的问题应该仍是以用户口吻发问的，如果用户问题本身已经明确叶子业务，那么可以直接将用户问题作为改写后的问题。\\n\\n5.请严格按1~4中定义的JSON格式输出（是且仅是其中的一个）, 并确保可以被Python json.loads方法解析。\\n\\n## 对话历史说明\\n\\n1.对话历史中的role取值\\\"user\\\"的代表用户，role取值\\\"assistant\\\"的代表\\\"业务分拣员\\\"和\\\"业务办理员\\\"，具体通过content区分，content中指明\\\"角色\\\":\\\"业务分拣员\\\"的来自业务分拣员，其它的则来自业务办理员。\\n\\n2.对话历史中来自业务分拣员的消息有2种形式：\\n（1）{\\\"角色\\\":\\\"业务分拣员\\\", \\\"回复\\\":\\\"XXX\\\"}\\n（2）{\\\"角色\\\":\\\"业务分拣员\\\",\\\"可能的业务\\\":[\\\"业务1XX\\\",\\\"业务2XX\\\"],\\\"追问\\\":\\\"XXX\\\"}\\n\\n3.对话历史中来自业务办理员的消息通常是非结构化的普通文本，代表了对叶子业务的解答。**注意**你是业务分拣员，不要输出对叶子业务的解答！\\n\",\n" + "\t\t\t\t\"Conversations\": [\n" + "\t\t\t\t\t{\n" + "\t\t\t\t\t\t\"Id\": 1864562735306010625,\n" + "\t\t\t\t\t\t\"Prompt\": \"我要开个烟花爆竹店，需要什么资质吗？\",\n" + "\t\t\t\t\t\t\"Response\": \"{\\\"角色\\\":\\\"业务分拣员\\\",\\\"可能的业务\\\":[\\\"烟花爆竹经营（批发）许可证首次申请\\\",\\\"烟花爆竹经营（批发）许可证延期申请\\\",\\\"烟花爆竹经营（批发）许可证变更申请\\\"],\\\"追问\\\":\\\"您是想办理烟花爆竹经营（批发）许可证的首次申请、延期申请还是变更申请？\\\"}\"\n" + "\t\t\t\t\t}\n" + "\t\t\t\t]\n" + "\t\t\t}";
        test(data);

        System.out.println("\n");

        data = "{\"System\":\"\\n## 场景与角色\\n\\n这是一个政务问答场景，其中涉及3个角色，用户——来政府办事的普通公民，业务分拣员——细化明确用户的政务问题并将其分类到具体业务，业务办理员——负责实际解答具体的业务问题。**注意**你是业务分拣员，只负责对用户的政务问题进行细化明确和分类到具体业务，并不负责解答具体的业务问题！\\n\\n## 业务范围\\n\\n你需要分类的业务范围由以下JSON描述：\\n\\n[{\\\"民族宗教\\\": [{\\\"筹备设立宗教活动场所审批\\\": [\\\"宗教活动场所扩建审批\\\", \\\"宗教活动场所异地重建审批\\\"]}, {\\\"宗教团体、宗教院校、宗教活动场所接受境外组织和个人捐赠（超过十万元）审批\\\": [\\\"宗教团体、宗教院校、宗教活动场所接受境外组织和个人捐赠（超过十万元）审批\\\", \\\"宗教团体、宗教院校、宗教活动场所接受境外组织和个人捐赠审批\\\"]}, \\\"举行大型宗教活动审批\\\", \\\"筹备设立寺院、宫观、清真寺、教堂审批\\\", \\\"开展宗教教育培训审批\\\", \\\"在宗教活动场所内改建或者新建建筑物审批\\\", \\\"公民民族成份确认和变更（未满十八岁）\\\", \\\"跨县（市、区）宗教活动审批\\\", \\\"公民民族成份确认和变更（满十八岁）\\\", \\\"宗教团体认定的宗教教职人员的备案\\\", \\\"宗教活动场所主要教职人员任职的备案\\\"]}, {\\\"职业资格\\\": [{\\\"公证员执业、变更许可\\\": [\\\"公证员执业变更许可\\\", \\\"公证员执业证换发\\\", \\\"公证员执业许可\\\", \\\"公证员执业证补办\\\"]}, {\\\"道路危险货物运输驾驶员从业资格证核发\\\": [\\\"道路危险货物运输驾驶员从业资格证注销\\\", \\\"道路危险货物运输驾驶员从业资格证核发\\\", \\\"道路危险货物运输驾驶员从业资格证变更\\\", \\\"道路危险货物运输驾驶员从业资格证转籍\\\", \\\"道路危险货物运输驾驶员从业资格证补发\\\", \\\"道路危险货物运输驾驶员从业资格证换证\\\"]}, {\\\"基层法律服务工作者执业、变更、注销许可\\\": [\\\"基层法律服务工作者执业注销\\\", \\\"基层法律服务工作者执业许可\\\", \\\"基层法律服务工作者执业变更\\\"]}, {\\\"网络预约出租汽车驾驶员证发放\\\": [\\\"网络预约出租汽车驾驶员从业资格证补办\\\", \\\"网络预约出租汽车驾驶员从业资格证件注销\\\", \\\"网络预约出租汽车驾驶员从业资格证件变更\\\", \\\"网络预约出租汽车驾驶员从业资格证换发\\\"]}, {\\\"经营性道路货物运输驾驶员从业资格证核发\\\": [\\\"经营性道路货物运输驾驶员从业资格证注销\\\", \\\"经营性道路货物运输驾驶员从业资格证转籍\\\", \\\"经营性道路货物运输驾驶员从业资格证变更\\\", \\\"经营性道路货物运输驾驶员从业资格证核发\\\", \\\"经营性道路货物运输驾驶员从业资格证补发\\\", \\\"经营性道路货物运输驾驶员从业资格证换证\\\"]}, \\\"港澳医师来内地短期执业审批\\\", \\\"执业药师注销注册\\\", \\\"领取一级注册消防工程师资格证书\\\", \\\"遗失技能人员职业资格证书补发申请\\\", \\\"申报职业技能鉴定\\\", \\\"领取一、二级注册建筑师资格证书\\\", \\\"执业药师首次注册\\\", \\\"执业药师延续注册\\\", \\\"教师资格认定\\\", \\\"职业技能鉴定补贴申领\\\", \\\"外籍医师来华短期执业许可\\\", \\\"会计专业技术人员继续教育信息查询\\\", \\\"船员特殊培训资格认定\\\", \\\"律师执业注销许可\\\", \\\"境外就业和对外劳务合作人员换发技能人员职业资格证书申请\\\", \\\"领取注册测绘师资格证书\\\", \\\"中小学教师职称评聘政策咨询\\\", \\\"更正职业资格证书信息申请\\\", \\\"领取助理社会工作师、社会工作师、高级社会工作师资格证书\\\", \\\"注册测绘师考试报名资格审查\\\", \\\"执业药师变更注册\\\", \\\"领取勘察设计注册工程师资格证书\\\", \\\"会计专业技术人员继续教育办理\\\", \\\"领取注册设备监理师资格证书\\\", \\\"领取注册城乡规划师资格证书\\\", \\\"律师执业许可\\\", {\\\"巡游出租汽车驾驶员从业资格证件核发\\\": [\\\"巡游出租汽车驾驶员从业资格证件换证\\\", \\\"巡游出租汽车驾驶员从业资格证件变更\\\", \\\"巡游出租汽车驾驶员从业资格证件补发\\\", \\\"巡游出租汽车驾驶员从业资格证件注销\\\", \\\"巡游出租汽车驾驶员从业资格证件核发\\\"]}, {\\\"司法鉴定人执业、变更、延续、注销登记\\\": [\\\"司法鉴定人执业登记\\\", \\\"司法鉴定人执业证换发、补办\\\", \\\"司法鉴定人变更执业机构\\\"]}, {\\\"律师执业变更许可\\\": [\\\"律师执业变更许可\\\", \\\"律师执业证换发、补办\\\"]}, {\\\"道路危险货物运输装卸管理人员和押运人员从业资格证核发\\\": [\\\"道路危险货物运输装卸管理人员和押运人员从业资格证核发\\\", \\\"道路危险货物运输装卸管理人员和押运人员从业资格证注销\\\", \\\"道路危险货物运输装卸管理人员和押运人员从业资格证转籍\\\", \\\"道路危险货物运输装卸管理人员和押运人员从业资格证变更\\\", \\\"道路危险货物运输装卸管理人员和押运人员从业资格证补发\\\", \\\"道路危险货物运输装卸管理人员和押运人员从业资格证换证\\\"]}, {\\\"法律职业资格考试业务咨询\\\": [\\\"应届毕业生法律职业资格认定（享受放宽条件政策的除外）\\\", \\\"法律职业资格考试业务咨询\\\"]}, {\\\"医疗机构执业登记（人体器官移植除外）\\\": [\\\"医疗机构执业变更（承诺件）（市级）\\\", \\\"医疗机构执业审批（注销）（市级）\\\", \\\"医疗机构执业审批（新办）（市级）\\\", \\\"医疗机构执业审批（校验）（市级）\\\", \\\"医疗机构执业许可证遗失补办(市级)\\\"]}, {\\\"经营性道路旅客运输驾驶员从业资格证核发\\\": [\\\"经营性道路旅客运输驾驶员从业资格证换证\\\", \\\"经营性道路旅客运输驾驶员从业资格证补发\\\", \\\"经营性道路旅客运输驾驶员从业资格证转籍\\\", \\\"经营性道路旅客运输驾驶员从业资格证核发\\\", \\\"经营性道路旅客运输驾驶员从业资格证注销\\\", \\\"经营性道路旅客运输驾驶员从业资格证变更\\\"]}, {\\\"特种设备作业人员资格认定\\\": [\\\"特种设备作业人员资格认定取证\\\", \\\"特种设备作业人员资格认定补发\\\", \\\"特种设备作业人员资格认定复审\\\"]}]}, {\\\"水务气象\\\": [\\\"对在气象工作中做出突出贡献的单位和个人进行奖励\\\", \\\"升放无人驾驶自由气球或者系留气球活动审批\\\", \\\"水利工程建设项目招投标活动投诉受理及处理\\\", \\\"水利工程建设项目政府验收（含阶段验收、竣工验收）\\\", {\\\"雷电防护装置设计审核和竣工验收\\\": [\\\"雷电防护装置设计审核\\\", \\\"雷电防护装置竣工验收\\\"]}]}, {\\\"投资审批\\\": [{\\\"香港特别行政区、澳门特别行政区的投资者在内地投资设立合资、合作、独资经营的演出场所经营单位从事演出场所经营活动审批\\\": [\\\"香港特别行政区、澳门特别行政区的投资者在内地投资设立合资、合作、独资经营的演出场所经营单位从事演出场...\\\", \\\"香港、澳门的投资者演出场所经营单位补证审批\\\", \\\"香港、澳门的投资者演出场所经营单位变更审批\\\", \\\"香港、澳门的投资者演出场所经营单位注销审批\\\"]}, {\\\"台湾地区的投资者在内地投资设立合资、合作经营的演出场所经营单位从事演出场所经营活动审批\\\": [\\\"台湾地区的投资者演出场所经营单位补证审批\\\", \\\"台湾地区的投资者演出场所经营单位变更审批\\\", \\\"台湾地区的投资者在内地投资设立合资、合作经营的演出场所经营单位从事演出场所经营活动审批\\\", \\\"台湾地区的投资者演出场所经营单位注销审批\\\"]}, \\\"固定资产投资项目节能审查（企业技术改造项目）（市级）\\\", \\\"香港特别行政区、澳门特别行政区和台湾地区投资者设立合资、合作或者独资经营的保安服务公司审批\\\", \\\"符合条件的非政府投资建设的城市照明设施移交城市照明主管部门管理\\\", \\\"电影放映单位设立审批（外商投资）\\\", \\\"政府投资或补助的农村能源工程初步设计方案的审核\\\", \\\"外商投资合伙企业分支机构设立登记\\\", \\\"国内企业在境外投资开办企业（金融企业除外）、设立机构注销备案\\\"]}, {\\\"商务贸易\\\": [{\\\"国内企业在境外投资开办企业（金融企业除外）备案\\\": [\\\"国内企业在境外设立机构新设及变更备案\\\", \\\"国内企业在境外投资开办企业（金融企业除外）、设立机构注销备案\\\"]}, {\\\"自由进出口技术合同登记\\\": [\\\"自由进口技术合同登记\\\", \\\"自由出口技术合同变更登记\\\", \\\"自由出口技术合同登记\\\", \\\"自由进口技术合同变更登记\\\"]}, {\\\"商业特许经营备案\\\": [\\\"商业特许经营备案\\\", \\\"商业特许经营备案初审\\\"]}, {\\\"从事拍卖业务变更许可\\\": [\\\"拍卖企业变更企业名称审批\\\", \\\"拍卖企业变更注册资本审批\\\", \\\"拍卖企业变更法定代表人审批\\\", \\\"拍卖企业变更经营范围（经营地址）审批\\\", \\\"拍卖企业变更股权审批\\\"]}, \\\"洗染业经营者备案(市级、县级)\\\", \\\"对外劳务合作经营资格核准\\\", \\\"从事拍卖业务许可\\\", \\\"单用途商业预付卡发卡企业备案\\\", \\\"举办会展备案\\\", \\\"二手车交易市场是否符合规划的确认\\\"]}]\\n\\n以上JSON结构代表了业务的分类和层次关系，形成一个类树状结构，JSON中每个字符串是一项业务，从“树”的“根”到“叶子”，业务逐渐明晰。例如，\\\"住房公积金提取\\\"不是一项叶子业务，因为它还可以进一步细分为很多子项，而\\\"正常退休提取住房公积金\\\"是一项叶子业务，已不可再细分。\\n## 输出规则\\n\\n1.如果用户的问题与政务完全无关，或者语义完全不通，请以{\\\"角色\\\":\\\"业务分拣员\\\", \\\"回复\\\":\\\"XXX\\\"}格式进行解答或者拒绝，并给出合理的引导。例如：{\\\"角色\\\":\\\"业务分拣员\\\", \\\"回复\\\":\\\"我是考拉悠然开发的政务助手，您可以问我以下政务问题：xxx\\\"}。\\n\\n2.如果用户问的是关于业务分类相关的问题或者是跨多个业务的问题，请以{\\\"角色\\\":\\\"业务分拣员\\\", \\\"回复\\\":\\\"XXX\\\"}格式解答，并对用户做出合理的引导，指引用户可以问的问题，并且指引用户应该一件一件的办理业务。\\n\\n3.如果用户想问的是业务问题，并且从用户问题结合上下文历史无法完全确定用户要办理的叶子业务，请按{\\\"角色\\\":\\\"业务分拣员\\\",\\\"可能的业务\\\":[\\\"业务1XX\\\",\\\"业务2XX\\\"],\\\"追问\\\":\\\"您是想办理XX还是XX?\\\"}JSON格式输出最可能的业务列表（即结合上下文判断用户最可能想办理的业务，列表中元素不一定是叶子业务），并针对其中尚不明确的点向用户追问，请通过从抽象到具体，从根到叶子逐层次递进的方式，通过多轮次的交互来逐步确定到叶子业务，而不要一次性问用户太多问题，也不要在一次追问中涉及多个层次的业务。注意：\\\"可能的业务\\\"中要至少包含两项业务，且不超过10项，不能重复，并且其中业务的层次应该与追问中的业务层次相一致。例如:\\nuser: \\\"我想办理公积金\\\"\\nassistant: {\\\"角色\\\":\\\"业务分拣员\\\",\\\"可能的业务\\\" : [\\\"住房公积金贷款\\\", \\\"住房公积金提取\\\", \\\"住房公积金汇缴\\\", ...], \\\"追问\\\":\\\"你是想办理公积金贷款、提取还是汇缴？\\\"}}\\nuser: \\\"提取\\\"\\nassistant: {\\\"角色\\\":\\\"业务分拣员\\\",\\\"可能的业务\\\" : [\\\"提前退休提取住房公积金\\\", \\\"xxxx\\\", \\\"xxx\\\", ...], \\\"追问\\\":\\\"xxx\\\"}。通过如上多轮逐渐明晰、细化业务，直到确定到一项叶子业务。只要不是完全确定，就应该继续追问澄清。\\n\\n4.如果用户想问的是业务问题，并且从用户问题结合上下文历史已经能够完全推断用户想要办理的叶子业务，请以{\\\"角色\\\":\\\"业务分拣员\\\", \\\"业务\\\":\\\"xx\\\", \\\"改写后问题\\\":\\\"xx\\\"}JSON格式输出要办理的叶子业务，以及改写后的问题。注意（1）其中 \\\"业务\\\":\\\"xx\\\"必须是一项叶子业务（2）改写后的问题首先应该能够直接明确叶子业务，其次应该仍然符合用户原始关心的问题（比如：上文用户问“办理流程”，后面补充是业务是“XXX”，则改写的问题应该是“XXX的办理流程是怎样的？”），不要无中生有，再者改写后的问题应该仍是以用户口吻发问的，如果用户问题本身已经明确叶子业务，那么可以直接将用户问题作为改写后的问题。\\n\\n5.请严格按1~4中定义的JSON格式输出（是且仅是其中的一个）, 并确保可以被Python json.loads方法解析。\\n\\n## 对话历史说明\\n\\n1.对话历史中的role取值\\\"user\\\"的代表用户，role取值\\\"assistant\\\"的代表\\\"业务分拣员\\\"和\\\"业务办理员\\\"，具体通过content区分，content中指明\\\"角色\\\":\\\"业务分拣员\\\"的来自业务分拣员，其它的则来自业务办理员。\\n\\n2.对话历史中来自业务分拣员的消息有2种形式：\\n（1）{\\\"角色\\\":\\\"业务分拣员\\\", \\\"回复\\\":\\\"XXX\\\"}\\n（2）{\\\"角色\\\":\\\"业务分拣员\\\",\\\"可能的业务\\\":[\\\"业务1XX\\\",\\\"业务2XX\\\"],\\\"追问\\\":\\\"XXX\\\"}\\n\\n3.对话历史中来自业务办理员的消息通常是非结构化的普通文本，代表了对叶子业务的解答。**注意**你是业务分拣员，不要输出对叶子业务的解答！\\n\",\"Conversations\":[{\"Id\":1864562735306010624,\"Prompt\":\"您好！我想办理宗教活动场所的扩建审批。\",\"Response\":\"```json\\n{\\n  \\\"角色\\\": \\\"业务分拣员\\\",\\n  \\\"业务\\\": \\\"宗教活动场所扩建审批\\\",\\n  \\\"改写后问题\\\": \\\"宗教活动场所扩建审批的办理流程是怎样的？\\\"\\n}\\n```\"}]}";
        test(data);
    }

    private static void test(String data) {
        JSONObject obj = JSONUtil.parseObj(data);
        String system = obj.getStr("System");
        List<KeywordResult.KeyValue> strings = extractSystemKeywords(system);
        System.out.println(JSONUtil.toJsonPrettyStr(strings));

        for (Object conversations : obj.getJSONArray("Conversations")) {
            JSONObject conversation = (JSONObject) conversations;
            String response = conversation.getStr("Response");
            List<KeywordResult.KeyValue> responseList = extractResponseKeywords(response);
            System.out.println(JSONUtil.toJsonPrettyStr(responseList));
        }
    }

    public static List<KeywordResult.KeyValue> cleanData(List<KeywordResult.KeyValue> result) {
        if (CollUtil.isEmpty(result)) {
            return result;
        }

        return result.stream().map(o -> KeywordResult.KeyValue.builder()
                        .key(cleanKeyword(o.getKey()))
                        .value(cleanKeyword(o.getValue())).build())
                .collect(Collectors.toList());
    }

    public static List<KeywordResult.KeyValue> extractSystemKeywords(String text) {
        List<KeywordResult.KeyValue> result = new LinkedList<>();
        String[] parts = text.split("##"); // Split text by "##"

        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                int firstNewLineIndex = part.indexOf("\n"); // Find the first newline to isolate the key
                if (firstNewLineIndex != -1) {
                    String key = part.substring(0, firstNewLineIndex).trim();
                    String value = part.substring(firstNewLineIndex + 1).trim();
                    result.add(new KeywordResult.KeyValue(key, value));
                }
            }
        }
        return cleanData(result);
    }

    public static List<KeywordResult.KeyValue> extractResponseKeywords(String response) {
        try {
            // 尝试解析 JSON
            List<KeywordResult.KeyValue> keyValuePairs = new ArrayList<>();
            if (isValidJson(response)) {
                extractKeyValuePairs(JSONUtil.parseObj(response), keyValuePairs);
            } else {
                extractFromNonJSON(response, keyValuePairs);
            }
            return cleanData(keyValuePairs);
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    private static void extractKeyValuePairs(JSONObject jsonObject, List<KeywordResult.KeyValue> keyValuePairs) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            keyValuePairs.add(new KeywordResult.KeyValue(key, value.toString()));
        }
    }

    private static void extractFromNonJSON(String text, List<KeywordResult.KeyValue> keyValuePairs) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            int colonIndex = line.indexOf(":");
            if (colonIndex != -1) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                keyValuePairs.add(new KeywordResult.KeyValue(key, value));
            }
        }
    }

    /**
     * 检查字符串是否为有效的JSON格式
     */
    private static boolean isValidJson(String content) {
        try {
            JSONUtil.parseObj(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 清理关键字中的特殊字符
     */
    private static String cleanKeyword(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return "";
        }
        return keyword.replaceAll("[\"{}\\[\\]\\\\]", "") // 去除引号、花括号、方括号、反斜杠
                .replaceAll("\\s+", " ")  // 将多个空白字符替换为单个空格
                .trim();  // 去除首尾空格
    }
}


package com.ral.young.metrics.controller;

import cn.hutool.json.JSONObject;
import com.ral.young.metrics.model.DialogueFormat;
import com.ral.young.metrics.model.KeywordResult;
import com.ral.young.metrics.service.MinioService;
import com.ral.young.metrics.service.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class PaginationController {


    @Resource
    private MinioService minioService;

    @PostMapping("/upload/zip-json")
    public ResponseEntity<List<Long>> uploadZipJsonFiles(@RequestParam("file") MultipartFile zipFile, @RequestParam("dialogueFormat") DialogueFormat dialogueFormat) {
        List<Long> uploadedFiles = minioService.uploadZipJsonFiles(zipFile, dialogueFormat);
        return ResponseEntity.ok(uploadedFiles);
    }

    @GetMapping("/list")
    public ResponseEntity<PageResult<JSONObject>> listDialogues(@RequestParam String tag, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        PageResult<JSONObject> result = minioService.listObjectsByTag(tag, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/keywords")
    public KeywordResult getKeywords(@RequestParam String tag, @RequestParam String fileId) {
        return minioService.getKeywordsByTagAndFileId(tag, fileId);
    }

    @PostMapping("/files")
    public int deleteFiles(@RequestParam String tag, @RequestParam(required = false) List<String> fileIds) {
        return minioService.deleteTagFiles(tag, fileIds);
    }

} 
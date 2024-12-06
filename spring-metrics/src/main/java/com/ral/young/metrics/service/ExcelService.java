package com.ral.young.metrics.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.google.common.collect.Lists;
import com.ral.young.metrics.excel.DeviceData;
import com.ral.young.metrics.excel.DeviceSaveData;
import com.ral.young.metrics.excel.OrgData;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExcelService implements ApplicationRunner {

    private List<OrgData> orgDataList;

    public void uploadExcel(MultipartFile file) throws IOException {
        // 创建一个监听器来处理Excel数据
        AnalysisEventListener<DeviceData> listener = new AnalysisEventListener<DeviceData>() {
            private final List<DeviceSaveData> dataList = new ArrayList<>();

            @Override
            public void invoke(DeviceData data, AnalysisContext context) {
                // 解析组织机构路径,格式如: 成都市/武侯区
                String orgPath = data.getOrganizationStructure();
                if (orgPath != null && !orgPath.isEmpty()) {
                    // 按/分割组织路径
                    String[] orgNames = orgPath.split("/");
                    StringBuilder orgCode = new StringBuilder();
                    Long parentId = 0L;

                    // 逐级查找组织ID并构建orgCode
                    for (String orgName : orgNames) {
                        Long finalParentId = parentId;
                        OrgData currentOrg = orgDataList.stream()
                                .filter(org -> org.getOrgName().equals(orgName.trim()) &&
                                        (finalParentId == null ? org.getParentId() == null : org.getParentId().equals(finalParentId)))
                                .findFirst()
                                .orElse(null);

                        if (currentOrg != null) {
                            if (orgCode.length() > 0) {
                                orgCode.append("_");
                            }
                            orgCode.append(currentOrg.getId());
                            parentId = currentOrg.getId();
                        }
                    }

                    dataList.add(DeviceSaveData.builder().deviceCode(data.getDeviceCode())
                            .orgCode(orgCode.toString())
                            .externalOrgCode(data.getExternalOrgCode()).build());
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 这里可以将解析的数据保存到数据库
                log.info("解析完成，共解析到 {} 条数据 , 详细数据: {}", dataList.size(), JSONUtil.toJsonStr(dataList));
            }
        };

        EasyExcel.read(file.getInputStream(), DeviceData.class, listener).sheet().doRead();
    }

    public void downloadExcel(String fileName) {
        List<DeviceData> data = fetchDataForExcel(); // 假设这个方法从数据库或其他地方获取数据
        EasyExcel.write(fileName, DeviceData.class).registerWriteHandler(new SimpleColumnWidthStyleStrategy(20)).sheet("设备数据").doWrite(data);
    }

    private List<DeviceData> fetchDataForExcel() {
        // 这里应该实现数据的获取逻辑
        return Lists.newArrayList(new DeviceData()); // 示例数据
    }

    public void downloadTemplate(HttpServletResponse response) {
        try {
            List<OrgData> orgData = orgDataList;
            orgData = orgData.stream().limit(30).collect(Collectors.toList());
            // 将组织数据转换为下拉列表格式
            List<String> orgDropdownList = new ArrayList<>();
            for (OrgData org : orgData) {
                // 根据parentId构建完整的组织路径
                StringBuilder path = new StringBuilder();
                OrgData current = org;
                List<String> pathParts = new ArrayList<>();

                // 向上遍历构建完整路径
                while (current != null) {
                    pathParts.add(0, current.getOrgName());
                    final Long parentId = current.getParentId();
                    current = orgData.stream()
                            .filter(o -> o.getId().equals(parentId))
                            .findFirst()
                            .orElse(null);
                }

                // 用/连接各级组织名称
                path.append(String.join("/", pathParts));
                orgDropdownList.add(path.toString());
            }

            // 转换为数组
            String[] organizationStructures = orgDropdownList.toArray(new String[0]);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("设备导入模板.xlsx", "UTF-8"));
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), DeviceData.class)
                    .registerWriteHandler(new DropdownBoxWriteHandler(2, organizationStructures))
                    .build();
            WriteSheet writeSheet = EasyExcel.writerSheet("设备导入模板模板").build();
            excelWriter.write(null, writeSheet);
            excelWriter.finish();
        } catch (IOException e) {
            throw new RuntimeException("生成Excel文件失败", e);
        }
    }

    private List<OrgData> generateOrgDataForDropdown() {
        List<OrgData> orgDataList = new ArrayList<>();

        // 四川省
        Long sichuanId = IdUtil.getSnowflakeNextId();
        orgDataList.add(new OrgData(sichuanId, 0L, "四川省"));

        // 成都市
        Long chengduId = IdUtil.getSnowflakeNextId();
        orgDataList.add(new OrgData(chengduId, sichuanId, "成都市"));
        // 成都市的区县
        String[] chengduDistricts = {"锦江区", "青羊区", "金牛区", "武侯区", "成华区", "龙泉驿区", "新都区", "温江区", "双流区", "郫都区"};
        for (String district : chengduDistricts) {
            Long districtId = IdUtil.getSnowflakeNextId();
            orgDataList.add(new OrgData(districtId, chengduId, district));
            // 每个区下设5个街道
            for (int i = 1; i <= 5; i++) {
                Long streetId = IdUtil.getSnowflakeNextId();
                orgDataList.add(new OrgData(streetId, districtId, district + i + "号街道"));
                // 每个街道下设4个社区
                for (int j = 1; j <= 4; j++) {
                    Long communityId = IdUtil.getSnowflakeNextId();
                    orgDataList.add(new OrgData(communityId, streetId, district + i + "号街道" + j + "号社区"));
                }
            }
        }

        // 绵阳市
        Long miangyangId = IdUtil.getSnowflakeNextId();
        orgDataList.add(new OrgData(miangyangId, sichuanId, "绵阳市"));
        // 绵阳市的区县
        String[] mianyangDistricts = {"涪城区", "游仙区", "安州区", "三台县", "盐亭县", "梓潼县", "北川县", "平武县"};
        for (String district : mianyangDistricts) {
            Long districtId = IdUtil.getSnowflakeNextId();
            orgDataList.add(new OrgData(districtId, miangyangId, district));
            // 每个区下设4个街道
            for (int i = 1; i <= 4; i++) {
                Long streetId = IdUtil.getSnowflakeNextId();
                orgDataList.add(new OrgData(streetId, districtId, district + i + "号街道"));
                // 每个街道下设3个社区
                for (int j = 1; j <= 3; j++) {
                    Long communityId = IdUtil.getSnowflakeNextId();
                    orgDataList.add(new OrgData(communityId, streetId, district + i + "号街道" + j + "号社区"));
                }
            }
        }

        // 德阳市
        Long deyangId = IdUtil.getSnowflakeNextId();
        orgDataList.add(new OrgData(deyangId, sichuanId, "德阳市"));
        // 德阳市的区县
        String[] deyangDistricts = {"旌阳区", "罗江区", "广汉市", "什邡市", "绵竹市", "中江县"};
        for (String district : deyangDistricts) {
            Long districtId = IdUtil.getSnowflakeNextId();
            orgDataList.add(new OrgData(districtId, deyangId, district));
            // 每个区下设4个街道
            for (int i = 1; i <= 4; i++) {
                Long streetId = IdUtil.getSnowflakeNextId();
                orgDataList.add(new OrgData(streetId, districtId, district + i + "号街道"));
                // 每个街道下设3个社区
                for (int j = 1; j <= 3; j++) {
                    Long communityId = IdUtil.getSnowflakeNextId();
                    orgDataList.add(new OrgData(communityId, streetId, district + i + "号街道" + j + "号社区"));
                }
            }
        }

        return orgDataList;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        orgDataList = generateOrgDataForDropdown();
        log.info("初始化数据完成");
    }
}


class DropdownBoxWriteHandler implements SheetWriteHandler {

    private final int columnIndex;
    private final String[] dropdownItems;

    public DropdownBoxWriteHandler(int columnIndex, String[] dropdownItems) {
        this.columnIndex = columnIndex;
        this.dropdownItems = dropdownItems;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // Not used
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000000, columnIndex, columnIndex);
        DataValidationConstraint constraint = helper.createExplicitListConstraint(dropdownItems);
        DataValidation dataValidation = helper.createValidation(constraint, addressList);
        dataValidation.setShowErrorBox(true);
        sheet.addValidationData(dataValidation);
    }
}

class CustomReadListener extends AnalysisEventListener<DeviceData> {

    private int currentRow = 2; // Excel行号通常从2开始，排除表头

    private final List<String> errors = new ArrayList<>();

    @Override
    public void invoke(DeviceData data, AnalysisContext context) {
        // 检查数据有效性
        if ("大华".equals(data.getPlatformIdentifier()) && (data.getExternalOrgCode() == null || data.getExternalOrgCode().isEmpty())) {
            errors.add("第 " + currentRow + " 行错误：接入平台为大华时，外部组织编码必填");
        }
        currentRow++; // 处理完一行数据后，行号增加
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!errors.isEmpty()) {
            throw new RuntimeException("Excel数据校验失败：" + String.join(", ", errors));
        }
    }
}
package com.ral.young.metrics.service;

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
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    public void uploadExcel(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), DeviceData.class, new CustomReadListener()).sheet().doRead();
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
            String[] organizationStructures = {"四川省",
                    "四川省/成都市", "四川省/成都市/锦江区", "四川省/成都市/青羊区", "四川省/成都市/金牛区", "四川省/成都市/武侯区",
                    "四川省/绵阳市", "四川省/绵阳市/涪城区", "四川省/绵阳市/游仙区", "四川省/绵阳市/安州区",
                    "四川省/德阳市", "四川省/德阳市/旌阳区", "四川省/德阳市/中江县", "四川省/德阳市/罗江区",
                    "四川省/泸州市", "四川省/泸州市/江阳区", "四川省/泸州市/纳溪区", "四川省/泸州市/龙马潭区"};

            String[] platformTags = {"大华", "海康", "阿启视"};
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("设备导入模板.xlsx", "UTF-8"));
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), DeviceData.class)
                    .registerWriteHandler(new DropdownBoxWriteHandler(1, platformTags))
                    .registerWriteHandler(new DropdownBoxWriteHandler(2, organizationStructures))
                    .build();
            WriteSheet writeSheet = EasyExcel.writerSheet("设备导入模板模板").build();
            excelWriter.write(null, writeSheet);
            excelWriter.finish();
        } catch (IOException e) {
            throw new RuntimeException("生成Excel文件失败", e);
        }
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
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, columnIndex, columnIndex);
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
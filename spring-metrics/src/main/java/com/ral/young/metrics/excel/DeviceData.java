package com.ral.young.metrics.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ColumnWidth(25)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceData {
    @ExcelProperty("设备编码")
    private String deviceCode;

    @ExcelProperty("接入平台标识")
    private String platformIdentifier;

    @ExcelProperty(value = "组织结构", converter = ExcelDropDownConverter.class)
    private String organizationStructure;

    @ExcelProperty("外部组织编码")
    private String externalOrgCode;
}
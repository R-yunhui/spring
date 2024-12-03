package com.ral.young.metrics.controller;

import com.ral.young.metrics.service.ExcelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Resource
    private ExcelService excelService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            excelService.uploadExcel(file);
            return ResponseEntity.ok("文件上传成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        excelService.downloadTemplate(response);
    }
} 
package com.ral.young.metrics.controller;

import com.ral.young.metrics.service.MinioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author renyunhui
 * @description 这是一个FileUploadController类
 * @date 2024-12-04 11-19-44
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Resource
    private MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        return ResponseEntity.ok(minioService.asyncUploadFile(files));
    }
}

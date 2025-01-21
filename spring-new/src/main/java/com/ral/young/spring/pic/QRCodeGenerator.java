package com.ral.young.spring.pic;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author renyunhui
 * @description 生成二维码 demo
 * @date 2025-01-20 11-39-21
 * @since 1.0.0
 */
public class QRCodeGenerator {

    private static final String QR_CODE_IMAGE_PATH = "./MyQRCode.png";

    public static void generateQRCodeImage(String baseUrl, int width, int height, String filePath) throws WriterException, IOException {
        // 添加时间戳到URL
        String urlWithTimestamp = baseUrl + "?timestamp=" + System.currentTimeMillis();

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(urlWithTimestamp, BarcodeFormat.QR_CODE, width, height, hints);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    public static void main(String[] args) {
        try {
            generateQRCodeImage("http://10.10.1.162:8080/web/sayHello", 350, 350, QR_CODE_IMAGE_PATH);
            System.out.println("QR Code has been generated at: " + QR_CODE_IMAGE_PATH);
        } catch (WriterException e) {
            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }
    }
}

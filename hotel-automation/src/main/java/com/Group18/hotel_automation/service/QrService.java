package com.Group18.hotel_automation.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QrService {

    public String generateQrBase64(String text) {
        try {

            QRCodeWriter writer = new QRCodeWriter();

            BitMatrix matrix = writer.encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    250,
                    250
            );

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(matrix, "PNG", stream);

            return Base64.getEncoder().encodeToString(stream.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("QR generation failed");
        }
    }
}

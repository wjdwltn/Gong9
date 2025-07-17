package com.gg.gong9.global.utils.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.gg.gong9.global.exception.ExceptionMessage;
import com.gg.gong9.global.exception.exceptions.S3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${BUCKET_NAME}")
    private String bucket;

    @Value("${BASE_URL}")
    private String baseUrl;

    public String uploadFile(String folderName, MultipartFile file) {
        String fileName = folderName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
            return baseUrl + "/" + fileName;

        } catch (IOException e) {
            throw new S3Exception(ExceptionMessage.S3_UPLOAD_FAILED);
        }
    }
}

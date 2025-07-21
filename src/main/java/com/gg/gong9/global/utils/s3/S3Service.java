package com.gg.gong9.global.utils.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.gg.gong9.global.exception.exceptions.s3.S3Exception;
import com.gg.gong9.global.exception.exceptions.s3.S3ExceptionMessage;
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

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.base-url}")
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
            throw new S3Exception(S3ExceptionMessage.PRODUCT_NOT_FOUND);
        }
    }
}

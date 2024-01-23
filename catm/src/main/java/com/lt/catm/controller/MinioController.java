package com.lt.catm.controller;

import com.lt.catm.annotation.JwtAuth;
import com.lt.catm.auth.AuthUser;
import com.lt.catm.config.MinioConfig;
import com.lt.catm.response.ResponseModel;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name = "Minio")
@RestController
@RequestMapping(path = "/minio")
public class MinioController {
    public MinioConfig minioConfig;
    public MinioClient minioClient;

    @Autowired
    public MinioController(MinioConfig minioConfig, MinioClient minioClient) throws Exception{
        this.minioConfig = minioConfig;
        this.minioClient = minioClient;
        // 创建桶
        boolean exists = this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.bucketName).build());
        if (!exists) {
            this.minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.bucketName).build());
        }
    }

    @GetMapping("/upload")
    @Operation(description = "获取临时minio上传url")
    public Mono<ResponseModel<String>> policy(@JwtAuth AuthUser user) {
        String key = UUID.randomUUID().toString();
        try {
            return Mono.just(
                    new ResponseModel<>(
                            minioClient.getPresignedObjectUrl(
                                    GetPresignedObjectUrlArgs.builder()
                                            .bucket(minioConfig.bucketName)
                                            .method(Method.PUT)
                                            .expiry(60 * 30)
                                            .object(key)
                                            .build())));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}

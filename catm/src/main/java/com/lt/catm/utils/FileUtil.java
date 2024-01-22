package com.lt.catm.utils;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    @Value("${minio.bucket}")
    private static String bucketName;

    @Value("${minio.endpoint}")
    private static String endpoint;

//    @Resource
    private final MinioClient minioClient = SpringUtil.getBean(MinioClient.class);

    /**
     * 上传文件到minio服务器
     * @param content 文件内容
     * @param path 文件上传路径
     * @param type 文件类型
     * @return 完整的可访问路径
     */
    public static String fileUploader(byte[] content, String path, String type) {
        MinioClient minioClient = SpringUtil.getBean(MinioClient.class);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName) // bucket 必须传递
                    .contentType(type) //文件类型-->text/plain
                    .object(path) // 相对路径作为 key
                    .stream(new ByteArrayInputStream(content), content.length, -1) // 文件内容
                    .build());
        } catch (ErrorResponseException | InvalidResponseException | InvalidKeyException | InsufficientDataException |
                 InternalException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            throw new RuntimeException(e);
        }
        return path.startsWith("/") ? endpoint + path : endpoint + "/" + path;
    }

    /**
     * 删除文件
     * @param path 文件路径
     * @return 成功标志
     */
    public static Boolean fileDelete(String path) {
        MinioClient minioClient = SpringUtil.getBean(MinioClient.class);
        boolean flag;
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path)
                    .build());
            flag = true;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            flag = false;
            log.info("delete file err : ",e);
        }
        return flag;
    }
}

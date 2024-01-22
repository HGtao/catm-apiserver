package com.lt.catm.utils;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
     *
     * @param content 文件内容
     * @param path    文件上传路径
     * @param type    文件类型
     * @return 完整的可访问路径
     */
    public static Mono<String> fileUploader(Flux<DataBuffer> dataBufferFlux/*,byte[] content, String path, String type*/) {
        String path = "/test";
        String endpoint = "http://124.222.37.186:9002";
        String bucketName = "test";
        return DataBufferUtils.join(dataBufferFlux)
                .flatMap(dataBuffer -> {
                    try {
                        MinioClient minioClient = SpringUtil.getBean(MinioClient.class);
                        // 仅当Minio上传被订阅时, 将ByteBuffer转换成InputStream，并执行上传
                        return Mono.fromCallable(() -> {
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(dataBuffer.asByteBuffer().array());
                            minioClient.putObject(PutObjectArgs.builder()
                                    .bucket(bucketName) // bucket 必须传递
                                    .contentType("text/plain") // 文件类型
                                    .object(path) // 相对路径作为 key
                                    .stream(inputStream, -1, 10485760) // 文件内容
                                    .build());
                            // 直接关闭 inputStream，因为 putObject 是阻塞调用，完成后再进行关闭操作
                            inputStream.close();
                            // 返回文件路径
                            return path.startsWith("/") ? endpoint + path : endpoint + "/" + path;
                        }).subscribeOn(Schedulers.boundedElastic()); // 使用支持阻塞操作的调度器
                    }catch (Exception e){
                        // 释放DataBuffer资源并处理异常
                        DataBufferUtils.release(dataBuffer);
                        return Mono.error(new RuntimeException("Error occurred while uploading the file", e));
                    }
                });
    }

    /**
     * 删除文件
     *
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
            log.info("delete file err : ", e);
        }
        return flag;
    }
}

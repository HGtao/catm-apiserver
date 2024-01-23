package com.lt.catm.utils;

import cn.hutool.core.util.IdUtil;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.PostConstruct;
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

@Component
public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    @Value("${minio.bucketName}")
    private String bucket;
    private static String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;
    private static String endpointUrl;

    //    @Resource
    private static MinioClient minioClient;

    @PostConstruct
    public void init() {
        FileUtil.bucketName = bucket;
        FileUtil.endpointUrl = endpoint;
        FileUtil.minioClient = SpringUtil.getBean(MinioClient.class);
    }

    /**
     * 上传文件到minio服务器
     * @return 完整的可访问路径
     */
    public static Mono<String> fileUploader(Flux<DataBuffer> dataBufferFlux,String type,String fileName) {
        String path = IdUtil.simpleUUID() + "-" + fileName;
        return DataBufferUtils.join(dataBufferFlux) // 合并为一个完整的数据缓冲区
                .flatMap(dataBuffer -> {
                    try {
                        // 仅当Minio上传被订阅时, 将ByteBuffer转换成InputStream，并执行上传
                        return Mono.fromCallable(() -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes); //读取数据到字节数组
                            DataBufferUtils.release(dataBuffer); //释放资源
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                            minioClient.putObject(PutObjectArgs.builder()
                                    .bucket(bucketName) // bucket 必须传递
                                    .contentType(type) // 文件类型 "text/plain"
                                    .object(path) // 相对路径作为 key
                                    .stream(inputStream, -1, 10485760) // 文件内容
                                    .build());
                            // 直接关闭 inputStream，因为 putObject 是阻塞调用，完成后再进行关闭操作
                            inputStream.close();
                            // 返回文件路径
                            return path.startsWith("/") ? endpointUrl + path : endpointUrl + "/" + path;
                            // 使用支持阻塞操作的调度器,Schedulers.boundedElastic()是为了非阻塞式回退策略准备的调度器，它为可能的阻塞任务（如I/O操作）提供一个弹性线程池
                        }).subscribeOn(Schedulers.boundedElastic());
                    } catch (Exception e) {
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
    public static Mono<Boolean> fileDelete(String path) {
       return Mono.fromCallable(() -> {
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(path)
                        .build());
                return true;
            } catch (Exception e) {
                log.info("delete file err : {}", e.getMessage(),e);
                return false;
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}

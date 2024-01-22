package com.lt.catm.controller;

import cn.hutool.core.io.IoUtil;
import com.lt.catm.response.ResponseModel;
import com.lt.catm.utils.FileUtil;
import com.lt.catm.utils.RedisKeyUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

/**
 * @author yuwu
 */
@Tag(name = "KeyPair")
@RestController
public class KeyPairController {
    private final ReactiveRedisOperations<String, String> redisOperations;

    @Autowired
    public KeyPairController(ReactiveRedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    /**
     * 获取加密密码的公钥.
     *
     * @return Mono<ResponseModel < KeyPairSchema>> 公钥
     */
    @GetMapping("/keypair")
    @Operation(description = "获取随机公钥")
    public Mono<ResponseModel<KeyPairSchema>> generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 随机uuid保存密钥对
        String kid = UUID.randomUUID().toString();
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        // 响应数据
        KeyPairSchema data = new KeyPairSchema(kid, publicKey);
        String key = RedisKeyUtil.getPrivateKeyCacheKey(kid);
        return redisOperations
                .opsForValue()
                .set(key, privateKey, Duration.ofMinutes(5))
                .map(result -> {
                    if (!result) {
                        return ResponseModel.error(500,"keypair generate error");
                    }
                    return ResponseModel.success(data);
                });
    }


    @Data
    @AllArgsConstructor
    public static class KeyPairSchema {
        // kid查找密钥对
        String kid;
        // 公钥内容
        String publicKey;
    }


}

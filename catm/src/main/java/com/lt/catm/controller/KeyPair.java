package com.lt.catm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

import com.lt.catm.ResponseModel;
import com.lt.catm.schema.KeyPairSchema;
import com.lt.catm.common.RedisKeyUtil;


@RestController
public class KeyPair {
    private final ReactiveRedisOperations<String, String> redisOperations;

    @Autowired
    public KeyPair(ReactiveRedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    @GetMapping("login/key")
    public Mono<ResponseModel<KeyPairSchema>> generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 随机uuid保存密钥对
        String kid = UUID.randomUUID().toString();
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        // 响应数据
        KeyPairSchema data = new KeyPairSchema(kid, publicKey);
        ResponseModel<KeyPairSchema> response = new ResponseModel<>(data);
        // 设置密钥的过期时间5分钟, 并返回数据
        String key = RedisKeyUtil.getPrivateKeyCacheKey(kid);
        return redisOperations.opsForValue().set(key, privateKey, Duration.ofMinutes(5)).thenReturn(response);
    }
}
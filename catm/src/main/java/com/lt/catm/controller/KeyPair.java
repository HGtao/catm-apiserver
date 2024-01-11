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
import java.util.HashMap;
import java.util.UUID;

import com.lt.catm.Constants;


@RestController
public class KeyPair {
    private final ReactiveRedisOperations<String, String> redisOperations;

    @Autowired
    public KeyPair(ReactiveRedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    @GetMapping("login/key")
    public Mono<HashMap<String, String>> generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 随机uuid保存密钥对
        String uuid = UUID.randomUUID().toString();
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        // 响应数据 TODO ylei 封装为实体返回
        HashMap<String, String> response = new HashMap<>();
        response.put("id", uuid);
        response.put("public_key", publicKey);
        // 设置密钥的过期时间5分钟, 并返回数据
        String key = uuid + ":" + Constants.APP_NAME;
        return redisOperations.opsForValue().set(key, privateKey, Duration.ofMinutes(5)).thenReturn(response);
    }
}
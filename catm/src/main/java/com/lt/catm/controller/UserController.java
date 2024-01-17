package com.lt.catm.controller;

import com.lt.catm.annotation.JwtAuth;
import com.lt.catm.common.RedisKeyUtil;
import com.lt.catm.exceptions.HttpException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import com.lt.catm.models.User;
import com.lt.catm.response.ResponseModel;
import com.lt.catm.repositories.UserRepository;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;


@RestController
@RequestMapping(path = "/user")
public class UserController {
    @Resource
    UserRepository repository;

    private final ReactiveRedisOperations<String, String> redisOperations;

    @Autowired
    public UserController(ReactiveRedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    // 创建密码编码器
    private final Argon2PasswordEncoder passwordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public final static HttpException DecodePasswordError = new HttpException(HttpStatus.BAD_REQUEST, 1000, "decode password err");

    /**
     * 创建用户的请求体.
     */
    public static class CreateUserSchema {
        public String username;
        // 密码前端公钥机密了的
        public String password;
        // 密钥对ID
        public String kid;
    }

    /***
     * 解密前端加密了的密码.
     * @param password 加密后的密码.
     * @param kid 密钥ID.
     * @return 密码.
     */
    public Mono<String> decodePassword(String password, String kid) {
        String key = RedisKeyUtil.getPrivateKeyCacheKey(kid);
        return redisOperations.opsForValue().get(key).switchIfEmpty(
                    Mono.error(DecodePasswordError)
                ).flatMap(privateKey -> {
                    try {
                        PrivateKey priKey = KeyFactory.getInstance("RSA")
                                .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
                        Cipher cipher = Cipher.getInstance("RSA");
                        cipher.init(Cipher.DECRYPT_MODE, priKey);
                        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(password));
                        return Mono.just(new String(decryptedBytes));
                    } catch (Exception e) {
                        return Mono.error(DecodePasswordError);
                    }
                });
    }

    @Operation(
            description = "注册账号-(1000: decode password err)"
    )
    @PostMapping("/register")
    public Mono<ResponseModel<User>> create(@RequestBody UserController.CreateUserSchema userSchema) {
        Mono<User> monoUser = decodePassword(userSchema.password, userSchema.kid).flatMap(password ->
                Mono.just(new User(userSchema.username, passwordEncoder.encode(password))));
        return monoUser.flatMap(user -> repository.save(user).thenReturn(new ResponseModel<>(user)));
        // TODO ylei 签发jwt
    }

    @Operation(
            description = "获取登录用户信息"
    )
//    @JwtAuth
    @GetMapping("/test")
    public Mono<ResponseModel<User>> read() {
        // TODO HG 快实现
        return Mono.just(new ResponseModel<>());
    }
}

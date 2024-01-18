package com.lt.catm.controller;

import com.lt.catm.auth.AuthUser;
import com.lt.catm.auth.Jwt;
import com.lt.catm.common.Constants;
import com.lt.catm.common.RedisKeyUtil;
import com.lt.catm.exceptions.HttpException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;

@RestController
@RequestMapping(path = "/user")
public class UserController {
    @Resource UserRepository repository;
    private final ReactiveRedisOperations<String, String> redisOperations;

    @Autowired
    public UserController(ReactiveRedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    // 创建密码编码器
    private final Argon2PasswordEncoder passwordEncoder =
            Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    public static final HttpException DecodePasswordError =
            new HttpException(HttpStatus.BAD_REQUEST, 1000, "decode password err");

    /** 创建用户的请求体. */
    public static class CreateUserSchema {
        public String username;
        // 密码前端公钥机密了的
        public String password;
        // 密钥对ID
        public String kid;
    }

    /** 用户登录请求体 */
    public static class LoginUserSchema {
        // 用户名
        public String username;
        // 密码前端公钥机密了的
        public String password;
        // 密钥对ID
        public String kid;
    }

    /** User响应信息去掉关键信息. */
    public static class ResponseUser {
        public int id;
        public String username;
        public LocalDateTime created_at;
        public LocalDateTime updated_at;

        public ResponseUser(User user) {
            this.id = user.id;
            this.username = user.username;
            this.created_at = user.created_at;
            this.updated_at = user.updated_at;
        }
    }

    /***
     * 解密前端加密了的密码.
     * @param password 加密后的密码.
     * @param kid 密钥ID.
     * @return 密码.
     */
    public Mono<String> decodePassword(String password, String kid) {
        String key = RedisKeyUtil.getPrivateKeyCacheKey(kid);
        return redisOperations
                .opsForValue()
                .get(key)
                .switchIfEmpty(Mono.error(DecodePasswordError))
                .flatMap(
                        privateKey -> {
                            try {
                                PrivateKey priKey =
                                        KeyFactory.getInstance("RSA")
                                                .generatePrivate(
                                                        new PKCS8EncodedKeySpec(
                                                                Base64.getDecoder()
                                                                        .decode(privateKey)));
                                Cipher cipher = Cipher.getInstance("RSA");
                                cipher.init(Cipher.DECRYPT_MODE, priKey);
                                return Mono.just(
                                        new String(
                                                cipher.doFinal(
                                                        Base64.getDecoder().decode(password))));
                            } catch (Exception e) {
                                return Mono.error(DecodePasswordError);
                            }
                        });
    }

    @Operation(description = "注册账号-(1001: decode password err)")
    @PostMapping("/register")
    public Mono<ResponseModel<ResponseUser>> create(
            @RequestBody UserController.CreateUserSchema userSchema, ServerHttpResponse response) {
        Mono<User> monoUser =
                decodePassword(userSchema.password, userSchema.kid)
                        .flatMap(
                                password ->
                                        Mono.just(
                                                new User(
                                                        userSchema.username,
                                                        passwordEncoder.encode(password))));
        return monoUser.flatMap(user -> repository.save(user))
                .flatMap(
                        user -> {
                            AuthUser authUser = new AuthUser(user.getId());
                            ResponseCookie jwtCookie =
                                    ResponseCookie.from(
                                                    Constants.COOKIES_JWT_NAME,
                                                    Jwt.create(authUser))
                                            .httpOnly(true)
                                            .maxAge(Duration.ofDays(7))
                                            .path("/")
                                            .build();
                            response.addCookie(jwtCookie);
                            return Mono.just(new ResponseModel<>(new ResponseUser(user)));
                        });
    }

    @Operation(description = "退出登录")
    @PostMapping("/logout")
    public Mono<ResponseModel<Object>> logout(ServerHttpResponse response) {
        ResponseCookie jwtCookie =
                ResponseCookie.from(Constants.COOKIES_JWT_NAME, "")
                        .httpOnly(true)
                        .maxAge(0)
                        .path("/")
                        .build();
        response.addCookie(jwtCookie);
        return Mono.just(new ResponseModel<>());
    }
}

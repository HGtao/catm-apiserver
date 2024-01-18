package com.lt.catm.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lt.catm.annotation.JwtAuth;
import com.lt.catm.auth.AuthUser;
import com.lt.catm.auth.Jwt;
import com.lt.catm.common.Constants;
import com.lt.catm.utils.RedisKeyUtil;
import com.lt.catm.exceptions.HttpException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.format.annotation.DateTimeFormat;
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

@Tag(name = "用户")
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
            new HttpException(HttpStatus.BAD_REQUEST, 1001, "decode password err");

    public static final HttpException NotFoundUserError =
            new HttpException(HttpStatus.NOT_FOUND, 1002, "user not found");

    public static final HttpException PasswordError =
            new HttpException(HttpStatus.BAD_REQUEST, 1003, "password err");

    /** 创建用户的请求体. */
    public static class CreateUserSchema {
        public String username;
        // 密码前端公钥加密
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

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public LocalDateTime created_at;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
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

    @PostMapping("/register")
    @Operation(description = "注册账号-(1001: decode password err)")
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

    @PostMapping("/logout")
    @Operation(description = "退出登录")
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

    @GetMapping("")
    @Operation(description = "获取当前用户登录信息-(1002 用户不存在)")
    public Mono<ResponseModel<ResponseUser>> auth(@JwtAuth AuthUser user) {
        Mono<User> userMono = repository.findById(Long.valueOf(user.getId()));
        return userMono.switchIfEmpty(Mono.error(NotFoundUserError))
                .flatMap(userModel -> Mono.just(new ResponseModel<>(new ResponseUser(userModel))));
    }

    @PostMapping("/login")
    @Operation(description = "用户登录-(1002 用户不存在 1003 密码错误)")
    public Mono<ResponseModel<ResponseUser>> login(
            @RequestBody UserController.LoginUserSchema loginUserSchema,
            ServerHttpResponse response) {
        // 解密前端加密的密码
        Mono<String> passwordMono = decodePassword(loginUserSchema.password, loginUserSchema.kid);
        // username条件查询用户
        User probe = new User();
        probe.setUsername(loginUserSchema.username);
        Example<User> query =
                Example.of(
                        probe,
                        ExampleMatcher.matching()
                                .withMatcher(
                                        "username",
                                        ExampleMatcher.GenericPropertyMatchers.exact()));
        Mono<User> userMono =
                repository.findOne(query).switchIfEmpty(Mono.error(NotFoundUserError));
        // 对比密码
        // TODO ylei 限制次数防止爆破接口
        return Mono.zip(userMono, passwordMono)
                .flatMap(
                        tuple -> {
                            User user = tuple.getT1();
                            String password = tuple.getT2();
                            if (passwordEncoder.matches(password, user.password)) {
                                // 下发jwt
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
                            } else {
                                return Mono.error(PasswordError);
                            }
                        });
    }
}

package com.lt.catm.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import com.lt.catm.models.User;
import com.lt.catm.ResponseModel;
import com.lt.catm.repositories.UserRepository;


@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Resource
    UserRepository repository;

    @PostMapping("/register")
    public Mono<ResponseModel<User>> create(@RequestBody User user) {
        // TODO ylei 解密前端密码, 加密密码入库
        return repository.save(user).thenReturn(new ResponseModel<>(user));
    }
}

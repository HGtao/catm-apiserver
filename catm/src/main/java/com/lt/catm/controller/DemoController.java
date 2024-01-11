package com.lt.catm.controller;

import com.lt.catm.service.DemoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class DemoController {

    @Autowired
    private DemoServiceImpl demoService;

    @GetMapping("/demo")
    public Flux<String> demo() {
        demoService.testR2dbcEntityTemplate();
        demoService.testDatabaseClient();
        demoService.testDemoRepository();
        return Flux.just("1","2");
    }
}

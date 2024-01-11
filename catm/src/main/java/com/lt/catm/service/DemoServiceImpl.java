package com.lt.catm.service;

import com.lt.catm.entity.User;
import jakarta.annotation.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl {
    @Resource
    R2dbcEntityTemplate r2dbcEntityTemplate; //CRUD API

    @Resource
    DatabaseClient databaseClient; //数据库客户端

    public void testR2dbcEntityTemplate() {
        //Query By Criteria: QBC

        //1.构建查询条件
        Criteria empty = Criteria.empty();

        //2.构建查询语句
        empty.and("id").is(1L)
                .and("name").is("张三")
                .and("age").is(18);

        //3.封装到查询对象中
        Query query = Query.query(empty);

        //4.执行查询
        r2dbcEntityTemplate.select(query, User.class).subscribe(System.out::println);

    }
}

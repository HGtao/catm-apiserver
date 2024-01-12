package com.lt.catm.service;

import com.lt.catm.entity.User;
import com.lt.catm.repositories.DemoRepository;
import jakarta.annotation.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class DemoServiceImpl {
    @Resource
    R2dbcEntityTemplate r2dbcEntityTemplate; //CRUD API 只能单表

    @Resource
    DatabaseClient databaseClient; //数据库客户端

    @Resource
    DemoRepository demoRepository;

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



    public void testDatabaseClient() {
        databaseClient
                .sql("SELECT * FROM user where id=?")
                .bind(0, 1L)
                .fetch()//抓取数据
                .all() //返回是一个map
                .map(map -> {
                    User user = new User();
                    user.setId((Long) map.get("id"));
                    user.setUserName((String) map.get("user_name"));
                    user.setAge((Integer) map.get("age"));
                    return user;
                }).subscribe(System.out::println);
    }

    public void testDemoRepository() {
        //使用内置的api
        demoRepository.findAll().subscribe(System.out::println);

        //使用自定义的api
        demoRepository.findAllByIdAndUserNameLike(1L,"张三%").subscribe(System.out::println);

        //使用自定义的api,注解方式
        demoRepository.selectAll(List.of(1L),"张%").subscribe(System.out::println);

        demoRepository.selectJoin(1L).subscribe(x-> System.out.println("===>"+x));
    }
}

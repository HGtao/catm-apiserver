package com.lt.catm.repositories;

import com.lt.catm.entity.User;
import com.lt.catm.entity.UserType;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;

/**
 * ReactiveCrudRepository<User, Long> :仓库 User：实体类，Long：主键类型
 */
@Repository
public interface DemoRepository extends R2dbcRepository<User, Long> {

    //方法起名的方式：单表条件查询
    // findAllByIdInAndUserNameLike：多了个in就是传递 id集合，这里参数不然乱定义，否则启动会报错
    Flux<User> findAllByIdAndUserNameLike(Long id, String userName);


    //自定义注解方式
    @Query("select * from user where id in (:ids) and user_name like :userName")
    Flux<User> selectAll(List<Long> ids, String userName);


    //连表查询 加上 @Modifying 表示增删改操作
    @Query("SELECT u.*,ut.user_type,ut.create_time FROM `user` u INNER JOIN " +
            "user_type ut ON u.type_id = ut.id WHERE u.id = :userId")
    Flux<UserType> selectJoin(@Param("userId") Long userId);


}

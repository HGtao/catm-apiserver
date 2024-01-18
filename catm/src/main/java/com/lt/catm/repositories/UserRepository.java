package com.lt.catm.repositories;

import com.lt.catm.models.User;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface UserRepository extends R2dbcRepository<User, Integer> {

}

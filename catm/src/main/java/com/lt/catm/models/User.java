package com.lt.catm.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Data
@Table("user")
@NoArgsConstructor
public class User {

    @Id
    @Column("id")
    public Integer id;

    @Column("username")
    public String username;

    @Column("password")
    public String password;

    @CreatedDate
    @Column("created_at")
    public LocalDateTime created_at;

    @LastModifiedDate
    @Column("updated_at")
    public LocalDateTime updated_at;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

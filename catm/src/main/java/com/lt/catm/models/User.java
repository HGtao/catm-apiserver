package com.lt.catm.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Data
@Table("user")
@NoArgsConstructor
public class User {

    @Id
    @Column("id")
    @ReadOnlyProperty
    public int id;

    @Column("username")
    public String username;

    @Column("password")
    public String password;

    @Column("created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    public LocalDateTime created_at;

    @LastModifiedDate
    @Column("updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public LocalDateTime updated_at;
}

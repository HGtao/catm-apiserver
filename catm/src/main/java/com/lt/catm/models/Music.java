package com.lt.catm.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

public class Music {
    // 数据ID
    @Id
    @Column("id")
    public Integer id;

    @CreatedDate
    @Column("created_at")
    public LocalDateTime created_at;

    @LastModifiedDate
    @Column("updated_at")
    public LocalDateTime updated_at;
    // 音乐上传者
    @Column("creator")
    public String creator;
    // 音乐播放地址

    // 封面地址
    // 音乐作者 多个作者怎么处理
    // 歌词
}

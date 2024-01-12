-- 初始化数据库
CREATE DATABASE IF NOT EXISTS catm;

-- 创建用户表
create table user
(
    id         int auto_increment comment '用户ID'
        primary key,
    username   varchar(128)                             not null comment '用户名',
    password   varchar(128)                             not null comment '密码',
    created_at datetime(6) default CURRENT_TIMESTAMP(6) not null,
    updated_at datetime(6) default CURRENT_TIMESTAMP(6) not null on update CURRENT_TIMESTAMP(6),
    constraint username
        unique (username)
)
    comment '用户表';


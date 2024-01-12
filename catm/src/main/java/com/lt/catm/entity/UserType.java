package com.lt.catm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("user_type")
public class UserType {
    @Id
    private Long id;

    private Integer userType;

    //时间 使用 LocalDateTime 也行
    private Instant createTime;

    private User user;
}

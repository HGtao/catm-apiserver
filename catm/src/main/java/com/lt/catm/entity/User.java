package com.lt.catm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("catm_user")
public class User {
    private Long id;
    private String userName;
    private Integer age;
}

package com.lt.catm.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthUser {
    // 用户ID
    public int id;

    public AuthUser(int id) {
        this.id = id;
    }
}

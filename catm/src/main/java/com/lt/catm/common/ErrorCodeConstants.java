package com.lt.catm.common;

import com.lt.catm.exceptions.HttpException;
import org.springframework.http.HttpStatus;

/**
 * 错误枚举码
 * @author zt
 */
public interface ErrorCodeConstants {
    /**
     * HttpStatus httpStatus, int code, String msg
     */
    HttpException JWT_AUTH_ERROR = new HttpException(HttpStatus.UNAUTHORIZED, 1000, "jwt error");

}

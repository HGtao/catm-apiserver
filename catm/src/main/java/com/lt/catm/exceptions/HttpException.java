package com.lt.catm.exceptions;

import org.springframework.http.HttpStatus;


public class HttpException extends Exception{
    public int code = 5000;
    public HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    public String msg;

    public HttpException() {

    }

    public HttpException(int code) {
        this.code = code;
    }

    public HttpException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public HttpException(HttpStatus httpStatus, int code, String msg) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.msg = msg;
    }
}

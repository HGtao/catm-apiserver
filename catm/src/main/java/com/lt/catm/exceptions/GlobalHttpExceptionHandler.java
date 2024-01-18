package com.lt.catm.exceptions;

import com.lt.catm.response.ResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalHttpExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ResponseModel<String>> handle(HttpException httpException) {
        return new ResponseEntity<>(new ResponseModel<>(httpException.code, httpException.msg, null), httpException.httpStatus);
    }
}
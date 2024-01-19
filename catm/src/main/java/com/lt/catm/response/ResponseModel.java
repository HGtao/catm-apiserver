package com.lt.catm.response;

import lombok.Data;
import reactor.core.publisher.Mono;

import java.io.Serializable;


@Data
public class ResponseModel<T> implements Serializable {
    // 业务码, 如果没设置默认200代表正常
    private Integer code = 200;
    // 返回消息默认为null
    private String msg = null;
    // 数据
    private T data = null;

    public ResponseModel() {
    }

    public ResponseModel(T data) {
        this.data = data;
    }

    public static <T> Mono<ResponseModel<T>> successReactor(T data) {
        ResponseModel<T> responseModel = new ResponseModel<>();
        responseModel.code = 200;
        responseModel.msg = "success";
        responseModel.data = data;
        return Mono.just(responseModel);
    }

    public static <T> ResponseModel<T> success(T data) {
        ResponseModel<T> responseModel = new ResponseModel<>();
        responseModel.code = 200;
        responseModel.msg = "success";
        responseModel.data = data;
        return responseModel;
    }

    public static <T> ResponseModel<T> error(Integer code, String msg) {
        ResponseModel<T> responseModel = new ResponseModel<>();
        responseModel.code = code;
        responseModel.msg = msg;
        return responseModel;
    }

    public static <T> Mono<ResponseModel<T>> errorReactor(Integer code, String msg) {
        ResponseModel<T> responseModel = new ResponseModel<>();
        responseModel.code = code;
        responseModel.msg = msg;
        return Mono.just(responseModel);
    }


    public ResponseModel(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}

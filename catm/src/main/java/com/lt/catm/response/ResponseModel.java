package com.lt.catm.response;

import lombok.Data;


@Data
public class ResponseModel<T> {
    // 业务码, 如果没设置默认200代表正常
    public int code = 200;
    // 返回消息默认为null
    public String msg = null;
    // 数据
    public T data = null;

    public ResponseModel() {
    }

    public ResponseModel(T data) {
        this.data = data;
    }

    public ResponseModel(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}

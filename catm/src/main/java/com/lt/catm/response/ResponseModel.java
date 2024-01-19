package com.lt.catm.response;

import lombok.Data;
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

    public static <T> ResponseModel<T> success(T data) {
        return new ResponseModel<>(200, "success", data);
    }

    public static <T> ResponseModel<T> error(Integer code, String msg) {
        return new ResponseModel<>(code, msg, null);
    }

    public ResponseModel(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}

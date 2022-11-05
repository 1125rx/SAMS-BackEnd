package com.sxh.usercenter.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: usercenter
 * @description: 通用返回类
 * @author: SXH
 * @create: 2022-11-05 11:12
 **/
@Data
public class BaseResponse<T> implements Serializable {

    private int code;
    private T data;
    private String message;
    private String description;

    public BaseResponse(int code,T data,String message,String description){
        this.code=code;
        this.message=message;
        this.data=data;
        this.description=description;
    }
    public BaseResponse(int code, T data, String message) {
        this(code, data, message, "");
    }

    public BaseResponse(int code, T data) {
        this(code, data, "", "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }

}

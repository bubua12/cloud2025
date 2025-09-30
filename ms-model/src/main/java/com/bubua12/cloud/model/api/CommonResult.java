package com.bubua12.cloud.model.api;

import lombok.Getter;
import lombok.Setter;

/**
 * 统一标准返回
 *
 * @author bubua12
 * @since 2025/9/30 13:14
 */
@SuppressWarnings("unused")
@Setter
@Getter
public class CommonResult<T> {
    private String code;
    /** 结果状态 ,具体状态码参见枚举类 ResponseCodeEnum.java*/
    private String message;
    private T data;
    private Boolean success;
    private long timestamp;

    /**
     * CommonResult返回值增强：增加 traceId
     * 说明：traceId不要在这里的构造方法进行赋值，这里是拿到 aop里面用
     */
    private String traceId;

    public CommonResult() {
        this.timestamp = System.currentTimeMillis();
    }

    public CommonResult(Boolean success, String code, String message, T data) {
        this();
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(Boolean.TRUE, ResponseCodeEnum.RC200.getCode(), ResponseCodeEnum.RC200.getMessage(), data);
    }

    public static <T> CommonResult<T> fail(String code, String message) {
        return new CommonResult<>(Boolean.FALSE, code, message, null);
    }

    /**
     * 使用 isSuccess() 代替 getSuccess()
     */
    public Boolean isSuccess() {
        return success;
    }
}
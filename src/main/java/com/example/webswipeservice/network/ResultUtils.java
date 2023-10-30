package com.example.webswipeservice.network;

/**
 * 响应工具类
 */
public class ResultUtils {

    /**
     * 返回成功
     */
    public static <T> BaseResponse<T> success(String successMsg, T data) {
        return new BaseResponse<T>(0, successMsg, data);
    }

    /**
     * 返回错误
     */
    public static <T> BaseResponse<T> error(int code, String errorMsg) {
        return new BaseResponse<T>(code, errorMsg, null);
    }
}

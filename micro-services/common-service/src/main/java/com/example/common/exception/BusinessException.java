package com.example.common.exception;

public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private String code;
    
    // 构造方法
    public BusinessException(String message) {
        super(message);
        this.code = "400"; // 默认错误码
    }
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    // getter 方法
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
}
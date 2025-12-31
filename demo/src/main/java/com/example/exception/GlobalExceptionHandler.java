package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * 全局异常处理器
 * 使用 @ControllerAdvice 注解，捕获所有控制器抛出的异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理自定义业务异常
     * @param ex 业务异常对象
     * @param request 请求对象
     * @return 统一错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        // 从请求中获取路径
        String path = extractPathFromRequest(request);
        
        // 创建错误响应对象
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getCode(),
            ex.getMessage(),
            path
        );
        
        // 返回400 Bad Request状态码
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 处理所有其他未捕获的异常
     * @param ex 异常对象
     * @param request 请求对象
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        // 从请求中获取路径
        String path = extractPathFromRequest(request);
        
        // 创建错误响应对象
        ErrorResponse errorResponse = new ErrorResponse(
            "500",
            "系统内部错误", // 隐藏具体错误信息，提高安全性
            path
        );
        
        // 打印异常堆栈，便于调试
        ex.printStackTrace();
        
        // 返回500 Internal Server Error状态码
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * 从WebRequest中提取请求路径
     * @param request WebRequest对象
     * @return 请求路径
     */
    private String extractPathFromRequest(WebRequest request) {
        // WebRequest.getDescription(false) 返回格式："uri=/path"
        String description = request.getDescription(false);
        if (description.startsWith("uri=")) {
            return description.substring(4); // 去掉"uri="前缀
        }
        return description;
    }
}
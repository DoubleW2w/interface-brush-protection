package com.doublew2w.interfaceBrushProtection.handler;

import com.doublew2w.interfaceBrushProtection.constant.Result;
import com.doublew2w.interfaceBrushProtection.constant.ResultCode;
import com.doublew2w.interfaceBrushProtection.exception.CommonException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author DoubleW2w
 * @description
 * @created 2023/4/5 12:55
 * @project interface-brush-protection
 */
@RestControllerAdvice
public class BaseExceptionHandler {
    /**
     * 通用自定义异常捕获(登录状态/权限验证、接口防刷等)
     *
     * @return
     */
    @ExceptionHandler(value = CommonException.class)
    public Result commonException(CommonException exception) {
        if (exception.getMessage() != null && exception.getMessage().equals(ResultCode.ACCESS_FREQUENT.getMessage())) {
            // 访问过于频繁
            return Result.failed(ResultCode.ACCESS_FREQUENT);
        }
        if (exception.getMessage() != null) {
            // 给定异常信息
            return new Result(10001, exception.getMessage(), false);
        }
        // 请求失败
        return Result.failed();
    }

    /**
     * 服务器异常统一返回
     *
     * @return 结果
     */
    @ExceptionHandler(value = Exception.class)
    public Result error() {
        return Result.error();
    }
}

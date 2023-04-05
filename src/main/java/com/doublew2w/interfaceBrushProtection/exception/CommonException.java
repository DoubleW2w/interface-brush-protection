package com.doublew2w.interfaceBrushProtection.exception;

import com.doublew2w.interfaceBrushProtection.constant.ResultCode;

/**
 * @author DoubleW2w
 * @description
 * @created 2023/4/5 12:54
 * @project interface-brush-protection
 */
public class CommonException extends Exception {
    public CommonException(String context) {
        super(context);
    }

    public CommonException(ResultCode context) {
        super(context.getMessage());
    }
}

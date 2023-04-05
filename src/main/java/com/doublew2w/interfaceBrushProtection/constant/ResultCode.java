package com.doublew2w.interfaceBrushProtection.constant;

import lombok.Getter;

/**
 * @author DoubleW2w
 * @description
 * @created 2023/4/5 0:21
 * @project interface-brush-protection
 */
@Getter
public enum ResultCode {
    /**
     * 错误
     */
    ERROR(-1, "错误"),
    /**
     * 操作成功
     */
    SUCCESS(0, "操作成功"),
    /**
     * 操作失败
     */
    FAILED(1, "操作失败"),
    /**
     * 警告
     */
    WARNING(2, "警告"),
    /**
     * 访问过于频繁
     */
    ACCESS_FREQUENT(50001, "访问过于频繁"),
    ;
    /**
     * 值
     */
    private final int value;
    /**
     * 描述
     */

    private final String message;

    ResultCode(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static boolean success(int code) {
        return code == SUCCESS.value;
    }

    public static boolean success(ResultCode errorCode) {
        return errorCode == SUCCESS;
    }

    public static boolean failed(ResultCode errorCode) {
        return errorCode == FAILED;
    }

    public static boolean error(ResultCode errorCode) {
        return errorCode == ERROR;
    }
}

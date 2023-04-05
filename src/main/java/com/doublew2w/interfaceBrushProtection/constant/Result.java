package com.doublew2w.interfaceBrushProtection.constant;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author DoubleW2w
 * @description
 * @created 2023/4/5 0:20
 * @project interface-brush-protection
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> extends BaseEntity {
    /**
     * 响应结果错误码
     */
    private int result;
    /**
     * 响应结果备注
     */
    private String resultNote;
    /**
     * 响应结果返回
     */
    private T data;

    public Result() {
        this(ResultCode.FAILED);
    }

    private Result(ResultCode code) {
        this(code, code.getMessage(), null);
    }

    private Result(ResultCode code, String resultNote) {
        this(code, resultNote, null);
    }

    private Result(ResultCode code, T data) {
        this(code, code.getMessage(), data);
    }

    private Result(ResultCode code, String resultNote, T data) {
        this(code.getValue(), resultNote, data);
    }

    public Result(int result, String resultNote, T data) {
        this.result = result;
        this.resultNote = resultNote;
        this.data = data;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getResultNote() {
        return resultNote;
    }

    public void setResultNote(String resultNote) {
        this.resultNote = resultNote;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS);
    }

    public static <T> Result<T> error() {
        return new Result<>(ResultCode.ERROR);
    }

    public static <T> Result<T> success(String resultNote) {
        return new Result<>(ResultCode.SUCCESS, resultNote);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, data);
    }

    public static <T> Result<T> success(T data, String resultNote) {
        return new Result<>(ResultCode.SUCCESS, resultNote, data);
    }

    public static <T> Result<T> failed() {
        return new Result<>();
    }

    public static <T> Result<T> failed(ResultCode code) {
        return new Result<>(code);
    }

    public static <T> Result<T> failed(ResultCode code, String resultNote) {
        return new Result<>(code, resultNote, null);
    }

    public static <T> Result<T> failed(String resultNote) {
        return new Result<>(ResultCode.FAILED, resultNote);
    }

    public static <T> Result<T> failed(String resultNote, T data) {
        return new Result<>(ResultCode.FAILED, resultNote, data);
    }

    public static <T> Result<T> failed(ResultCode code, String resultNote, T data) {
        return new Result<>(code, resultNote, data);
    }

    public static <T> Result<T> warning(String resultNote) {
        return new Result<>(ResultCode.WARNING, resultNote);
    }
}

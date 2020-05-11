package com.foochane.awpay.common.result;



import java.io.Serializable;

public final class Result<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 信息
     */
    private String msg;

    /**
     * 返回结果实体
     */
    private T data = null;


    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T extends Serializable> Result<T> error() {
        return new Result<T>(-1, "error", null);
    }

    public static <T extends Serializable> Result<T> error(int code,String msg) {
        return new Result<T>(code, msg, null);
    }

    public static <T extends Serializable> Result<T> error(int code, String msg,T data) {
        return new Result<T>(code, msg, data);
    }

    public static <T extends Serializable> Result<T> success() {
        return new Result<T>(1, "SUCCESS", null);
    }

    public static <T extends Serializable> Result<T> success(int code, String msg) {
        return new Result<T>(code, msg, null);
    }

    public static <T extends Serializable> Result<T> success(int code, String msg, T data) {
        return new Result<T>(code, msg, data);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result [code=" + code + ", msg=" + msg + ", data=" + data + "]";
    }

}

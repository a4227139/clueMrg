package com.wa.cluemrg.response;


public class ResponseResult<T> {
    private T object;
    private boolean success;
    private String message;

    public ResponseResult() {
        this.success = true;
        this.message = "";
    }

    public ResponseResult(T object) {
        this();
        this.object = object;
    }

    public ResponseResult(T object, boolean success, String message) {
        this.object = object;
        this.success = success;
        this.message = message;
    }

    public T getObject() {
        return this.object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFail(String message) {
        this.success = false;
        this.message = "操作失败！错误信息：" + message;
    }
}

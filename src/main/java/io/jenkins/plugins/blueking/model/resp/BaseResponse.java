package io.jenkins.plugins.blueking.model.resp;

import com.alibaba.fastjson2.JSON;

public class BaseResponse<T> {
    private Boolean result;
    private Integer code;
    private String message;

    private T data;

    public boolean isSuccess() {
        return Boolean.TRUE.equals(result);
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getStrCode() {
        return String.valueOf(code);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

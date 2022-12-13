package org.wwg.network.base;

import com.google.gson.annotations.SerializedName;

public class BaseResponse<T> {
    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

    public boolean isValid() {
        return code >= 1;
    }
}

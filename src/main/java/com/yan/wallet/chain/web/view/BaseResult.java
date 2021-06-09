package com.yan.wallet.chain.web.view;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class BaseResult<T> implements Serializable {

    private T data;

    private Integer code;

    private String message;


    private Map<String,Object> extendObj;

    private boolean success = false;

    public BaseResult(T data, Integer code, String message, Map<String, Object> extendObj, boolean success) {
        this.data = data;
        this.code = code;
        this.message = message;
        this.extendObj = extendObj;
        this.success = success;
    }

    public BaseResult() {
    }

    public BaseResult(T data) {
        this.data = data;
        code = 200;
        message = "";
        success = true;
    }

    public BaseResult(T data, String message) {
        this.data = data;
        code = 200;
        success = true;
        this.message = message;
    }

    public BaseResult makeSuccessResult() {
        code = 200;
        message = "success";
        success = true;
        return this;
    }

    public BaseResult makeSuccessResult(T data) {
        this.data = data;
        code = 200;
        message = "success";
        success = true;
        return this;
    }

    public BaseResult makeFailedResultWithData(T data) {
        this.data = data;
        code = -1;
        message = "failed";
        success = false;
        return this;
    }

    public BaseResult makeFailedResult() {
        code = -1;
        message = "failed";
        success = false;
        return this;
    }

    public BaseResult makeFailedResult(String message) {
        code = -1;
        this.message = message;
        success = false;
        return this;
    }

    public BaseResult makeFailedResult(Integer code, String message) {
        this.code = code;
        this.message = message;
        success = false;
        return this;
    }

    public void setExtendObj(String key, Object value) {
        if (this.extendObj == null) {
            this.extendObj = new HashMap<>();
        }

        this.extendObj.put(key, value);
    }
}

package com.nix.jingxun.addp.rpc.common;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author keray
 * @date 2018/12/08 13:36
 */
/*
{
    "code": "ERROR",
    "error": {
        "code": "TIMEOUT",
        "exception": {
            "@type": "java.util.concurrent.TimeoutException",
            "stackTrace": [
                {
                    "className": "com.nix.jingxun.addp.rpc.producer.TestProxy",
                    "fileName": "TestProxy.java",
                    "lineNumber": 20,
                    "methodName": "main",
                    "nativeMethod": false
                }
            ]
        }
    }
}


* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RPCResponse implements Serializable {
    public enum ResponseCode{
        SUCCESS,
        ERROR
    }
    public enum ResponseError{
        TIMEOUT,
        EXCEPTION
    }
    private ResponseCode code;
    private SuccessResult result;
    private ErrorResult error;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SuccessResult{
        private Class clazz;
        private Object data;

        public void setClazz(String clazz) {
            try {
                this.clazz = Class.forName(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class ErrorResult{
        ResponseError code;
        Throwable exception;
    }
}

package com.nix.jingxun.addp.web.exception;

/**
 * @author keray
 * @date 2019/05/13 18:05
 */
public class WebRunException extends RuntimeException{
    private Code code;

    public WebRunException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }
}

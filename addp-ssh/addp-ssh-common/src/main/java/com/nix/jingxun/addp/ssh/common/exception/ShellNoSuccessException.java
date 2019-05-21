package com.nix.jingxun.addp.ssh.common.exception;

/**
 * @author keray
 * @date 2019/05/21 18:16
 */
public class ShellNoSuccessException extends RuntimeException{
    public ShellNoSuccessException(Throwable throwable) {
        super(throwable);
    }
    public ShellNoSuccessException(String msg) {
        super(msg);
    }
    public ShellNoSuccessException(String msg,Throwable throwable) {
        super(msg,throwable);
    }

}

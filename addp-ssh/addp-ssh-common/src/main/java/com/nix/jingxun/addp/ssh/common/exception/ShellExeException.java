package com.nix.jingxun.addp.ssh.common.exception;

/**
 * @author keray
 * @date 2019/05/12 16:24
 */
public class ShellExeException extends RuntimeException{
    public ShellExeException(Throwable throwable) {
        super(throwable);
    }
    public ShellExeException(String msg) {
        super(msg);
    }
    public ShellExeException(String msg,Throwable throwable) {
        super(msg,throwable);
    }
}

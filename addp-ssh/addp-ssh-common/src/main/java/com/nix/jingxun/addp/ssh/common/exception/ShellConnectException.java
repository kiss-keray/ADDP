package com.nix.jingxun.addp.ssh.common.exception;

import java.net.ConnectException;

/**
 * @author keray
 * @date 2019/05/12 15:37
 */
public class ShellConnectException extends ConnectException {

    public ShellConnectException(String message) {
        super(message);
    }
}

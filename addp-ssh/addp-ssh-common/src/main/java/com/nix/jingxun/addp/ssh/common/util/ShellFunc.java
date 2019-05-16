package com.nix.jingxun.addp.ssh.common.util;

/**
 * @author keray
 * @date 2019/05/16 16:33
 */
public interface ShellFunc<T> {
    void accept(T result, String msg);
}

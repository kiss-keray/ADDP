package com.nix.jingxun.addp.ssh.common.util;

import com.jcraft.jsch.UserInfo;

/**
 * @author keray
 * @date 2018/12/04 下午3:25
 */
public class SSHUser implements UserInfo {
    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean promptPassword(String s) {
        return false;
    }

    @Override
    public boolean promptPassphrase(String s) {
        return false;
    }

    @Override
    public boolean promptYesNo(String s) {
        if (s.contains("The authenticity of host")) {
            return true;
        }
        return true;
    }

    @Override
    public void showMessage(String s) {

    }
}

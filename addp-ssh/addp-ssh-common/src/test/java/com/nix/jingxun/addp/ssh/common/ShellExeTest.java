package com.nix.jingxun.addp.ssh.common;

import com.jcraft.jsch.JSchException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import org.junit.Test;

/**
 * @author keray
 * @date 2019/05/10 22:36
 */
public class ShellExeTest {

    private ShellExe shellExe = ShellExe.connect("59.110.234.213","root","Kiss4400");

    public ShellExeTest() throws JSchException {
    }

    @Test
    public void lsTest() {
        shellExe.execute("ls", System.out::println);
    }

    @Test
    public void topTest() {
        shellExe.execute("top", System.out::println);
    }
}

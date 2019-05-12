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

    public ShellExeTest() throws Exception {
    }

    @Test
    public void lsTest() {
        shellExe.syncExecute("ls", System.out::println)
        .syncExecute("cd /usr/", System.out::println)
        .syncExecute("ls", System.out::println);
    }


    @Test
    public void topTest() {
        shellExe.AsyncExecute("top",System.out::println);
    }

    public static void main(String[] args) throws Exception {
        ShellExe shellExe = new ShellExeTest().shellExe;
        while (true) {
            byte[] b = new byte[1024];
            System.in.read(b);
            String command = new String(b);
            System.out.println(command.replaceAll("[\\W]",""));
            shellExe.AsyncExecute(command.replaceAll("[\\W]",""),System.out::print);
        }


    }
}

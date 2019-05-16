package com.nix.jingxun.addp.ssh.common;

import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author keray
 * @date 2019/05/10 22:36
 */
public class ShellExeTest {

    private ShellExe shellExe = ShellExe.connect("59.110.234.213", "root", "Kiss4400");

    public ShellExeTest() throws Exception {
    }

    @Test
    public void lsTest() {
        shellExe.syncExecute("cd /usr/addp/",System.out::println)
                .syncExecute("git clone http://git.ceemoo.com:10086/ceemoo/cmcore.git", System.out::println)
                .syncExecute("xxxxx", System.out::println)
                .syncExecute("xxxxxx",System.out::println)
        .close();
    }


    @Test
    public void topTest() {
        shellExe.AsyncExecute("top", System.out::println);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(("[root@izkiqfmzrlha3jz ~]# mkdir -p /usr/addp/\r\n" +
                "[root@izkiqfmzrlha3jz ~]# \r\n" +
                "[root@izkiqfmzrlha3jz ~]# \r\n").matches("[\\S|\\s]*#[\\s]*"));
    }

    @Test
    public void test() {
        System.out.println(Arrays.toString("With great power comes great responsibility.".getBytes()));
    }

    @Test
    public void cdTest() {
        System.out.println(ShellUtil.cd("/usr/addp/",shellExe));
    }


    @Test
    public void fetchTest() {
        shellExe.fetch("cd /usr/addp")
                .then(System.out::println)
                .then1(r -> r.split("1")[5])
                .then(System.out::println)
                .Catch((e,d) -> e.printStackTrace())
                .then(System.out::println);
    }
}

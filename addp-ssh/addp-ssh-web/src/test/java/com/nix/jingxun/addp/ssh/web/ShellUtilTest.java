package com.nix.jingxun.addp.ssh.web;

import com.jcraft.jsch.JSchException;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author keray
 * @date 2018/12/04 下午4:20
 */
public class ShellUtilTest {

    @Test
    public void shellTest() throws IOException, JSchException {
        ShellUtil shellUtil = new ShellUtil("59.110.234.213","root","Kiss4400");
        final BlockingQueue<String> out = new LinkedBlockingQueue<>();
        final AtomicBoolean stop = new AtomicBoolean(false);
//        String command = "tail -1000f /var/lib/gitea/gitea/log/xorm.log";
        String command = "ls -all";
        stop.set(false);
        new Thread(() -> shellUtil.execute("top", out, stop)).start();
        while (!stop.get() || !out.isEmpty()) {
            try {
                String result = out.poll(10, TimeUnit.MILLISECONDS);
                if (result != null) {
                    if (ShellUtil.SHELL_EXEC_FAIL.equalsIgnoreCase(result)) {
                        result = "command exec fail";
                    }
                    System.out.println(result);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

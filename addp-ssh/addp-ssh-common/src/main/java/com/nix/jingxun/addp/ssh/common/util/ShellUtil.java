package com.nix.jingxun.addp.ssh.common.util;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author keray
 * @date 2018/12/04 下午3:11
 */
@Slf4j
public class ShellUtil {
    public static final String SHELL_EXEC_FAIL = "SHELL_EXEC_FAIL";

    //远程主机的ip地址
    private String ip;
    //远程主机登录用户名
    private String username;
    //远程主机的登录密码
    private String password;
    //设置ssh连接的远程端口
    private static final int DEFAULT_SSH_PORT = 22;
    private  Session session = null;

    public ShellUtil(String ip, String username, String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
    }

    private void init() throws JSchException {
        JSch jsch = new JSch();
        SSHUser userInfo = new SSHUser();
        //创建session并且打开连接，因为创建session之后要主动打开连接
        session = jsch.getSession(username, ip, DEFAULT_SSH_PORT);
        session.setPassword(password);
        session.setUserInfo(userInfo);
        session.connect();
    }

    public void execute(final String command, final BlockingQueue<String> out,final AtomicBoolean stop) {
        try {
            init();
            log.info("command={}",command);
            Channel channel = session.openChannel("exec");
            ChannelExec channelExec = (ChannelExec)channel;
            channelExec.setInputStream(null);
            channelExec.setCommand(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
            channelExec.connect();
            //接收远程服务器执行命令的结果
            String line = "";
            while (!channelExec.isClosed() || line != null) {
                if ((line = reader.readLine()) != null) {
                    out.put(line);
                }
            }
            reader.close();
            // 得到returnCode
            if (channelExec.isClosed()) {
                if (channelExec.getExitStatus() != 0) {
                    out.put(SHELL_EXEC_FAIL);
                }
            }
            // 关闭通道
            channelExec.disconnect();
        }catch (Exception e) {
            log.error("ssh command fail",e);
        }finally {
            stop.set(true);
        }
    }
    public void stop() {
        session.disconnect();
    }
}

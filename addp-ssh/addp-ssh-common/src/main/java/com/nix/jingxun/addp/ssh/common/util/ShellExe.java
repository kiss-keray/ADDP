package com.nix.jingxun.addp.ssh.common.util;

import cn.hutool.core.io.IoUtil;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author keray
 * @date 2019/05/10 22:17
 */
@Slf4j
public class ShellExe {

    private final static JSch jsch = new JSch();
    //远程主机的ip地址
    private String ip;
    //远程主机登录用户名
    private String username;
    //远程主机的登录密码
    private String password;
    //设置ssh连接的远程端口
    private static final int DEFAULT_SSH_PORT = 22;
    private final Session session;

    private ShellExe(String ip, String username, String password) throws JSchException {
        this.ip = ip;
        this.username = username;
        this.password = password;
        //创建session并且打开连接，因为创建session之后要主动打开连接
        session = jsch.getSession(username, ip, DEFAULT_SSH_PORT);
        session.setPassword(password);
        //  必须设置userInfo 同意key使用
        SSHUser userInfo = new SSHUser();
        session.setUserInfo(userInfo);
        session.connect();
    }
    public static ShellExe connect(String ip, String username, String password) throws JSchException {
        return new ShellExe(ip,username,password);
    }

    /**
     * 执行命令 一次性命令
     * @param command 命令
     * func 0 执行成功后执行
     *      1 执行失败后执行
     *      2 不管执行成功还是失败都执行
     * */
    @SafeVarargs
    public final ShellExe execute(String command, Consumer<Object>... func) {
        try {
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            InputStream in = channelExec.getInputStream();
            channelExec.setCommand(command);
            channelExec.setErrStream(System.err);
            channelExec.connect();
            String result  = IoUtil.read(in,"UTF-8");
            channelExec.disconnect();
            if (func != null && func.length > 0 && func[0] != null) {
                func[0].accept(result);
            }
        }catch (Exception e) {
            e.printStackTrace();
            if (func != null && func.length > 1 && func[1] != null) {
                func[1].accept(e);
            }
        }
        if (func != null && func.length > 2 && func[2] != null) {
            func[2].accept(null);
        }
        return this;
    }

    /**
     * 执行top tail -f 等命令
     * @param command 命令
     * @param optional 获取到一行数据后执行
     * */
    public final ShellExe execute(String command, Optional<Object> optional) {
        try {

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

                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}

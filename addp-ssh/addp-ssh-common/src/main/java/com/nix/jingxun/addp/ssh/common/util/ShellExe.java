package com.nix.jingxun.addp.ssh.common.util;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.nix.jingxun.addp.ssh.common.exception.ShellConnectException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.nix.jingxun.addp.ssh.common.util.ShellUtil.shellEnd;

/**
 * @author keray
 * @date 2019/05/10 22:17
 */
@Slf4j
public class ShellExe {
    private final static ThreadPoolExecutor THREAD_POOL_EXECUTOR =
            new ThreadPoolExecutor(10, 200, 10,
                    TimeUnit.SECONDS, new LinkedBlockingDeque<>(1024),
                    r -> {
                        Thread t = new Thread(r);
                        t.setName("shell");
                        return t;
                    });
    private final static JSch jsch = new JSch();
    //设置ssh连接的远程端口
    private static final int DEFAULT_SSH_PORT = 22;
    private final Session session;
    private ChannelShell channelShell;
    private OutputStream writer;
    private InputStream read;

    private ShellExe(String ip, String username, String password, long time) throws IOException, JSchException {
        //创建session并且打开连接，因为创建session之后要主动打开连接
        session = jsch.getSession(username, ip, DEFAULT_SSH_PORT);
        session.setPassword(password);
        //  必须设置userInfo 同意key使用
        SSHUser userInfo = new SSHUser();
        session.setUserInfo(userInfo);
        session.connect();
        channelShell = (ChannelShell) session.openChannel("shell");
        writer = channelShell.getOutputStream();
        read = channelShell.getInputStream();
        channelShell.connect(3000);
        Future<Boolean> welcomeTask = THREAD_POOL_EXECUTOR.submit(() -> {
            try {
                String resultStr = read();
                return resultStr.contains("Welcome") || resultStr.contains("welcome");
            } catch (Exception e) {
                return false;
            }
        });
        try {
            if (!welcomeTask.get(time, TimeUnit.MILLISECONDS)) {
                throw new ShellConnectException("shell content fail");
            }
        } catch (Exception e) {
            welcomeTask.cancel(true);
            throw new ShellConnectException(e.getMessage());
        }
    }

    public static ShellExe connect(String ip, String username, String password) throws  IOException, JSchException {
        return new ShellExe(ip, username, password, 3000);
    }

    /**
     * 执行命令 一次性命令
     *
     * @param command 命令
     *                func 0 执行成功后执行
     *                1 执行失败后执行
     *                2 不管执行成功还是失败都执行
     */
    @SafeVarargs
    public final ShellExe AsyncExecute(String command, Consumer<Object>... func) {
        return execute(command, false, func);
    }


    @SafeVarargs
    public final ShellExe syncExecute(String command, Consumer<Object>... func) {
        return execute(command, true, func);
    }

    public final String oneway(String command) {
        StringBuilder builder = new StringBuilder();
        syncExecute(command, builder::append,e -> {
            throw new RuntimeException((Throwable) e);
        });
        return builder.toString();
    }

    private ShellExe execute(String command, Boolean sync, Consumer<Object>... func) {
        Future<String> task = THREAD_POOL_EXECUTOR.submit(() -> {
            try {
                writer.write((command + "\r").getBytes(StandardCharsets.UTF_8));
                writer.flush();
                StringBuilder result = new StringBuilder();
                System.out.println("+++++++++++++++++++++++++++++++++：" + command);
                while (true) {
                    String line = read();
                    System.out.println(line);
                    result.append(line);
                    if (!sync) {
                        if (func != null && func.length > 0) {
                            func[0].accept(line);
                        }
                    }
                    if (shellEnd(line)) {
                        break;
                    }
                }
                System.out.println("---------------------------------");
                return result.toString();
            } catch (Exception e) {
                if (!sync) {
                    e.printStackTrace();
                    if (func != null && func.length > 1) {
                        func[1].accept(e);
                    }
                }
                throw new RuntimeException(e);
            } finally {
                if (!sync) {
                    if (func != null && func.length > 2) {
                        func[2].accept(null);
                    }
                }
            }
        });
        if (!sync) {
            return this;
        }
        try {
            String result = task.get();
            if (func != null && func.length > 0) {
                func[0].accept(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (func != null && func.length > 1) {
                func[1].accept(e);
            }
        } finally {
            if (func != null && func.length > 2) {
                func[2].accept(null);
            }
        }
        return this;
    }

    public String read() throws IOException {
        while (true) {
            byte[] bytes = new byte[2048];
            int len = read.read(bytes);
            return new String(bytes,0,len);
        }
    }
    public void close() {
        try {
            writer.close();
            channelShell.disconnect();
            session.disconnect();
            read.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.nix.jingxun.addp.ssh.common.util;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
    private BufferedReader read;
    private PrintWriter writer;

    private ShellExe(String ip, String username, String password,long time) throws Exception {
        //创建session并且打开连接，因为创建session之后要主动打开连接
        session = jsch.getSession(username, ip, DEFAULT_SSH_PORT);
        session.setPassword(password);
        //  必须设置userInfo 同意key使用
        SSHUser userInfo = new SSHUser();
        session.setUserInfo(userInfo);
        session.connect();
        channelShell = (ChannelShell) session.openChannel("shell");
        writer = new PrintWriter(new OutputStreamWriter(channelShell.getOutputStream()));
        read = new BufferedReader(new InputStreamReader(channelShell.getInputStream()));
        channelShell.connect(3000);
        Future<Boolean> welcomeTask = THREAD_POOL_EXECUTOR.submit(() -> {
            try {
                String line;
                while ((line = read.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains("Welcome")) {
                        return true;
                    }
                }
                return false;
            }catch (Exception e) {
                return false;
            }
        });
        if (!welcomeTask.get(time,TimeUnit.MILLISECONDS)) {
            throw new ConnectException("shell content fail");
        }
    }

    public static ShellExe connect(String ip, String username, String password) throws Exception {
        return new ShellExe(ip, username, password,3000);
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

        return execute(command,false,func);
    }



    public final ShellExe syncExecute(String command, Consumer<Object>... func) {
        return execute(command,true,func);
    }

    private ShellExe execute(String command,Boolean sync,Consumer<Object>... func) {
        Future<String> task = THREAD_POOL_EXECUTOR.submit(() -> {
            try {
                writer.println(command);
                writer.flush();
                StringBuilder result = new StringBuilder();
                String line;
                boolean start = false;
                while ((line = read.readLine()) != null) {
                    if (!start) {
                        if (line.matches("\\[[^\\[|^\\]]+][#|$][\\s]*" + command + ".*")) {
                            start = true;
                            if (!sync) {
                                if (func != null && func.length > 0) {
                                    func[0].accept(line + System.lineSeparator());
                                }
                            }
                            result.append(line).append(System.lineSeparator());
                            continue;
                        }
                    }
                    if (start) {
                        if (!sync) {
                            if (func != null && func.length > 0) {
                                func[0].accept(line + System.lineSeparator());
                            }
                        }
                        result.append(line).append(System.lineSeparator());
                        if (line.matches("\\[[^\\[|^\\]]+][#|$].*")) {
                            return result.toString();
                        }
                    }
                }
                return result.toString();
            }catch (Exception e) {
                if (!sync) {
                    e.printStackTrace();
                    if (func != null && func.length > 1) {
                        func[1].accept(e);
                    }
                }
                throw new RuntimeException(e);
            }finally {
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
        }finally {
            if (func != null && func.length > 2) {
                func[2].accept(null);
            }
        }
        return this;
    }
    public void close() {
        channelShell.disconnect();
        session.disconnect();
    }
}

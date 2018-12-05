package com.nix.jingxun.addp.ssh.websocket;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author keray
 * @date 2018/12/04 下午3:15
 */
@Slf4j

@ServerEndpoint(value = "/shell/socket")
@Component
public class ShellWebsocket {
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(50,50,10, TimeUnit.SECONDS,new LinkedBlockingDeque<>(1024), (ThreadFactory) Thread::new);
    private static final Map<String, Integer> PORT_ID_MAP = new ConcurrentHashMap<>(32);
    private static final Map<Session, String> sessionStringMap = new ConcurrentHashMap<>(32);

    private final static String SOCKET_CREATE = "login";
    private final static String SOCKET_CONNECT = "connect";

    private final static AtomicInteger PYTHON_SSH_PORT = new AtomicInteger(20000);

    @OnOpen
    public void onOpen(Session session) {
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        log.info("close websocket.{}",session.getId());
        PORT_ID_MAP.remove(sessionStringMap.remove(session));
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("websocket on message {},session {}",message,session);
        HashMap<String,String> data = JSON.parseObject(message, HashMap.class);
        String key = data.get("key");
        if (SOCKET_CREATE.equalsIgnoreCase(key)) {
            String ip = data.get("ip");
            String username = data.get("username");
            String password = data.get("password");
            String result = null;
            int count = 0;
            int port;
            do {
                port = PYTHON_SSH_PORT.getAndIncrement();
                if (port >= 65535) {
                    PYTHON_SSH_PORT.set(20000);
                }
                result = execPyShell(port,ip,username,password);
                log.info("python exec result {}",result);
            }while (result != null && result.contains("Address already in use") && count++ < 20);
            String json;
            if (result != null) {
                port = -1;
                json = "{\"key\":\"login\",\"port\":" + -1 + "}";
            } else {
                json = "{\"key\":\"login\",\"port\":" + port + "}";
                sessionStringMap.put(session,session.getId());
                PORT_ID_MAP.put(session.getId(),port);
            }
            log.info("create python connect port {}",port);
            sendMessage(json,session);
        } else if (SOCKET_CONNECT.equalsIgnoreCase(key)) {
            String id = data.get("id");
            sessionStringMap.put(session,id);
            String json = "{\"key\":\"login\",\"port\":" + PORT_ID_MAP.get(id) + "}";
            sendMessage(json,session);
        } else {
            log.warn("未知命令！！{}",key);
        }
    }

    private String execPyShell(int port,String ip,String username,String password) {
        Future<String> future = executor.submit(() -> {
            try {
                Process proc = Runtime.getRuntime().exec(String.format("python3  %s/%s %d %s %s %s",ShellWebsocket.class.getResource("/").getPath(),"server.py",port,ip,username,password));
                BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                StringBuilder content = new StringBuilder();
                String line = null;
                long now = System.currentTimeMillis();
                while ((line = reader.readLine()) != null && System.currentTimeMillis() > now + 100) {
                    if (line != null) {
                        content.append(line);
                    }
                }
                reader.close();
                return content.toString().isEmpty() ? null : content.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }
        });
        try {
            return future.get(100,TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            return null;
        } catch (Exception e) {
            return e.toString();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket error.",error);
        log.warn("websocket error.session {} ",session);
        PORT_ID_MAP.remove(sessionStringMap.remove(session));
    }

    private void sendMessage(String message,Session session) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("websocket send message fail.",e);
            log.warn("websocket send message fail.session {} message {}",session,message);
            PORT_ID_MAP.remove(sessionStringMap.remove(session));
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String command = String.format("python3  %s/%s %d %s %s %s",ShellWebsocket.class.getResource("/").getPath(),"server.py",8888,"59.110.234.213","root","Kss4400");
        System.out.println(command);
        Process proc = Runtime.getRuntime().exec(command);
//        Process proc = Runtime.getRuntime().exec("ls");
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        //接收远程服务器执行命令的结果
        StringBuilder content = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line != null) {
                content.append(line);
            }
        }
        reader.close();
        System.out.println(content);
        System.out.println(proc.isAlive());
        Thread.sleep(1000);
        System.out.println(proc.isAlive());
    }
}

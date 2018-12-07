package com.nix.jingxun.addp.ssh.websocket;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

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
}

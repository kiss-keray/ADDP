package com.nix.jingxun.addp.web.service.socket;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.IEnum.ReleasePhase;
import com.nix.jingxun.addp.web.IEnum.ReleaseType;
import com.nix.jingxun.addp.web.common.supper.WebThreadPool;
import com.nix.jingxun.addp.web.iservice.IWebSocket;
import com.nix.jingxun.addp.web.model.ReleaseBillModel;
import com.nix.jingxun.addp.web.model.ReleaseServerStatusModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author keray
 * @date 2019/06/05 20:48
 */
@Service
@ServerEndpoint(value = "/bill/status")
@Slf4j
public class ReleaseStatusSocket implements IWebSocket {

    private static final ConcurrentMap<Long, Set<Session>> sessionMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Session, Long> sessionBillIpMap = new ConcurrentHashMap<>();
    private static final String[] sessionRemoveClock = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    private Session mySession;

    @OnOpen
    public void onOpen(Session session) {
        log.info("接入bill status socket {}", session.getId());
        mySession = session;
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject msg = JSON.parseObject(message);
        if (msg.containsKey("subscribe")) {
            Long billId = msg.getLong("subscribe");
            if (!billId.equals(sessionBillIpMap.get(session))) {
                subscribeBillStatus(billId);
            }
        }
        if (msg.containsKey("unSubscribe")) {
            unSubscribeBillStatus();
        }
    }

    @OnClose
    public void onClose() {
        log.info("socket close:{}", mySession.getId());
        unSubscribeBillStatus();
    }

    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        log.error(StrUtil.format("bill socket error:{}", session.getId()), error);
        unSubscribeBillStatus();
        mySession.close();
    }

    private void subscribeBillStatus(Long billId) {
        unSubscribeBillStatus();
        sessionBillIpMap.put(mySession, billId);
        Set<Session> sessions = sessionMap.get(billId);
        if (sessions == null) {
            sessions = Collections.synchronizedSet(new LinkedHashSet());
        }

        sessions.add(mySession);
        synchronized (sessionRemoveClock[(int) (billId & 15)]) {
            sessionMap.putIfAbsent(billId, sessions);
        }
        try {
            send(mySession, WebSocketMsg.of("hello", StrUtil.format("订阅{}发布单成功", billId)));
        } catch (IOException ignore) {
        }
    }

    private void unSubscribeBillStatus() {
        Long billId = sessionBillIpMap.remove(mySession);
        if (billId == null) {
            return;
        }
        Set<Session> sessions = sessionMap.get(billId);
        sessions.remove(mySession);
        log.info("移除{}发布单订阅客户端{}", billId, mySession.getId());
        try {
            send(mySession, WebSocketMsg.of("hello", StrUtil.format("取消订阅{}发布单成功", billId)));
        } catch (Exception ignore) {
        }
        if (sessions.size() == 0) {
            synchronized (sessionRemoveClock[(int) (billId & 15)]) {
                if (sessions.size() == 0) {
                    sessionMap.remove(billId);
                    log.info("移除{}发布单订阅", billId);
                }
            }
        }
    }

    private void send(Session client, WebSocketMsg msg) throws IOException {
        client.getBasicRemote().sendText(JSON.toJSONString(msg));
    }

    public void notifyClient(ReleaseBillModel billModel) {
        log.info("notify client bill status {}", billModel.getId());
        WebThreadPool.IO_THREAD.execute(() -> {
            if (sessionMap.containsKey(billModel.getId())) {
                List<Session> clients = new CopyOnWriteArrayList<>(sessionMap.get(billModel.getId()));
                clients.forEach(client -> {
                    try {
                        List<ReleaseServerStatusModel> models = billModel._getReleaseServerStatusModel();
                        models.forEach(m -> m.setServerModel(m._getServerModel()));
                        send(client, WebSocketMsg.of("bill_status", BillNotify.builder()
                                .id(billModel.getId())
                                .releasePhase(billModel.getReleasePhase())
                                .releaseType(billModel.getReleaseType())
                                .environment(billModel.getEnvironment())
                                .releaseServerStatusModels(models)
                                .build()));
                    } catch (IOException e) {
                        log.error("bill status send socket error:", e);
                    }
                });
            }
        });
    }
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class BillNotify {
    private Long id;
    private ReleasePhase releasePhase;
    private ReleaseType releaseType;
    private ADDPEnvironment environment;
    private List<ReleaseServerStatusModel> releaseServerStatusModels;
}

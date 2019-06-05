package com.nix.jingxun.addp.web.service.socket;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
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

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author keray
 * @date 2019/06/05 20:48
 */
@Service
@ServerEndpoint("/bill/status/{billId}")
@Slf4j
public class ReleaseStatusSocket implements IWebSocket {

    private final ConcurrentMap<Long, List<Session>> sessionMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Session, Long> sessionBillIpMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("billId") Long billId) {
        log.info("接入bill status socket {} {}", session.getId(), billId);
        List<Session> sessions = sessionMap.get(billId);
        if (session == null) {
            sessions = Collections.synchronizedList(new LinkedList<>());
            sessions = sessionMap.putIfAbsent(billId, sessions);
        }
        sessions.add(session);
        sessionBillIpMap.putIfAbsent(session, billId);
    }

    @OnClose
    public void onClose(Session session) {
        log.info("socket close:{}", session.getId());
        removeSession(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error(StrUtil.format("bill socket error:{}", session.getId()), error);
        removeSession(session);
    }

    private void removeSession(Session session) {
        if (sessionBillIpMap.containsKey(session)) {
            Long billId = sessionBillIpMap.get(session);
            sessionMap.get(billId).remove(session);
            sessionBillIpMap.remove(session);
        }
    }

    public void notifyClient(ReleaseBillModel billModel) {
        log.info("notify client bill status {}", billModel.getId());
        WebThreadPool.IO_THREAD.execute(() -> {
            if (sessionMap.containsKey(billModel.getId())) {
                List<Session> clients = new CopyOnWriteArrayList<>(sessionMap.get(billModel.getId()));
                clients.forEach(client -> {
                    try {
                        client.getBasicRemote().sendText
                                (JSON.toJSONString(BillNotify.builder()
                                        .releasePhase(billModel.getReleasePhase())
                                        .releaseType(billModel.getReleaseType())
                                        .environment(billModel.getEnvironment())
                                        .serverStatus(billModel._getReleaseServerStatusModel())
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
    private ReleasePhase releasePhase;
    private ReleaseType releaseType;
    private ADDPEnvironment environment;
    private List<ReleaseServerStatusModel> serverStatus;
}

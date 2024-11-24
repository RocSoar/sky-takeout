package com.sky.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket服务
 */
@Slf4j
@Component
@ServerEndpoint("/ws/{sid}")  // sid:路径参数
public class WebSocketServer {

    //    存放会话对象
    private static final Map<String, Session> sessionMap = new HashMap<>();

    /**
     * 连接成功建立后的回调
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("[WebSocket] 客户端 {} 建立连接", sid);
        sessionMap.put(sid, session);
    }

    /**
     * 收到客户端消息后的回调
     */
    @OnMessage
    public void onMessage(String msg, @PathParam("sid") String sid) {
        log.info("[WebSocket] 收到来自客户端 {} 的消息: {}", sid, msg);
    }

    /**
     * 连接关闭的回调
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("[WebSocket] 客户端 {} 连接断开", sid);
        sessionMap.remove(sid);
    }

    /**
     * 向客户端群发消息
     */
    @SneakyThrows
    public void sendToAllClient(String msg) {
        for (Session session : sessionMap.values()) {
//            向客户端发送消息
            session.getBasicRemote().sendText(msg);
        }
    }
}

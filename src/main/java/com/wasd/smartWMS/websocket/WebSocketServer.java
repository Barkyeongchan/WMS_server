/**
 * WebSocketServer
 * - 클라이언트(WebSocket) 연결 관리 및 메시지 송수신 담당
 */
package com.wasd.smartWMS.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.*;

@Component
public class WebSocketServer extends TextWebSocketHandler {

    // 현재 연결된 모든 클라이언트의 세션을 저장하는 리스트
    private final List<WebSocketSession> sessions = new ArrayList<>();

    /**
     * 클라이언트가 WebSocket 서버에 처음 연결되면 호출됨
     * → 연결된 세션을 sessions 리스트에 추가하여 관리
     * → 콘솔에 연결된 클라이언트의 세션 ID를 출력
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);  // 연결된 세션 저장
        System.out.println("[WS] 클라이언트 연결됨: " + session.getId());
    }

    /**
     * 클라이언트가 서버로 텍스트 메시지를 전송하면 호출됨
     * → 수신된 메시지 내용을 콘솔에 출력
     * → (추후) 수신 데이터를 ROS나 다른 모듈로 전달 가능
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("[WS] 수신: " + message.getPayload());
        // TODO: ROS 모듈로 데이터 전달 예정
    }

    /**
     * 서버에서 모든 클라이언트에게 메시지를 전송할 때 사용
     * → 현재 연결된 세션 중 열린 상태인 클라이언트에게만 메시지 전송
     * → 로봇 상태 정보나 실시간 알림을 브로드캐스트할 때 활용 가능
     */
    public void sendToAll(String msg) throws Exception {
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) { // 세션이 아직 연결되어 있으면
                s.sendMessage(new TextMessage(msg)); // 메시지 전송
            }
        }
    }
}

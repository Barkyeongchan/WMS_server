/**
 * WebSocket과 ROS 간 메시지를 중계하는 서비스 클래스
 * - 클라이언트(WebSocket) ↔ ROS 메시지 전달
 * - ROS → 모든 클라이언트로 브로드캐스트
 */
package com.wasd.smartWMS.websocket;

import com.wasd.smartWMS.ros.RosBridgeClient;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class WebSocketRosBridgeService {

    private final WebSocketServer webSocketServer; // WebSocket 세션 관리 및 메시지 전송 담당
    private final RosBridgeClient rosBridgeClient; // ROS와 통신 담당

    private String lastPoseJson = "{}"; // 최신 로봇 pose를 JSON 형태로 저장

    public WebSocketRosBridgeService(WebSocketServer webSocketServer, RosBridgeClient rosBridgeClient) {
        this.webSocketServer = webSocketServer;
        this.rosBridgeClient = rosBridgeClient;

        // ROS에서 메시지 수신 시 처리
        rosBridgeClient.setOnRosMessage(msg -> {
            lastPoseJson = msg; // 최신 pose 갱신
            try {
                // 수신한 메시지를 모든 연결된 클라이언트로 브로드캐스트
                webSocketServer.sendToAll(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // 클라이언트 → ROS 메시지 전달
    public void handleClientMessage(String msg) {
        rosBridgeClient.sendMessageToROS(msg);
    }

    // 클라이언트 요청 시 최신 pose 전송 (단방향 트리거)
    public void sendLatestPose(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage(lastPoseJson));
    }
}
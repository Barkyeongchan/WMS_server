/**
 * WebSocket과 ROS 간 메시지를 중계하는 서비스 클래스
 * - 클라이언트(WebSocket) → ROS로 메시지 전달
 * - ROS → 모든 클라이언트(WebSocket)로 메시지 전달
 */
package com.wasd.smartWMS.websocket;

import com.wasd.smartWMS.ros.RosBridgeClient;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class WebSocketRosBridgeService {

    private final WebSocketServer webSocketServer;
    private final RosBridgeClient rosBridgeClient;

    private String lastPoseJson = "{}"; // 최신 pose 저장

    public WebSocketRosBridgeService(WebSocketServer webSocketServer, RosBridgeClient rosBridgeClient) {
        this.webSocketServer = webSocketServer;
        this.rosBridgeClient = rosBridgeClient;

        // ROS에서 수신 시 자동 브로드캐스트 + 최신 pose 저장
        rosBridgeClient.setOnRosMessage(msg -> {
            lastPoseJson = msg;
            try {
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

    // 클라이언트 요청 시 최신 pose 전송 (단방향 트리거형)
    public void sendLatestPose(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage(lastPoseJson));
    }
}

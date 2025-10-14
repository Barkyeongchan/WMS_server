/**
 * WebSocket과 ROS 간 메시지를 중계하는 서비스 클래스
 * - 클라이언트(WebSocket) → ROS로 메시지 전달
 * - ROS → 모든 클라이언트(WebSocket)로 메시지 전달
 */
package com.wasd.smartWMS.websocket;

import com.wasd.smartWMS.ros.RosBridgeClient;
import org.springframework.stereotype.Service;

@Service
public class WebSocketRosBridgeService {

    private final WebSocketServer webSocketServer;  // 클라이언트 통신 담당
    private final RosBridgeClient rosBridgeClient;  // ROS 통신 담당

    public WebSocketRosBridgeService(WebSocketServer webSocketServer, RosBridgeClient rosBridgeClient) {
        this.webSocketServer = webSocketServer;
        this.rosBridgeClient = rosBridgeClient;
    }

    /**
     * [클라이언트 → ROS]
     * 웹 클라이언트에서 받은 메시지를 ROS로 전송
     */
    public void handleClientMessage(String msg) {
        rosBridgeClient.sendMessageToROS(msg);
    }

    /**
     * [ROS → 클라이언트]
     * ROS에서 받은 메시지를 연결된 모든 클라이언트에게 전송
     */
    public void handleRosMessage(String msg) throws Exception {
        webSocketServer.sendToAll(msg);
    }
}

/**
 * ROS WebSocket 클라이언트 (rosbridge 연결)
 * 1. ROS WebSocket 서버에 연결
 * 2. ROS 메시지 수신 → Spring 내부 로직으로 전달
 * 3. Spring → ROS 메시지 전송 가능
 */
package com.wasd.smartWMS.ros;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component // Spring Bean 등록 -> 다른 컴포넌트에서 @Autowired로 사용 가능
public class RosBridgeClient extends WebSocketClient {

    /**
     * 생성자
     * - ROS WebSocket 서버(ws://ROS_IP:9090)에 연결
     * - connect() 호출로 비동기 연결 시도
     */
    public RosBridgeClient() throws Exception {
        super(new URI("ws://ROS_IP:9090")); // rosbridge 기본 포트
        connect(); // 비동기 연결
    }

    /**
     * 연결 성공 시 호출
     * - ROS 서버와 WebSocket 연결이 열리면 실행
     * - 원하는 토픽 구독(subscribe) 메시지를 ROS에 전송
     */
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("[ROS] 연결 성공");
        String subscribeMsg = """
            {
                "op": "subscribe",
                "topic": "/turtle1/pose"
            }
            """;
        this.send(subscribeMsg); // pose 토픽 구독 요청
    }

    /**
     * 메시지 수신 시 호출
     * - ROS에서 발행된 토픽 데이터를 JSON 문자열로 받음
     * - 등록된 핸들러(onRosMessage) 호출 → Spring에서 처리
     */
    @Override
    public void onMessage(String message) {
        System.out.println("[ROS 수신] " + message); // 확인용
        if (onRosMessage != null) onRosMessage.accept(message);
    }

    /**
     * 연결 종료 시 호출
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[ROS] 연결 종료: " + reason);
    }

    /**
     * WebSocket 예외 발생 시 호출
     */
    @Override
    public void onError(Exception ex) {
        System.err.println("[ROS] 에러: " + ex.getMessage());
    }

    /**
     * Spring → ROS 메시지 전송
     * - JSON 형식으로 ROS 토픽 발행 명령 전송 가능
     */
    public void sendMessageToROS(String json) {
        send(json);
    }

    // ROS 수신 시 호출될 콜백 핸들러
    private java.util.function.Consumer<String> onRosMessage;

    /**
     * 외부(Spring)에서 ROS 메시지 처리 핸들러 등록
     * - WebSocketRosBridgeService에서 사용
     */
    public void setOnRosMessage(java.util.function.Consumer<String> handler) {
        this.onRosMessage = handler;
    }
}

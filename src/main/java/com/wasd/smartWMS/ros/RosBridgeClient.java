/**
 * 1. ROS WebSocket 서버(rosbridge)에 연결
 * 2. ROS로부터 수신한 메시지를 콘솔 출력 또는 Spring 내부 로직으로 전달
 * 3. Spring 측에서 ROS로 명령(JSON)을 전송 가능
 */
package com.wasd.smartWMS.ros;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component // Spring Bean으로 등록 -> 다른 컴포넌트에서 주입(@Autowired) 가능
public class RosBridgeClient extends WebSocketClient {

    /**
     * 생성자
     *
     * - rosbridge_server의 기본 주소(ws://localhost:9090)에 연결 설정
     * - connect() 메서드 호출로 실제 비동기 연결 시도
     *
     * @throws Exception URI 형식이 잘못된 경우 발생
     */
    public RosBridgeClient() throws Exception {
        super(new URI("ws://192.168.1.71:9090")); // rosbridge_server의 기본 포트
        connect(); // WebSocket 서버에 연결 시도 (비동기)
    }

    /**
     * ROS 서버와의 WebSocket 연결이 성공적으로 이루어졌을 때 호출
     * @param handshake 서버로부터 받은 핸드셰이크 정보
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
        this.send(subscribeMsg);
    }

    /**
     * ROS 서버로부터 메시지를 수신했을 때 호출
     * @param message 수신된 JSON 메시지 (ROS 토픽 데이터 등)
     */
    @Override
    public void onMessage(String message) {
        System.out.println("[ROS 수신] " + message); // 확인용
        if (onRosMessage != null) onRosMessage.accept(message);
    }



    /**
     * ROS 서버와의 연결이 끊어졌을 때 호출됨
     * @param code    종료 코드
     * @param reason  종료 이유
     * @param remote  원격(서버)에서 종료한 경우 true
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[ROS] 연결 종료: " + reason);
    }

    /**
     * WebSocket 통신 중 예외가 발생했을 때 호출됨
     * @param ex 발생한 예외 객체
     */
    @Override
    public void onError(Exception ex) {
        System.err.println("[ROS] 에러: " + ex.getMessage());
    }

    /**
     * Spring 애플리케이션에서 ROS로 JSON 메시지를 전송하는 메서드
     * @param json ROS에 보낼 메시지 (예: 토픽 발행 명령)
     *
     * 사용 예:
     *   sendMessageToROS("{\"op\": \"publish\", \"topic\": \"/cmd_vel\", \"msg\": {...}}");
     */
    public void sendMessageToROS(String json) {
        send(json);
    }

    private java.util.function.Consumer<String> onRosMessage;

    public void setOnRosMessage(java.util.function.Consumer<String> handler) {
        this.onRosMessage = handler;
    }
}

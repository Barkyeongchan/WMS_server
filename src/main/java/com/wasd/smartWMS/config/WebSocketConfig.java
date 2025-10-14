/**
 * WebSocket 서버 설정 클래스
 * - Spring Boot에서 WebSocket을 활성화하고 설정하는 역할
 * - 특정 경로("/ws")로 들어오는 WebSocket 요청을 처리하도록 핸들러(WebSocketServer)를 등록
 * - 클라이언트의 모든 도메인 요청(CORS)을 허용하여 어디서든 접속 가능
 */
package com.wasd.smartWMS.config;

import com.wasd.smartWMS.websocket.WebSocketServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration             // Spring IoC 컨테이너가 이 클래스를 설정 클래스로 인식하도록 지정
@EnableWebSocket          // WebSocket 기능 활성화
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketServer webSocketServer; // 실제 WebSocket 메시지를 처리하는 핸들러

    /**
     * 생성자 주입(Constructor Injection)
     * Spring이 WebSocketServer 빈을 주입하여 사용
     */
    public WebSocketConfig(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    /**
     * WebSocket 핸들러 등록
     *
     * @param registry WebSocketHandlerRegistry - WebSocket 요청을 매핑하는 레지스트리
     *
     * 기능:
     * 1. "/ws" 경로로 들어오는 WebSocket 요청을 webSocketServer가 처리하도록 등록
     * 2. setAllowedOrigins("*")를 통해 모든 출처(CORS)를 허용, 즉 웹 클라이언트가 어디서 접속해도 연결 가능
     * 3. 실제 메시지 송수신, 연결/끊김 이벤트 처리는 WebSocketServer에서 담당
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketServer, "/ws") // 핸들러와 연결할 URL 매핑
                .setAllowedOrigins("*");            // 모든 도메인 허용 (CORS 설정)
    }
}

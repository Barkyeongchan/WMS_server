/**
 * WebSocket 설정 클래스
 * - Spring Boot에서 WebSocket 기능을 활성화하고,
 * - "/ws" 경로로 들어오는 요청을 WebSocketServer가 처리하도록 등록함.
 */
package com.wasd.smartWMS.config;

import com.wasd.smartWMS.websocket.WebSocketServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration        // 이 클래스를 Spring 설정 파일로 인식
@EnableWebSocket       // WebSocket 기능을 켬
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketServer webSocketServer; // WebSocket 메시지를 처리할 핸들러

    /**
     * 생성자 주입
     * - Spring이 WebSocketServer 객체를 자동으로 주입해줌
     */
    public WebSocketConfig(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    /**
     * WebSocket 핸들러 등록 메소드
     * - "/ws" 주소로 들어오는 WebSocket 요청을 webSocketServer가 처리하도록 설정
     * - setAllowedOrigins("*"): 모든 클라이언트 도메인에서 접속 허용 (CORS 설정)
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketServer, "/ws")
                .setAllowedOrigins("*");
    }
}

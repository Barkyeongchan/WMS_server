# Websocket을 활용해 ROS - spring Boot - 브라우저를 연결하고 Topic 출력하기

## <데이터 흐름>

1. **ROS**에서 `/turtle1/pose` 등 토픽이 publish됨
2. **Rosbridge**가 해당 데이터를 **WebSocket 포맷(JSON)** 으로 변환
3. **Spring Boot**가 Rosbridge와 연결되어 메시지를 수신
4. **Spring Boot WebSocket 서버**가 수신 데이터를 **웹 클라이언트로 브로드캐스트**
5. **웹 클라이언트 화면에 표시**

## <시스템 환경>

`Ubuntu 20.04`

`ROS Noetic`

## <파일 구조>

```tree
└─src
    └─main
        └─java
            └─com
               └─wasd
                  │  Main.java
                  │
                  └─smartWMS
                      │  Application.java
                      │
                      ├─config
                      │      SecurityConfig.java    # WebSocket 핸드셰이크 허용
                      │      WebSocketConfig.java   # WebSocket 설정 (핸들러 등록)
                      │
                      ├─domain
                      │  │  BaseTimeEntity.java
                      │  │
                      │  ├─admins
                      │  │      Admins.java
                      │  │      AdminsRepository.java
                      │  │      Role.java
                      │  │
                      │  └─stocks
                      │          Stocks.java
                      │          StocksRepository.java
                      │
                      ├─ros
                      │      RosBridgeClient.java    # Rosbridge 서버(ws://ROS_IP:9090)에 연결
                      │
                      ├─service
                      │      AdminService.java
                      │
                      ├─web
                      │  │  DashboardController.java
                      │  │  LoginController.java
                      │  │
                      │  └─dto
                      │          SessionAdmin.java
                      │
                      └─websocket
                              WebSocketRosBridgeService.java    # Rosbridge <-> WebSocket 중계 서비스
                              WebSocketServer.java              # Web 클라이언트(WebSocket)와 통신
```

## 1️⃣ 네트워크 조건 확인

- ROS가 실행되는 PC와 Spring Boot 서버 PC는 서로 **네트워크가 연결되어** 있어야 함.

Spring Boot PC 터미널과 ROS PC 터미널에서 `ping`으로 연결 확인

```bash
ping [Spring Boot PC IP]    # ROS → 서버
ping [ROS PC IP]    # 서버 → ROS
```

양쪽에서 서로 핑이 정상적으로 오면 통신 가능

## ️2️⃣ 의존성 추가

### `build.gradle`

```gradle
implementation 'org.springframework.boot:spring-boot-starter-websocket'
implementation 'org.java-websocket:Java-WebSocket:1.5.6'   // ROS용 클라이언트
implementation 'com.fasterxml.jackson.core:jackson-databind'
```

## 3️⃣ ROS 설정 (Rosbridge 실행)

**Rosbridge**는 Ros 메세지를 Websocket으로 변환하는 중간 다리 역할을 함

```bash
sudo apt install ros-noetic-rosbridge-server
roslaunch rosbridge_server rosbridge_websocket.launch
```

- 성공 메세지
```
[INFO] ... Rosbridge WebSocket server started on port 9090    # 기본 WebSocket 포트: 9090
```

## 4️⃣ Spring Boot 서버 설정

### `WebSocketConfig.java`

```java
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
```

### **핸들러 (Handler)**
- 특정 이벤트나 동작 발생시 그것을 처리하는 코드
- 이 코드에서는 **WebSocket 이벤트를 처리하는 담당자**를 뜻함

### `SecurityConfig.java`

```java
package com.wasd.smartWMS.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())    // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        // WebSocket 경로 허용
                        // "/ws/**" 경로로 들어오는 WebSocket 요청은 인증 없이 접근 가능하도록 설정
                        .requestMatchers("/index", "/stocks", "/", "/login", "/signup",
                                "/css/**", "/js/**", "/images/**", "/ws/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable()) // 기본 로그인 페이지 비활성화
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login"));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### **CSRF**
- Cross-Site Request Forgery, **사이트 간 요청 위조**
- 공격자가 사용자 권한을 이용해 원하지 않는 요청을 서버에 보내는 공격
- 초기 연결 단게에서 WebSocket 업그레이드 요청이 차단 될 수 있음
- WebSocket 전용 경로 `/ws/**`는 주로 인증을 별도로 처리하기 때문에 보호가 크게 필요치 않음

## 5️⃣ ROS와 WebSocket 서버 연결

### `RosBridgeClient.java`

```java
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
```

1. 생성자에서 `connect()` 호출 → ROS WebSocket 서버와 비동기 연결
2. `onOpen()`에서 ROS 토픽 구독(JSON) 메시지 전송
3. `onMessage()`에서 메시지 수신 후 등록된 콜백(`onRosMessage`) 호출 → Spring이 처리
4. `sendMessageToROS()`로 Spring → ROS 메시지 발행 가능

## 6️⃣ ROS → WebSocket 서버로 데이터 수신 및 전송

### ROS에서 받은 메시지를 클라이언트로 브로드캐스트(모든 클라이언트에게 데이터 전송)

### `WebSocketRosBridgeService.java`

```java
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
```

1. `handleClientMessage()`로 클라이언트 → ROS 메시지 전송 가능
2. ROS에서 메시지 수신하면 자동으로 `sendToAll()`로 모든 클라이언트 브로드캐스트
3. 최신 pose는 `sendLatestPose()`로 단방향 전송 가능 (예: 요청 시 한 번만 보내기)


## 7️⃣ 웹 클라이언트 (index.mustache / script.js)

### `index.mustache`

```html
// ...
<div class="log">
   <p class="log_t">작업 내역</p>
   <p class="log_text">작업 내역 나오는 곳</p>
   <!-- ROS pose 표시 영역 -->
   <pre id="pose"></pre>
</div>
//...
```

### `script.js`

```javascript
document.addEventListener('DOMContentLoaded', () => {

    // WebSocket 연결
    const ws = new WebSocket("ws://spring Boot 서버 IP:8080/ws"); // 웹서버 IP:포트 확인

    ws.onopen = () => {
        console.log("WebSocket 연결 성공");
    };

    ws.onmessage = (event) => {
        console.log("받은 메시지:", event.data);
        try {
            const data = JSON.parse(event.data);
            // ROS pose 토픽 데이터만 화면에 표시
            if (data.topic && data.topic.includes("turtle1/pose")) {
                document.getElementById("pose").innerText = JSON.stringify(data.msg, null, 2);
            }
        } catch (err) {
            console.error("메시지 파싱 오류:", err);
        }
    };

    ws.onerror = (err) => {
        console.error("WebSocket 에러:", err);
    };

    ws.onclose = () => {
        console.log("WebSocket 연결 종료");
    };
//...
```

## 8️⃣ 전체 작동 과정

### 1. ROS 환경 준비 및 Rosbridge WebSocket 실행
- ROS 토픽 데이터를 WebSocket으로 변환, 외부에서 WebSocket으로 접근 하도록 연결함
```bash
sudo apt install ros-noetic-rosbridge-server
roslaunch rosbridge_server rosbridge_websocket.launch
```

### 2. Spring Boot WebSocket 서버 설정 `WebSocketConfig.java`
- WebSocket 핸들러 등록, "/ws" 경로로 들어오는 요청 처리, CORS 허용
  <br><br>
### 3. Spring Boot 보안 설정 `SecurityConfig.java`
- CSRF 비활성화, WebSocket 핸드쉐이크 방해 방지, 인증/인가 설정
  <br><br>
### 4. Spring Boot에서 ROS WebSocket 연결 `RosBridgeClient.java`
- ROS WebSocket 서버에 연결, 토픽 구독, ROS 메시지 수신 → Spring 내부로 전달
  <br><br>
### 5. Spring Boot 내부에서 메시지 처리 `WebSocketRosBridgeService.java`
- ROS에서 받은 메시지 처리, 최신 pose 저장, 모든 WebSocket 클라이언트로 브로드캐스트
  <br><br>
### 6. Spring Boot → 웹 클라이언트 전송 `WebSocketServer.java`
- WebSocket 세션 관리, 메시지 전송
  <br><br>
### 7. 웹 클라이언트에서 메시지 수신 및 화면 표시 `index.mustache`, `script.js`
- WebSocket 연결, 메시지 수신, 화면 표시, UI 이벤트 처리

## 9️⃣ 자주 발생한 오류와 해결 방법

| 오류                            | 원인                      | 해결 방법                      |
| ----------------------------- | ----------------------- | -------------------------- |
| `WebSocket connection failed` | WebSocket 서버(`/ws`) 미등록 | `WebSocketConfig` 확인       |
| `403 Forbidden`               | CSRF 차단                 | SecurityConfig에서 CSRF 비활성화 |
| `ERR_UNKNOWN_URL_SCHEME`      | URL 잘못 입력               | `ws://` 사용 (`http://` 아님)  |
| `readyState: 3`               | 서버가 닫힘 / 포트 불일치         | 포트(8080) 및 경로(`/ws`) 확인    |
| 데이터는 오지만 클라이언트 표시 X           | HTML에 `id="pose"` 없음    | HTML 요소 추가                 |

# 1. 웹서버 호스팅

## 1. 프로젝트 생성

```
src/main/java/com/wasd/smartWMS
src/main/resources
├─ static
├─ templates
├─ application.properties
```

## 2. Build.gradle 작성

```
// 스프링 부트의 의존성들을 관리해주는 플러그인
plugins {
    id 'org.springframework.boot' version '3.2.0' // RELEASE 삭제
    id 'io.spring.dependency-management' version '1.1.4' //스프링부트의 의존성을 관리해주는 플러그인
    id 'java'
}

group 'com.park'
version '1.0.4-SNAPSHOT-'+new Date().format("yyyyMMddHHmmss")

java {
    JavaVersion.VERSION_17 // Java 17로 업그레이드 (Spring Boot 3.x 최소 요구사항)
}
// 원격저장소
repositories {
    mavenCentral()
}

// for Junit 5
test { // (2)
    useJUnitPlatform()
}

dependencies {
    // 프로젝트 개발에 필요한 의존성을 선언 하는 곳
    implementation('org.springframework.boot:spring-boot-starter-web')

    // 테스트
    testImplementation('org.springframework.boot:spring-boot-starter-test')

    // Lombok 설정
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'

    // 스프링 부트용 Spring Data Jpa 추상화 라이브러리
    implementation('org.springframework.boot:spring-boot-starter-data-jpa')

    // 인메모리형 관계형 데이터  베이스
    implementation('com.h2database:h2')

    // 머스타치 스타터 의존성 등록
    implementation('org.springframework.boot:spring-boot-starter-mustache')

    // Spring Security 기본 스타터 (보통 함께 사용)
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.session:spring-session-jdbc'

    // MariaDB 드라이버 등록
    implementation 'org.mariadb.jdbc:mariadb-java-client:3.1.4'
}
```

## 3. 정적 리소스 준비

- CSS, JS, 이미지 파일 준비

```
src/main/resources/static/
├─ css/style.css
├─ js/script.js
└─ images/logo.png
```

## 4. 템플릿 파일 준비

- 화면 구성

```
src/main/resources/templates/
├─ index.mustache
├─ layout/header.mustache
├─ layout/footer.mustache
```

`index.mustache` : 메인 화면

`header/footer` : 공통 레이아웃 분리

```
{{> layout/herder}}
...
{{> layout/footer}}
```

## 5. 컨트롤러 작성

- 요청 처리 및 템플릿 연결

`IndexController.java`

```
@Controller
public class IndexController {
    @GetMapping("/")
    public String index() {
        return "index"; // templates/index.mustache 렌더링
    }
}
```

## 6. 서비스, 도메인, 레포지토리 설계

- 데이터 처리, DB 연동

`domain` : DB 테이블 매핑

`repository` : JpaRepository 상속, DB 쿼리 담당

```
src/main/java/com/wasd/smartWMS/domain/stocks
├─ Stocks.java
├─ StocksRepository.java
src/main/java/com/wasd/smartWMS/domain/admins
├─ Admins.java
├─ AdminsRepository.java
├─ Role.java
```

## 7. 보안 규칙 정의

- HTTP 요청에 대한 보안 정책을 정의함

- 개발 단계에서 일시적으로 모든 접속을 허용함

`SecurityConfig` : 보안 규칙을 정의

```
src/main/java/com/wasd/smartWMS/config
├─ SecurityConfig
```

## 8. application.properties 작성

- 프로젝트의 전역 설정
    - 서버 연결
    - DB 연결 정보
    - 라이브러리 동작 방식
    - 로깅 레벨 설정
    - 사용자 정의 변수

```
# JPA
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.h2.console.enabled=true

# UTF-8
server.servlet.encoding.charset=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true


# Mustache
spring.mustache.charset=UTF-8
spring.mustache.encoding=UTF-8

# HTTP
spring.http.encoding.enabled=true
spring.http.encoding.charset=UTF-8
spring.http.encoding.force=true
```

# 2. DB(RDS)연결

## 1. RDS 생성 (WASD_Project)

## 2. 보안그룹 생성

- 로컬 ip, 각 사용자 ip 추가

## 3. application.properties 설정 추가

```
spring.datasource.url=jdbc:mariadb://<RDS-ENDPOINT>:3306/<DB명>
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.username=<USERNAME>
spring.datasource.password=<PASSWORD>
```

## 4. 개발 편의를 위해 session 비활성화

```
spring.session.jdbc.enabled=false
```

# 3. 로그인 화면 구성

## 1. DB 생성

```sql
create database if not exists ADMINS;
```

## 2. admins Table 생성

```sql
USE ADMINS; -- ADMINS DB 사용

CREATE TABLE admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- 생성되는 admin의 순번
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(10) DEFAULT 'SUB_ADMIN', -- MAIN_ADMIN / SUB_ADMIN
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

| 컬럼명      | 타입          | NULL 허용 | 키   | 기본값              | 설명                |
|-------------|---------------|-----------|------|---------------------|---------------------|
| id          | bigint(20)    | NO        | PRI  | auto_increment      | 기본 키 (PK)        |
| username    | varchar(50)   | NO        | UNI  |                     | 사용자 이름 (고유)  |
| password    | varchar(100)  | NO        |      |                     | 비밀번호            |
| role        | varchar(10)   | NO        |      |                     | 관리자 권한 (MAIN, SUB) |
| created_at  | timestamp     | YES       |      | current_timestamp() | 생성 시각 자동 저장 |

## 3. Admins.java

*admins Table 구조 정의*

```
package com.wasd.smartWMS.domain.admins;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "admins")
@Getter
@NoArgsConstructor
public class Admins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // PK 자동 생성

    @Column(nullable = false, unique = true) // 빈값 허용 안함, 중복 허용 안함
    private String username;

    @Column(nullable = false) // DB와 일치
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)    // Enum를 문자열로 저장, 더 깔끔하고 안전함
    private Role role;  // 관리자 등급 - MAIN, SUB

    @CreationTimestamp // DB에 INSERT될 때 자동으로 현재 시간 저장
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Admins(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
```

## 4. AdminsRepository.java

*admins Table에서 CRUD 실행 인터페이스*

```
package com.wasd.smartWMS.domain.admins;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;  // 값이 여러 개가 있을 수 있을 때 다중 조회
import java.util.Optional;  // 값이 있냐 없냐의 단일 조회

public interface AdminsRepository extends JpaRepository<Admins, Long> {

    // username으로 단일 조회 (회원가입/로그인에 주로 사용)
    Optional<Admins> findByUsername(String username);

    // role별 전체 조회 (MAIN_ADMIN / SUB_ADMIN)
    List<Admins> findByRole(String role);

    // MAIN_ADMIN 1명만 조회
    default Optional<Admins> findMainAdmin() {
        return findByRole("MAIN_ADMIN").stream().findFirst();
    }
}
```

## 5. AdminService 생성

*실제 서비스를 담당*

```
// 실제 admin 관련 서비스를 담당
package com.wasd.smartWMS.service;

import com.wasd.smartWMS.domain.admins.Admins;
import com.wasd.smartWMS.domain.admins.AdminsRepository;
import com.wasd.smartWMS.domain.admins.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.wasd.smartWMS.domain.admins.Role.MAIN;

public class AdminService {
    private final AdminsRepository adminsRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();  // 비밀번호 암호화

    public AdminService(AdminsRepository adminsRepository) {
        this.adminsRepository = adminsRepository;
    }

    // 회원가입
    public Admins signup(String username, String rawPassword) {

        // 이미 존재하는 username 체크
        if (adminsRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // DB에 MAIN_ADMIN 존재 여부 확인
        boolean mainAdminExists = !adminsRepository.findByRole(MAIN).isEmpty();

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Admin 객체 생성
        Admins admin = new Admins();
        admin.setUsername(username);
        admin.setPassword(encodedPassword);

        // 권한 설정
        if (!mainAdminExists) {
            admin.setRole(MAIN); // 최초 가입자
        } else {
            admin.setRole(Role.SUB);  // 이후 가입자
        }

        // DB 저장 후 반환
        return adminsRepository.save(admin);
    }

    // 로그인
    public Optional<Admins> login(String username, String rawPassword) {
        Optional<Admins> adminOpt = adminsRepository.findByUsername(username);

        // 존재 확인 + 비밀번호 검증
        if (adminOpt.isPresent() && passwordEncoder.matches(rawPassword, adminOpt.get().getPassword())) {
            return adminOpt;
        }

        // 로그인 실패
        return Optional.empty();
    }

    // 비밀번호 변경
    public void changePassword(String username, String oldPassword, String newPassword) {
        // 계정 존재 여부 확인
        Admins admin = adminsRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않습니다."));

        // 기존 비밀번호 검증
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 틀립니다.");
        }

        // 새 비밀번호 저장
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminsRepository.save(admin);
    }

    // SUB_ADMIN 계정 삭제
    public void deleteAdmin(Long id) {

        // 계정 존재 여부 확인
        Admins admin = adminsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 계정이 없습니다."));

        // MAIN_ADMIN 삭제 금지
        if (admin.getRole() == MAIN) {
            throw new IllegalStateException("MAIN_ADMIN은 삭제할 수 없습니다.");
        }

        // 계정 삭제
        adminsRepository.delete(admin);
    }

    // MAIN_ADMIN 존재 여부 체크
    public boolean mainAdminExists() {
        return !adminsRepository.findByRole(MAIN).isEmpty();
    }
}
```

## 6. LoginController 생성

*웹에서 요청을 받아 로그인 관련 뷰(View)를 반환*

```
// 웹에서 요청을 받아 로그인 관련 뷰를 반환
package com.wasd.smartWMS.web;

import com.wasd.smartWMS.service.AdminService;
import com.wasd.smartWMS.domain.admins.Admins;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class LoginController {

    private final AdminService adminService;

    public LoginController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 로그인 화면
    // 최초 가입 시 MAIN_ADMIN이 없으면 회원가입 버튼/폼을 보여줌
    @GetMapping("/")
    public String loginPage(Model model) {
        model.addAttribute("showSignup", !adminService.mainAdminExists());
        return "login"; // templates/login.mustache
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        // DB에서 username 확인 + 비밀번호 일치 여부 체크
        Optional<Admins> adminOpt = adminService.login(username, password);

        // 로그인 성공 시 세션에 로그인 정보 저장
        if (adminOpt.isPresent()) {
            session.setAttribute("loginAdmin", adminOpt.get());
            return "redirect:/index"; // 로그인 성공 후 대시보드 이동
        }

        // 로그인 실패 시 메시지 표시
        model.addAttribute("error", "아이디 또는 비밀번호가 틀렸습니다.");
        model.addAttribute("showSignup", !adminService.mainAdminExists());
        return "login";
    }

    // 회원가입 화면
    // MAIN_ADMIN 존재 시 접근 불가
    @GetMapping("/signup")
    public String signupPage() {
        if (adminService.mainAdminExists()) {
            return "redirect:/"; // 이미 MAIN_ADMIN 존재 → 로그인 페이지 이동
        }
        return "signup"; // templates/signup.html
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         Model model) {

        try {
            // 서비스 호출: username 중복 체크, 최초 가입자 MAIN_ADMIN 설정
            adminService.signup(username, password);
            return "redirect:/"; // 가입 성공 → 로그인 페이지
        } catch (IllegalArgumentException e) {
            // 이미 존재하는 username 예외 처리
            model.addAttribute("error", e.getMessage());
            model.addAttribute("showSignup", true);
            return "signup";
        }
    }

    // 로그아웃 처리
    // 세션 초기화 후 로그인 페이지 이동
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 삭제
        return "redirect:/";
    }
}
```

## 7. IndexController 수정

```
@GetMapping("/")    // "/" 경로(루트 URL)를 처리
public String index() {
    return "index"; // "index:라는 뷰를 반환 -> templates/index.mustache 랜더링
}
```

`/`경로를 로그인 경로로 사용하기 때문에 `/index`경로로 수정

```
@GetMapping("/index")    // "/index" 경로(루트 URL)를 처리
public String index() {
    return "index"; // "index:라는 뷰를 반환 -> templates/index.mustache 랜더링
}
```

## 8. Session Table 설정

- build.gradle

```
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.session:spring-session-jdbc'    // 세션을 DB에 저장해 여러 서버에서도 공유 가능
implementation 'org.mariadb.jdbc:mariadb-java-client:3.1.4'
```

- application.properties

```
spring.datasource.url=jdbc:mariadb://host:3306/ADMINS   // ADMINS DB에 연결
spring.datasource.username=<USER_ID>
spring.datasource.password=<USER_PASSWORD>
spring.session.store-type=jdbc                          // JDBC(DB)을 세션 저장 타입으로 사용
spring.session.jdbc.initialize-schema=always            // 서버 시작시 스프링 세션 테이블 자동 생성
```

*서버 시작 시 스프링이 자동으로 `SPRING_SESSION`, `SPRING_SESSION_ATTRIBUTES` 테이블 생성*
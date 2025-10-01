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


## 1. 프로젝트 생성

```
src/main/java/com/wasd/smartWMS
src/main/resources
├─ static
├─ templates
├─ application.properties
```

## 2. 정적 리소스 준비

- CSS, JS, 이미지 파일 준비

```
src/main/resources/static/
├─ css/style.css
├─ js/script.js
└─ images/logo.png
```

## 3. 템플릿 파일 준비

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

## 4. 컨트롤러 작성

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

## 5. 서비스, 도메인, 레포지토리 설계

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

## 엔티티 (Entity)

1. 정의 : DB 테이블과 매핑되는 자바 클래스, 실제 데이터 구조를 표현


2. 역할 : DB의 한 행(row)을 객체로 표현, 컬럼과 필드를 매핑

```java
@Entity
@Table(name = "stocks")
public class Stock {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer inventory;
}
```

`Stock` 클래스 자체가 엔티티

## 애노테이션 (Annotation)
1. 정의 : 코드에 붙이는 메타정보, 컴파일이나 런타임에 특정 동작을 하도록 알려주는 표식
 

2. 역할 : 클래스/필드/메서드에 기능/속성을 부여


3. 자주 쓰이는 애노테이션

- Lombok - 반복되는 매서드를 컴파일 할 때 자동으로 만들어주는 라이브러리
  - `@Getter` : 모든 필드 getter 생성 - 필드(변수)의 값을 읽어오는 매서드
  - `@Settet` : 모든 필드 setter 생성 - 필드(변수)의 값을 설정(변경)하는 매서드
  - `@NoArgsConstructor` : 기본 생성자 생성 - 생성자(객체 생성시 초기값을 성절하거나, 객체를 초기화하는 메서드)
  - `@AllArgsConstructor` : 모든 필드 매개변수 생성자
  - `@Builder` : 빌더 패턴 생성 - 빌더(객체를 만들 때 단계별로 필요한 값만 넣어 안전하게 만드는 패턴)
  - `@Date` : Getter, Setter, ToString, EqualsAndHashCode, RequiredArgsConstructor 포함


- JPA
  - `@Entity` : 클래스가 DB 테이블과 매핑됨
  - `@Table(name="...")` : DB 테이블 이름 지정
  - `@Id` : 기본 키 지정
  - `@GeneratedValue(strategy=...)` : PK 자동 생성 전략 - 새 데이터 삽입시 PK값(DB 테이블 각 행을 유일하게 식별하는 필드)을 자동으로 생성
  - `@Column` : 컬럼 속성 정의 (nullable, length 등) - 테이블 항목 이름
  - `@MappedSuperclass` : 부모 클래스 필드를 상속 가능 - 상위 클래스(엔티티)의 속성을 받아서 씀
  - `@EntityListeners` : Auditing 등 이벤트 리스너 연결 - 엔티티가 생성/수정된 일시, 작성자를 자동으로 기록
  - `@CreatedDate` : 생성일시 자동 기록
  - `@LastModifiedDate` : 수정일시 자동 기록


- Spring MVC (Model-View-Controller)
```
// Stocks 예시 흐름
[웹 브라우저]  --HTTP 요청-->  [Controller]  --DB 조회/저장-->  [Service]  --Repository-->  [DB]
       ↑                                                     ↓
       |------------------ View(HTML) 렌더링 ----------------|

```
  - `@Controller` : 클라이언트 HTTP 요청을 받아 적절한 Service를 호출하고, 결과를 View(html)로 반환 
  - `@RestController` : JSON 등 데이터 반환 (Controller + ResponseBody) 
  - `@GetMapping("/path")` : 서버에서 데이터 조회
  - `@PostMapping("/path")` : 서버에서 데이터 생성
  - `@PutMapping("/path")` : 서버에서 데이터 전체 수정
  - `@PatchMapping("/path")` : 서버에서 데이터 일부 수정
  - `@DeleteMapping("/path")` : 서버에서 데이터 삭제
  - `@RequestParam` : URL에 데이터를 붙여 서버로 전달 - /stocks?key=value, 필터나 옵션용을 쓰임
  - `@PathVariable`	: URL에 경로 변수(id)를 붙여 서버로 전달 - /stocks/{id}, 식별용으로 쓰임
  - `@RequestBody` : HTTP 요청 본문(body)에 담긴 데이터를 자바 객체로 변환, 바로 사용 가능
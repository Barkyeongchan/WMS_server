// 테이블 구조를 정의 함
package com.wasd.smartWMS.domain.stocks;

import com.wasd.smartWMS.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "Stocks")    // DB 테이블 이름
public class Stocks extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)    // length : 글자수 제한 (100자까지), nullable : 비어있으면 오류
    private String name;

    @Column(nullable = false)    // 속성의 자료형이 Integer기 때문에 length는 쓰지않음
    private Integer inventory;

    @Builder
    public Stocks(String name, Integer inventory) {    // 속성의 값을 생성
        this.name = name;
        this.inventory = inventory;
    }

    public void Update(String name, Integer inventory) {    // 속성의 값을 업데이트
        this.name = name;
        this.inventory = inventory;
    }
}

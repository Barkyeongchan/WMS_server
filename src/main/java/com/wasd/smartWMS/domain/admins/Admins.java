// Admins 테이블 구조 정의
package com.wasd.smartWMS.domain.admins;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name="Admins")

public class Admins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // PK 자동 생성

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)    // Enum를 문자열로 저장, 더 깔끔하고 안전함
    private Role role;  // 관리자 등급 - MAIN, SUB

    private Long mainAdminId;  // SUB 관리자가 속해있는 MAIN 관리자 ID(PK)

    @Builder
    public Admins(String name, String role, Long mainAdminId) {
        this.name = name;
        this.role = role;
        this.mainAdminId = mainAdminId;
    }
}

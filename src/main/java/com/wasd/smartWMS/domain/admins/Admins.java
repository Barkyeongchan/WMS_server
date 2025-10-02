package com.wasd.smartWMS.domain.admins;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "admins")
@Setter
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
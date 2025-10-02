package com.wasd.smartWMS.domain.admins;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;  // 값이 여러 개가 있을 수 있을 때 다중 조회
import java.util.Optional;  // 값이 있냐 없냐의 단일 조회

public interface AdminsRepository extends JpaRepository<Admins, Long> {

    // username으로 단일 조회 (회원가입/로그인에 주로 사용)
    Optional<Admins> findByUsername(String username);

    // role별 전체 조회 (MAIN_ADMIN / SUB_ADMIN)
    List<Admins> findByRole(Role role);

    // MAIN_ADMIN 1명만 조회
    default Optional<Admins> findMainAdmin() {
        return findByRole(Role.MAIN).stream().findFirst();
    }
}
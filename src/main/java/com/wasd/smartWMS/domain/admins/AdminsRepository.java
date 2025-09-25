// DB에서 Admins 데이터의 CRUD를 수행함
package com.wasd.smartWMS.domain.admins;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminsRepository extends JpaRepository<Admins, Long> {

    // 메인 관리자의 서브 관리자 조회
    List<Admins> findByMainAdminId(Long mainAdminId);

    // 메인 관리자 전체 조회 (role="MAIN")
    List<Admins> findByRole(String role);
}

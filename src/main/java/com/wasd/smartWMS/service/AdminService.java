// 실제 admin 관련 서비스를 담당
package com.wasd.smartWMS.service;

import com.wasd.smartWMS.domain.admins.Admins;
import com.wasd.smartWMS.domain.admins.AdminsRepository;
import com.wasd.smartWMS.domain.admins.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.wasd.smartWMS.domain.admins.Role.MAIN;

@Service
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

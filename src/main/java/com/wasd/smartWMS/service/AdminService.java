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
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminService(AdminsRepository adminsRepository) {
        this.adminsRepository = adminsRepository;
    }

    // 회원가입
    public Admins signup(String userid, String username, String rawPassword) {

        // userid 중복 체크
        if (adminsRepository.findByUserid(userid).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // MAIN_ADMIN 존재 여부 확인
        boolean mainAdminExists = !adminsRepository.findByRole(MAIN).isEmpty();

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Admin 객체 생성
        Admins admin = new Admins();
        admin.setUserid(userid);
        admin.setUsername(username);
        admin.setPassword(encodedPassword);
        admin.setRole(mainAdminExists ? Role.SUB : MAIN);  // 최초 가입자 MAIN

        return adminsRepository.save(admin);
    }

    // 로그인
    public Optional<Admins> login(String userid, String rawPassword) {
        Optional<Admins> adminOpt = adminsRepository.findByUserid(userid);

        if (adminOpt.isPresent() && passwordEncoder.matches(rawPassword, adminOpt.get().getPassword())) {
            return adminOpt;
        }
        return Optional.empty();
    }

    // MAIN_ADMIN 존재 여부
    public boolean mainAdminExists() {
        return !adminsRepository.findByRole(MAIN).isEmpty();
    }
}

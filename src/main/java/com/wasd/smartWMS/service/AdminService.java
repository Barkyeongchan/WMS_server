// 실제 서비스를 담당
package com.wasd.smartWMS.service;

import com.wasd.smartWMS.domain.admins.Admins;
import com.wasd.smartWMS.domain.admins.AdminsRepository;
import com.wasd.smartWMS.domain.admins.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.wasd.smartWMS.domain.admins.Role.MAIN;

public class AdminService {
    private final AdminsRepository adminsRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();  // 비밀번호 암호화

    public AdminService(AdminsRepository adminsRepository) {
        this.adminsRepository = adminsRepository;
    }

    // 회원가입
    public Admins signup(String username, String rawPassword) {
        boolean mainAdminExists = adminsRepository.findByRole(MAIN).isEmpty();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Admins admin = new Admins();
        admin.setUsername(username);
        admin.setPassword(encodedPassword);

        if (!mainAdminExists) {
            admin.setRole(MAIN); // 최초 가입자
        } else {
            admin.setRole(Role.SUB);  // 이후 가입자
        }


        return adminsRepository.save(admin);
    }

    // 로그인
    public Optional<Admins> login(String username, String rawPassword) {
        Optional<Admins> adminOpt = adminsRepository.findByUsername(username);
        if (adminOpt.isPresent() && passwordEncoder.matches(rawPassword, adminOpt.get().getPassword())) {
            return adminOpt;
        }
        return Optional.empty();
    }

    // main admin 존재 여부 체크
    public boolean mainAdminExists() {
        return !adminsRepository.findByRole(MAIN).isEmpty();
    }
}



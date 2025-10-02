// 웹에서 요청을 받아 로그인 관련 뷰를 반환
package com.wasd.smartWMS.web;

import com.wasd.smartWMS.service.AdminService;
import com.wasd.smartWMS.domain.admins.Admins;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class LoginController {

    private final AdminService adminService;

    public LoginController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 로그인 화면
    // 최초 가입 시 MAIN_ADMIN이 없으면 회원가입 버튼/폼을 보여줌
    @GetMapping("/")
    public String loginPage(Model model) {
        model.addAttribute("showSignup", !adminService.mainAdminExists());
        return "login"; // templates/login.mustache
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        // DB에서 username 확인 + 비밀번호 일치 여부 체크
        Optional<Admins> adminOpt = adminService.login(username, password);

        // 로그인 성공 시 세션에 로그인 정보 저장
        if (adminOpt.isPresent()) {
            session.setAttribute("loginAdmin", adminOpt.get());
            return "redirect:/index"; // 로그인 성공 후 대시보드 이동
        }

        // 로그인 실패 시 메시지 표시
        model.addAttribute("error", "아이디 또는 비밀번호가 틀렸습니다.");
        model.addAttribute("showSignup", !adminService.mainAdminExists());
        return "login";
    }

    // 회원가입 화면
    // MAIN_ADMIN 존재 시 접근 불가
    @GetMapping("/signup")
    public String signupPage() {
        if (adminService.mainAdminExists()) {
            return "redirect:/"; // 이미 MAIN_ADMIN 존재 → 로그인 페이지 이동
        }
        return "signup"; // templates/signup.html
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         Model model) {

        try {
            // 서비스 호출: username 중복 체크, 최초 가입자 MAIN_ADMIN 설정
            adminService.signup(username, password);
            return "redirect:/"; // 가입 성공 → 로그인 페이지
        } catch (IllegalArgumentException e) {
            // 이미 존재하는 username 예외 처리
            model.addAttribute("error", e.getMessage());
            model.addAttribute("showSignup", true);
            return "signup";
        }
    }

    // 로그아웃 처리
    // 세션 초기화 후 로그인 페이지 이동
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 삭제
        return "redirect:/";
    }
}

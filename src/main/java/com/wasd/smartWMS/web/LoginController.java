package com.wasd.smartWMS.web;

import com.wasd.smartWMS.domain.admins.Admins;
import com.wasd.smartWMS.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class LoginController {

    private final AdminService adminService;

    public LoginController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 로그인 페이지
    @GetMapping({"/", "/login"})
    public String loginPage(Model model) {
        // 최초 가입 시 MAIN_ADMIN 없으면 회원가입 버튼/폼 보여주기
        model.addAttribute("showSignup", !adminService.mainAdminExists());
        return "login"; // templates/login.mustache
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String userid,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Optional<Admins> adminOpt = adminService.login(userid, password);

        if (adminOpt.isPresent()) {
            // 로그인 성공 시 세션 저장
            session.setAttribute("loginAdmin", adminOpt.get());
            return "redirect:/index"; // 로그인 성공 후 대시보드 이동
        }

        // 로그인 실패 시 메시지
        model.addAttribute("error", "아이디 또는 비밀번호가 틀렸습니다.");
        model.addAttribute("showSignup", !adminService.mainAdminExists());
        return "login";
    }

    // 회원가입 페이지
    @GetMapping("/signup")
    public String signupPage() {
        // MAIN_ADMIN 존재 시 접근 불가
        if (adminService.mainAdminExists()) {
            return "redirect:/login";
        }
        return "signup"; // templates/signup.mustache
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String userid,
                         @RequestParam String password,
                         Model model) {

        try {
            adminService.signup(userid, username, password);
            return "redirect:/login"; // 회원가입 후 로그인 페이지
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("showSignup", true);
            return "signup";
        }
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 삭제
        return "redirect:/login";
    }
}
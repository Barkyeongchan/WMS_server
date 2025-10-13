package com.wasd.smartWMS.controller;

import com.wasd.smartWMS.domain.admins.Admins;
import com.wasd.smartWMS.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 회원가입 페이지
    @GetMapping("/signup")
    public String signupForm() {
        return "signup"; // signup.html
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         Model model) {
        try {
            adminService.signup(username, password);
            model.addAttribute("msg", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "login"; // 회원가입 후 로그인 페이지로 이동
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginForm() {
        return "login"; // login.html
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        Optional<Admins> adminOpt = adminService.login(username, password);

        if (adminOpt.isPresent()) {
            session.setAttribute("admin", adminOpt.get());
            return "redirect:/index";
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "login";
        }
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

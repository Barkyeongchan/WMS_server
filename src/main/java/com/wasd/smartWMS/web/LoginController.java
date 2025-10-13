package com.wasd.smartWMS.web;

import com.wasd.smartWMS.domain.admins.Admins;
import com.wasd.smartWMS.service.AdminService;
import com.wasd.smartWMS.web.dto.SessionAdmin;
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
        model.addAttribute("showSignup", !adminService.mainAdminExists());
        model.addAttribute("signupSuccess", false);
        model.addAttribute("error", "");
        return "login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String userid,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Optional<Admins> adminOpt = adminService.login(userid, password);

        if (adminOpt.isPresent()) {
            // DTO로 세션 저장
            session.setAttribute("loginAdmin", new SessionAdmin(adminOpt.get()));
            return "redirect:/index";
        }

        model.addAttribute("error", "아이디 또는 비밀번호가 틀렸습니다.");
        model.addAttribute("showSignup", !adminService.mainAdminExists());
        model.addAttribute("signupSuccess", false);
        return "login";
    }


    // 회원가입 페이지
    @GetMapping("/signup")
    public String signupPage(Model model) {
        if (adminService.mainAdminExists()) {
            return "redirect:/login";
        }
        model.addAttribute("showSignup", true);
        model.addAttribute("signupSuccess", false);
        model.addAttribute("error", "");
        return "signup";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String userid,
                         @RequestParam String password,
                         Model model) {

        try {
            adminService.signup(userid, username, password);
            model.addAttribute("signupSuccess", true);
            model.addAttribute("showSignup", false);
            model.addAttribute("error", "");
            return "login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("showSignup", true);
            model.addAttribute("signupSuccess", false);
            return "signup";
        }
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
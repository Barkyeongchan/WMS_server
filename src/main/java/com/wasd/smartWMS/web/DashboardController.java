// 웹에서 요청을 받아 뷰(View)를 반환 + 세션 처리 포함
package com.wasd.smartWMS.web;

import com.wasd.smartWMS.domain.admins.Admins;
import com.wasd.smartWMS.service.AdminService;
import com.wasd.smartWMS.web.dto.SessionAdmin;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final AdminService adminService;

    public DashboardController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 메인 화면
    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        // 세션에서 로그인 정보 가져오기
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("loginAdmin");
        if (sessionAdmin != null) {
            // DB에서 엔티티 조회 필요 시
            Admins admin = adminService.findById(sessionAdmin.getId());
            model.addAttribute("admin", admin);
        }
        return "index"; // templates/index.mustache
    }

    // 재고 화면
    @GetMapping("/stocks")
    public String stocks() {
        return "stocks"; // templates/stocks.mustache
    }
}

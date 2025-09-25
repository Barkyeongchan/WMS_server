// 웹에서 요청을 받아 뷰(View)를 반환
package com.wasd.smartWMS.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")    // "/" 경로(루트 URL)를 처리
    public String index() {
        return "index"; // "index:라는 뷰를 반환 -> templates/index.mustache 랜더링
    }
}

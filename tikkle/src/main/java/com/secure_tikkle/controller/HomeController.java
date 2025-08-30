package com.secure_tikkle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {
	
	@GetMapping("/home")
    public String home() {
        return "redirect:/api/me"; // 뷰 렌더링 안 하고 리다이렉트
    }
}
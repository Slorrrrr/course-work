package com.fireservice.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String rootRedirect(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // Пользователь не авторизован — редирект на регистрацию
            return "redirect:/register";
        } else {
            // Пользователь авторизован — редирект на домашнюю страницу
            return "redirect:/home";
        }
    }
}

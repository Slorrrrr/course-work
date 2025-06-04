package com.bookshop.controller;

import com.bookshop.model.Order;
import com.bookshop.model.User;
import com.bookshop.service.OrderService;
import com.bookshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Получаем текущего пользователя
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // Получаем заказы пользователя
        List<Order> orders = orderService.getOrdersByUserId(user.getId());

        // Статистика по категориям (по имени категории)
        Map<String, Long> categoryStats = orders.stream()
                .filter(o -> o.getBook() != null && o.getBook().getCategory() != null)
                .collect(Collectors.groupingBy(o -> o.getBook().getCategory().getName(), Collectors.counting()));

        // Статистика по авторам
        Map<String, Long> authorStats = orders.stream()
                .filter(o -> o.getBook() != null && o.getBook().getAuthor() != null)
                .collect(Collectors.groupingBy(o -> o.getBook().getAuthor(), Collectors.counting()));

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("categoryStats", categoryStats);
        model.addAttribute("authorStats", authorStats);

        return "profile";
    }
}

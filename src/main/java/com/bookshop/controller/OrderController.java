package com.bookshop.controller;

import com.bookshop.model.Order;
import com.bookshop.model.User;
import com.bookshop.service.CustomUserDetails;
import com.bookshop.service.OrderService;
import com.bookshop.service.BookService;
import com.bookshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getAllOrders(Model model, Authentication authentication) {
        List<Order> orders;

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            orders = orderService.getAllOrders();
        } else {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = customUserDetails.getUser();
            orders = orderService.getOrdersByUserId(user.getId());
        }

        orders.forEach(o -> {
            if (o.getOrderDate() != null) {
                o.setOrderDateString(o.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            }
        });

        model.addAttribute("orders", orders);
        return "orders/list"; // Thymeleaf шаблон orders/list.html
    }


    @GetMapping("/add")
    public String showAddForm(Model model, Authentication authentication) {
        model.addAttribute("order", new Order());
        model.addAttribute("books", bookService.getAllBooks());

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<User> users;
        if (isAdmin) {
            users = userService.getAllUsers();  // все пользователи для админа
        } else {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User currentUser = customUserDetails.getUser();
            users = List.of(currentUser);  // только текущий пользователь для обычного юзера
        }
        model.addAttribute("users", users);

        return "orders/add";
    }


    @PostMapping("/add")
    public String addOrder(@ModelAttribute Order order, Model model) {
        boolean success = orderService.saveOrderWithStockCheck(order);
        if (!success) {
            model.addAttribute("error", "Недостаточно книг на складе для данного заказа");
            // Нужно заново подгрузить книги и пользователей, чтобы форма отобразилась корректно
            model.addAttribute("order", order);
            model.addAttribute("books", bookService.getAllBooks());
            model.addAttribute("users", List.of(order.getUser())); // или подгрузи согласно роли, как в showAddForm
            return "orders/add"; // Возврат на форму с ошибкой
        }
        return "redirect:/orders";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("users", userService.getAllUsers());
        return "orders/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateOrder(@PathVariable Long id, @ModelAttribute Order order) {
        order.setId(id);
        orderService.saveOrder(order);
        return "redirect:/orders";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "redirect:/orders";
    }
}

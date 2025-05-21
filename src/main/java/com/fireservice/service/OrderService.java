package com.fireservice.service;

import com.fireservice.model.Order;
import com.fireservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        var orders = orderRepository.findAll();
        System.out.println("Orders count: " + orders.size());
        for (Order order : orders) {
            System.out.println("Order ID: " + order.getId());
            if (order.getUser() != null) {
                System.out.println("  User ID: " + order.getUser().getId() + ", username: " + order.getUser().getUsername());
            } else {
                System.out.println("  User is null!");
            }
            if (order.getBook() != null) {
                System.out.println("  Book ID: " + order.getBook().getId() + ", title: " + order.getBook().getTitle());
            } else {
                System.out.println("  Book is null!");
            }
            System.out.println("  Quantity: " + order.getQuantity());
            System.out.println("  Status: " + order.getStatus());
            System.out.println("  Order Date: " + order.getOrderDate());
        }
        return orders;
    }


    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    // Можно добавить бизнес-логику для изменения статуса заказа, подсчёта суммы и т.д.
}

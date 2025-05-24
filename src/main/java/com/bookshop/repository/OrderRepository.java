package com.bookshop.repository;

import com.bookshop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAll();

    List<Order> findByUserId(Long userId);
}

package com.bookshop.service;

import com.bookshop.model.Book;
import com.bookshop.model.Order;
import com.bookshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookService bookService;  // Добавляем BookService

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
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

    // Новый метод с проверкой и списанием количества книг
    public boolean saveOrderWithStockCheck(Order order) {
        Book book = bookService.getBookById(order.getBook().getId());
        if (book == null) {
            throw new RuntimeException("Книга не найдена");
        }
        if (book.getStock() < order.getQuantity()) {
            // Недостаточно книг для заказа
            return false;
        }
        // Списываем книги со склада
        book.setStock(book.getStock() - order.getQuantity());
        bookService.saveBook(book);

        // Сохраняем заказ
        orderRepository.save(order);
        return true;
    }
}

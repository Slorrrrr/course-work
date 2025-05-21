package com.fireservice.repository;

import com.fireservice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAll();

    List<Book> findByCategory(String category);

    List<Book> findByStores_Id(Long storeId);

    List<Book> findByStockGreaterThan(int stock);
}

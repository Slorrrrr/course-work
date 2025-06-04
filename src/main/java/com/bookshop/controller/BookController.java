package com.bookshop.controller;

import com.bookshop.model.Book;
import com.bookshop.model.Category;
import com.bookshop.model.Store;
import com.bookshop.service.BookService;
import com.bookshop.service.CategoryService;
import com.bookshop.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private CategoryService categoryService;

    // Список всех книг
    @GetMapping
    public String listBooks(
            @RequestParam(defaultValue = "title") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        List<Book> books = bookService.getAllBooksSorted(sortField, sortDir);
        model.addAttribute("books", books);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "books/list";
    }

    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("stores", storeService.getAllStores());
        model.addAttribute("categories", categoryService.getAllCategories()); // добавляем категории
        return "books/add";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book,
                          @RequestParam(required = false) List<Long> storeIds,
                          @RequestParam Long categoryId) {

        // Устанавливаем магазины
        if (storeIds != null) {
            Set<Store> stores = new HashSet<>(storeService.getStoresByIds(storeIds));
            book.setStores(stores);
        } else {
            book.setStores(new HashSet<>());
        }

        // Устанавливаем категорию
        Category category = categoryService.getCategoryById(categoryId);
        book.setCategory(category);

        bookService.saveBook(book);
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        model.addAttribute("stores", storeService.getAllStores());
        model.addAttribute("categories", categoryService.getAllCategories()); // добавляем категории
        return "books/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable Long id,
                             @ModelAttribute Book book,
                             @RequestParam(required = false) List<Long> storeIds,
                             @RequestParam Long categoryId) {

        book.setId(id);

        // Обновляем магазины
        if (storeIds != null) {
            Set<Store> stores = new HashSet<>(storeService.getStoresByIds(storeIds));
            book.setStores(stores);
        } else {
            book.setStores(new HashSet<>());
        }

        // Обновляем категорию
        Category category = categoryService.getCategoryById(categoryId);
        book.setCategory(category);

        bookService.saveBook(book);
        return "redirect:/books";
    }
}

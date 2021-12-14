package me.sathish.myreads.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController {
    @Autowired
    BookRepo bookRepo;
    @GetMapping(value = "/books")
    public List<Book> getAllBooks(){
        List<Book> books= bookRepo.findAll();
        return books;
    }
}

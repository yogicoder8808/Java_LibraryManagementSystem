package libraryManagement;

import java.util.ArrayList;
import java.util.List;

public record User(String name, String email, String phoneNumber, List<Book> borrowedBooks) {
    public User(String name, String email, String phoneNumber) {
        this(name, email, phoneNumber, new ArrayList<>());
    }

    public User borrowBook(Book book) {
        List<Book> updatedBorrowedBooks = new ArrayList<>(borrowedBooks);
        updatedBorrowedBooks.add(book);
        return new User(name, email, phoneNumber, updatedBorrowedBooks);
    }

    public User returnBook(Book book) {
        List<Book> updatedBorrowedBooks = new ArrayList<>(borrowedBooks);
        updatedBorrowedBooks.remove(book);
        return new User(name, email, phoneNumber, updatedBorrowedBooks);
    }
}


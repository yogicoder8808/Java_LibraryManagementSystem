package libraryManagement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LibrarySystem implements Borrowable, Returnable {
    private final Map<String, Book> books = new ConcurrentHashMap<>();
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, List<Book>> userBorrowedBooks = new ConcurrentHashMap<>();

    public LibrarySystem() {
        books.put("978-3-16-148410-0", new Book("978-3-16-148410-0", "The Great Gatsby", "F. Scott Fitzgerald", true));
        books.put("978-0-7432-7356-5", new Book("978-0-7432-7356-5", "To Kill a Mockingbird", "Harper Lee", true));
    }

    @Override
    public void borrowBook(User user, Book book) {
        if (!books.containsKey(book.isbn())) {
            System.out.println("Book not found.");
            return;
        }

        if (!book.available()) {
            System.out.println("Book is not available.");
            return;
        }

        Book updatedBook = book.withAvailability(false);
        books.put(updatedBook.isbn(), updatedBook);

        // Update borrowed books for the user
        List<Book> userBooks = userBorrowedBooks.computeIfAbsent(user.email(), k -> new ArrayList<>());
        
        // Ensure that the book is added only once
        if (userBooks.stream().noneMatch(b -> b.isbn().equals(updatedBook.isbn()))) {
            userBooks.add(updatedBook);
        }

        // Update the User object in the users map
        User updatedUser = user.borrowBook(updatedBook);
        users.put(user.email(), updatedUser);

        System.out.println("Book borrowed successfully: " + updatedBook);
    }

    @Override
    public void returnBook(User user, Book book) {
        if (!books.containsKey(book.isbn())) {
            System.out.println("Book not found.");
            return;
        }

        List<Book> userBooks = userBorrowedBooks.get(user.email());
        if (userBooks == null || userBooks.stream().noneMatch(b -> b.isbn().equals(book.isbn()))) {
            System.out.println("User did not borrow this book.");
            return;
        }

        userBooks.removeIf(b -> b.isbn().equals(book.isbn()));
        if (userBooks.isEmpty()) {
            userBorrowedBooks.remove(user.email());
        } else {
            userBorrowedBooks.put(user.email(), userBooks);
        }

        Book updatedBook = book.withAvailability(true);
        books.put(updatedBook.isbn(), updatedBook);

        // Update the User object in the users map
        User updatedUser = user.returnBook(book);
        users.put(user.email(), updatedUser);

        System.out.println("Book returned successfully: " + updatedBook);
    }

    public void addBook(Book book) {
        books.put(book.isbn(), book);
        System.out.println("Book added: " + book);
    }

    public void deleteBook(String isbn) {
        if (books.remove(isbn) != null) {
            System.out.println("Book deleted with ISBN: " + isbn);
        } else {
            System.out.println("No books found with ISBN: " + isbn);
        }
    }

    public void deleteAllBooks() {
        books.clear();
        System.out.println("All books deleted.");
    }

    public void addUser(User user) {
        users.put(user.email(), user);
        userBorrowedBooks.put(user.email(), new ArrayList<>(user.borrowedBooks()));
        System.out.println("New user added: " + user);
    }

    public void removeUser(User user) {
        users.remove(user.email());
        userBorrowedBooks.remove(user.email());
        System.out.println("User removed: " + user);
    }

    public void removeAllUsers() {
        users.clear();
        userBorrowedBooks.clear();
        System.out.println("All users removed.");
    }

    public Optional<Book> findBookByTitle(String title) {
        return books.values().stream()
            .filter(b -> b.title().equalsIgnoreCase(title))
            .findFirst();
    }

    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }

    public void viewBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available.");
        } else {
            books.values().forEach(System.out::println);
        }
    }

    public void searchBooks(String keyword) {
        List<Book> results = books.values().stream()
            .filter(b -> b.title().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("No books found for keyword: " + keyword);
        } else {
            results.forEach(System.out::println);
        }
    }

    public void printUserBorrowedBooks(User user) {
        List<Book> books = userBorrowedBooks.get(user.email());

        if (books == null || books.isEmpty()) {
            System.out.println("No borrowed books for user: " + user);
        } else {
            books.forEach(System.out::println);
        }
    }

    public void viewAllUsers() {
        if (users.isEmpty()) {
            System.out.println("No users available.");
        } else {
            users.values().forEach(System.out::println);
        }
    }
}

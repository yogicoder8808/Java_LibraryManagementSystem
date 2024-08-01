package libraryManagement;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var librarySystem = new LibrarySystem();
        User currentUser = null; 
        boolean isAdmin = false; 

        while (true) {
            System.out.println("Welcome to Library Management System!");
            System.out.println("0. Exit");
            System.out.println("1. Login");
            System.out.println("2. New User");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 0:
                    if (confirmAction(scanner, "exit")) {
                        System.out.println("Thank you for using the Library Management System. Goodbye!");
                        scanner.close();
                        return;
                    }
                    break;

                case 1:
                case 2:
                    var result = handleUserOrAdmin(scanner, librarySystem, choice == 2);
                    if (result != null) {
                        currentUser = result.getUser();
                        isAdmin = result.isAdmin();
                        handleRoleMenu(scanner, librarySystem, currentUser, isAdmin);
                    }
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static LoginResult handleUserOrAdmin(Scanner scanner, LibrarySystem librarySystem, boolean isNewUser) {
        String email;
        User user = null;
        boolean isAdmin = false;

        if (isNewUser) {
            System.out.print("Enter user name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Phone number: ");
            String phoneNumber = scanner.nextLine();
            System.out.print("Enter Email ID: ");
            email = scanner.nextLine();

            user = new User(name, email, phoneNumber);

            if (librarySystem.findUserByEmail(email).isPresent()) {
                System.out.println("User already exists.");
                return null;
            } else {
                librarySystem.addUser(user);
            }
        } else {
            System.out.print("Enter Email ID: ");
            email = scanner.nextLine();

            user = librarySystem.findUserByEmail(email).orElse(null);

            if (user == null) {
                System.out.println("User not found. Please register as a new user.");
                return null;
            }
        }

        System.out.println("Are you an:");
        System.out.println("1. Admin");
        System.out.println("2. Normal User");

        int roleChoice = scanner.nextInt();
        scanner.nextLine();

        switch (roleChoice) {
            case 1:
                if (verifyAdminPassword(scanner)) {
                    isAdmin = true;
                } else {
                    System.out.println("Invalid admin password. Access denied.");
                    return null;
                }
                break;

            case 2:
                isAdmin = false;
                break;

            default:
                System.out.println("Invalid choice. Returning to main menu.");
                return null;
        }

        return new LoginResult(user, isAdmin);
    }

    private static boolean verifyAdminPassword(Scanner scanner) {
        final String ADMIN_PASSWORD = "Admin@123"; // Example password; 
        System.out.print("Enter admin password: ");
        String inputPassword = scanner.nextLine();
        return ADMIN_PASSWORD.equals(inputPassword);
    }

    private static void handleRoleMenu(Scanner scanner, LibrarySystem librarySystem, User user, boolean isAdmin) {
        if (user == null) return;

        if (isAdmin) {
            showAdminMenu(scanner, librarySystem);
        } else {
            showUserMenu(scanner, librarySystem, user);
        }
    }

    private static void showAdminMenu(Scanner scanner, LibrarySystem librarySystem) {
        while (true) {
            System.out.println("Admin Menu:");
            System.out.println("1. View books");
            System.out.println("2. Add book");
            System.out.println("3. Delete book");
            System.out.println("4. Delete all data");
            System.out.println("5. Search");
            System.out.println("6. View orders");
            System.out.println("7. View users"); 
            System.out.println("8. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    librarySystem.viewBooks();
                    break;

                case 2:
                    addBook(scanner, librarySystem);
                    break;

                case 3:
                    deleteBook(scanner, librarySystem);
                    break;

                case 4:
                    deleteAllData(scanner, librarySystem);
                    break;

                case 5:
                    searchBooks(scanner, librarySystem);
                    break;

                case 6:
                    viewOrders(scanner, librarySystem);
                    break;

                case 7: 
                    librarySystem.viewAllUsers();
                    break;

                case 8:
                    System.out.println("Exiting admin menu.");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addBook(Scanner scanner, LibrarySystem librarySystem) {
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();

        Book book = new Book(isbn, title, author, true);
        if (confirmAction(scanner, "Add book: " + book)) {
            librarySystem.addBook(book);
        }
    }

    private static void deleteBook(Scanner scanner, LibrarySystem librarySystem) {
        System.out.print("Enter ISBN of the book to delete: ");
        String isbn = scanner.nextLine();
        if (confirmAction(scanner, "Delete book with ISBN: " + isbn)) {
            librarySystem.deleteBook(isbn);
        }
    }

    private static void deleteAllData(Scanner scanner, LibrarySystem librarySystem) {
        if (confirmAction(scanner, "Delete all data")) {
            librarySystem.deleteAllBooks();
            librarySystem.removeAllUsers();
        }
    }

    private static void searchBooks(Scanner scanner, LibrarySystem librarySystem) {
        System.out.print("Enter a keyword to search for books: ");
        String keyword = scanner.nextLine();
        librarySystem.searchBooks(keyword);
    }

    private static void viewOrders(Scanner scanner, LibrarySystem librarySystem) {
        System.out.print("Enter user email to view borrowed books: ");
        String email = scanner.nextLine();
        librarySystem.findUserByEmail(email)
                .ifPresentOrElse(
                        user -> librarySystem.printUserBorrowedBooks(user),
                        () -> System.out.println("User not found.")
                );
    }

    private static void showUserMenu(Scanner scanner, LibrarySystem librarySystem, User user) {
        while (true) {
            System.out.println("User Menu:");
            System.out.println("1. View books");
            System.out.println("2. Borrow book");
            System.out.println("3. Return book");
            System.out.println("4. Search");
            System.out.println("5. View orders");
            System.out.println("6. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    librarySystem.viewBooks();
                    break;

                case 2:
                    borrowBook(scanner, librarySystem, user);
                    break;

                case 3:
                    returnBook(scanner, librarySystem, user);
                    break;

                case 4:
                    searchBooks(scanner, librarySystem);
                    break;

                case 5:
                    librarySystem.printUserBorrowedBooks(user);
                    break;

                case 6:
                    System.out.println("Exiting user menu.");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void borrowBook(Scanner scanner, LibrarySystem librarySystem, User user) {
        System.out.print("Enter the title of the book to borrow: ");
        String title = scanner.nextLine();
        Book book = librarySystem.findBookByTitle(title).orElse(null);

        if (book != null && confirmAction(scanner, "Borrow book: " + book)) {
            librarySystem.borrowBook(user, book);
        } else {
            System.out.println("Book not found or not available.");
        }
    }

    private static void returnBook(Scanner scanner, LibrarySystem librarySystem, User user) {
        System.out.print("Enter the title of the book to return: ");
        String title = scanner.nextLine();
        Book book = librarySystem.findBookByTitle(title).orElse(null);

        if (book != null && confirmAction(scanner, "Return book: " + book)) {
            librarySystem.returnBook(user, book);
        } else {
            System.out.println("Book not found or not borrowed by user.");
        }
    }

    private static boolean confirmAction(Scanner scanner, String actionDescription) {
        System.out.print("Are you sure you want to " + actionDescription + "? (y/n): ");
        String confirmation = scanner.nextLine();
        return confirmation.equalsIgnoreCase("y");
    }

    private static class LoginResult {
        private final User user;
        private final boolean isAdmin;

        public LoginResult(User user, boolean isAdmin) {
            this.user = user;
            this.isAdmin = isAdmin;
        }

        public User getUser() {
            return user;
        }

        public boolean isAdmin() {
            return isAdmin;
        }
    }
}



package libraryManagement;

public record Book(String isbn, String title, String author, boolean available) {

    public Book withAvailability(boolean available) {
        return new Book(this.isbn, this.title, this.author, available);
    }
}


package org.example;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private String format;
    private String status;
    private int rating;
    private String review;
    private int pagesRead;
    private int totalPages;
    private String dateAdded;

    public Book() {}

    public Book(String title, String author, String isbn, String genre, String format) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.format = format;
        this.status = "В планах";
        this.rating = 0;
        this.review = "";
        this.pagesRead = 0;
        this.totalPages = 0;
        this.dateAdded = java.time.LocalDate.now().toString();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
    public int getPagesRead() { return pagesRead; }
    public void setPagesRead(int pagesRead) { this.pagesRead = pagesRead; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }

    @Override
    public String toString() {
        String titleStr = title != null ? title : "без названия";
        String authorStr = author != null ? author : "неизвестен";
        String genreStr = genre != null ? genre : "-";
        String formatStr = format != null ? format : "-";
        String statusStr = status != null ? status : "-";
        String ratingStr = rating == 0 ? "-" : rating + "/5";

        return String.format("[%d] %s | %s | %s | %s | %s | %s",
                id, titleStr, authorStr, genreStr, formatStr, statusStr, ratingStr);
    }

    public String toDetailedString() {
        return String.format(
                "ID: %d\nНазвание: %s\nАвтор: %s\nISBN: %s\nЖанр: %s\nФормат: %s\n" +
                        "Статус: %s\nРейтинг: %s\nПрочитано: %d/%d стр.\nРецензия: %s\nДобавлена: %s",
                id,
                title != null ? title : "-",
                author != null ? author : "-",
                isbn != null ? isbn : "-",
                genre != null ? genre : "-",
                format != null ? format : "-",
                status != null ? status : "-",
                rating == 0 ? "нет" : rating + "/5",
                pagesRead,
                totalPages,
                review != null && !review.isEmpty() ? review : "нет",
                dateAdded != null ? dateAdded : "-"
        );
    }
}
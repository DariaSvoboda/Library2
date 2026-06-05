package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class BookStorage {
    private List<Book> books = new ArrayList<>();
    private int nextId = 1;

    public void add(Book book) {
        book.setId(nextId++);
        if (book.getStatus() == null) book.setStatus("В планах");
        if (book.getFormat() == null) book.setFormat("бумажная");
        if (book.getAuthor() == null) book.setAuthor("Неизвестен");
        if (book.getGenre() == null) book.setGenre("не указан");
        if (book.getIsbn() == null) book.setIsbn("не указан");
        if (book.getDateAdded() == null) book.setDateAdded(java.time.LocalDate.now().toString());
        books.add(book);
    }

    public void update(Book book) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == book.getId()) {
                books.set(i, book);
                return;
            }
        }
    }

    public boolean delete(int id) {
        return books.removeIf(b -> b.getId() == id);
    }

    public Optional<Book> findById(int id) {
        return books.stream().filter(b -> b.getId() == id).findFirst();
    }

    public List<Book> search(String query) {
        String q = query.toLowerCase();
        return books.stream()
                .filter(b ->
                        (b.getTitle() != null && b.getTitle().toLowerCase().contains(q)) ||
                                (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(q)) ||
                                (b.getGenre() != null && b.getGenre().toLowerCase().contains(q)) ||
                                (b.getIsbn() != null && b.getIsbn().contains(q)))
                .collect(Collectors.toList());
    }

    public List<Book> filterByStatus(String status) {
        return books.stream()
                .filter(b -> b.getStatus() != null && b.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public List<Book> getAll() {
        return new ArrayList<>(books);
    }

    public Map<String, Long> getGenreStats() {
        return books.stream()
                .filter(b -> b.getGenre() != null)
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()));
    }

    public long countByStatus(String status) {
        return books.stream()
                .filter(b -> b.getStatus() != null && b.getStatus().equalsIgnoreCase(status))
                .count();
    }

    public double avgRating() {
        return books.stream()
                .filter(b -> b.getRating() > 0)
                .mapToInt(Book::getRating)
                .average()
                .orElse(0);
    }
}
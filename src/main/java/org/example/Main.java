package org.example;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static BookStorage storage = new BookStorage();
    private static GoogleBooksService gbService = new GoogleBooksService();
    private static Scanner scanner = new Scanner(System.in, "UTF-8");

    public static void main(String[] args) {
        System.out.println("LIBRARY2 v1.0 - Управление библиотекой");

        addTestBooks();

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": addBookManually(); break;
                case "2": addBookByIsbn(); break;
                case "3": addBookByTitle(); break;
                case "4": listAllBooks(); break;
                case "5": searchBooks(); break;
                case "6": editBook(); break;
                case "7": deleteBook(); break;
                case "8": showStats(); break;
                case "9": showBookDetails(); break;
                case "0":
                    System.out.println("До свидания!");
                    return;
                default: System.out.println("Неверный выбор.");
            }
        }
    }

    private static void addTestBooks() {
        Book b1 = new Book("Мастер и Маргарита", "Михаил Булгаков", "9785170852741", "Роман", "бумажная");
        b1.setStatus("Прочитано");
        b1.setRating(5);
        b1.setPagesRead(480);
        b1.setTotalPages(480);
        b1.setReview("Великий роман");
        storage.add(b1);

        Book b2 = new Book("Преступление и наказание", "Фёдор Достоевский", "9785170878890", "Роман", "EPUB");
        b2.setStatus("В процессе");
        b2.setPagesRead(300);
        b2.setTotalPages(672);
        storage.add(b2);

        Book b3 = new Book("1984", "Джордж Оруэлл", "9780451524935", "Антиутопия", "PDF");
        storage.add(b3);
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Добавить книгу вручную");
        System.out.println("2. Добавить по ISBN (Google Books)");
        System.out.println("3. Найти по названию (Google Books)");
        System.out.println("4. Показать все книги");
        System.out.println("5. Поиск в библиотеке");
        System.out.println("6. Редактировать книгу");
        System.out.println("7. Удалить книгу");
        System.out.println("8. Статистика");
        System.out.println("9. Подробная информация о книге");
        System.out.println("0. Выход");
        System.out.print("> ");
    }

    private static void addBookManually() {
        System.out.println();
        Book book = new Book();

        book.setStatus("В планах");
        book.setFormat("бумажная");
        book.setDateAdded(java.time.LocalDate.now().toString());

        System.out.print("Название: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Название обязательно.");
            return;
        }
        book.setTitle(title);

        System.out.print("Автор: ");
        String author = scanner.nextLine().trim();
        book.setAuthor(author.isEmpty() ? "Неизвестен" : author);

        System.out.print("ISBN (Enter - пропустить): ");
        String isbn = scanner.nextLine().trim();
        book.setIsbn(isbn.isEmpty() ? "не указан" : isbn);

        System.out.print("Жанр (Enter - пропустить): ");
        String genre = scanner.nextLine().trim();
        book.setGenre(genre.isEmpty() ? "не указан" : genre);

        System.out.print("Формат (бумажная/EPUB/PDF/аудиокнига) [бумажная]: ");
        String format = scanner.nextLine().trim().toLowerCase();
        if (!format.isEmpty()) book.setFormat(format);

        storage.add(book);
        System.out.println("Добавлено: " + book.getTitle());
    }

    private static void addBookByIsbn() {
        System.out.print("\nISBN (10 или 13 цифр): ");
        String isbn = scanner.nextLine().trim();

        if (isbn.isEmpty()) {
            System.out.println("ISBN не может быть пустым.");
            return;
        }

        Book book = gbService.searchByIsbn(isbn);
        if (book != null) {
            System.out.print("Формат (бумажная/EPUB/PDF/аудиокнига) [бумажная]: ");
            String format = scanner.nextLine().trim();
            if (!format.isEmpty()) book.setFormat(format);
            storage.add(book);
            System.out.println("Добавлено: " + book.getTitle());
        }
    }

    private static void addBookByTitle() {
        System.out.println();
        System.out.print("Название книги: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Название не может быть пустым.");
            return;
        }
        System.out.print("Автор (Enter - пропустить): ");
        String author = scanner.nextLine().trim();

        Book book = gbService.searchByTitleAndAuthor(title, author);
        if (book != null) {
            System.out.print("Формат (бумажная/EPUB/PDF/аудиокнига): ");
            String format = scanner.nextLine().trim();
            if (!format.isEmpty()) book.setFormat(format);
            storage.add(book);
            System.out.println("Добавлено: " + book.getTitle());
        }
    }

    private static void listAllBooks() {
        List<Book> books = storage.getAll();
        if (books.isEmpty()) {
            System.out.println("\nБиблиотека пуста.");
            return;
        }
        System.out.println("\nВсего книг: " + books.size() +
                " | Прочитано: " + storage.countByStatus("Прочитано") +
                " | В процессе: " + storage.countByStatus("В процессе") +
                " | В планах: " + storage.countByStatus("В планах"));
        books.forEach(System.out::println);
    }

    private static void searchBooks() {
        System.out.print("\nПоиск: ");
        String query = scanner.nextLine().trim();
        if (query.isEmpty()) return;

        List<Book> found = storage.search(query);
        if (found.isEmpty()) {
            System.out.println("Ничего не найдено.");
            return;
        }
        System.out.println("Найдено: " + found.size());
        found.forEach(System.out::println);
    }

    private static void editBook() {
        listAllBooks();
        System.out.print("\nID книги: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return;

        try {
            int id = Integer.parseInt(input);
            Optional<Book> opt = storage.findById(id);
            if (opt.isEmpty()) {
                System.out.println("Книга не найдена.");
                return;
            }

            Book book = opt.get();
            System.out.println(book.toDetailedString());

            System.out.println("\nОставьте поле пустым, чтобы не менять.");

            System.out.print("Статус (Прочитано/В процессе/В планах): ");
            String status = scanner.nextLine().trim();
            if (!status.isEmpty()) book.setStatus(status);

            System.out.print("Рейтинг (1-5): ");
            String rating = scanner.nextLine().trim();
            if (!rating.isEmpty()) {
                int r = Integer.parseInt(rating);
                if (r >= 1 && r <= 5) book.setRating(r);
            }

            System.out.print("Прочитано страниц: ");
            String pages = scanner.nextLine().trim();
            if (!pages.isEmpty()) book.setPagesRead(Integer.parseInt(pages));

            System.out.print("Всего страниц: ");
            String total = scanner.nextLine().trim();
            if (!total.isEmpty()) book.setTotalPages(Integer.parseInt(total));

            System.out.print("Рецензия: ");
            String review = scanner.nextLine().trim();
            if (!review.isEmpty()) book.setReview(review);

            storage.update(book);
            System.out.println("Обновлено.");
        } catch (NumberFormatException e) {
            System.out.println("Некорректный ID.");
        }
    }

    private static void deleteBook() {
        listAllBooks();
        System.out.print("\nID книги для удаления: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return;

        try {
            int id = Integer.parseInt(input);
            Optional<Book> opt = storage.findById(id);
            if (opt.isPresent()) {
                System.out.print("Удалить \"" + opt.get().getTitle() + "\"? (да/нет): ");
                if (scanner.nextLine().trim().equalsIgnoreCase("да")) {
                    storage.delete(id);
                    System.out.println("Удалено.");
                }
            } else {
                System.out.println("Книга не найдена.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректный ID.");
        }
    }

    private static void showStats() {
        System.out.println("\nВсего книг: " + storage.getAll().size());
        System.out.println("Прочитано: " + storage.countByStatus("Прочитано"));
        System.out.println("В процессе: " + storage.countByStatus("В процессе"));
        System.out.println("В планах: " + storage.countByStatus("В планах"));
        System.out.printf("Средний рейтинг: %.1f/5\n", storage.avgRating());

        System.out.println("\nЖанры:");
        storage.getGenreStats().forEach((genre, count) ->
                System.out.println("  " + genre + ": " + count));

        List<Book> unread = storage.filterByStatus("В планах");
        if (!unread.isEmpty()) {
            System.out.println("\nНепрочитанные:");
            unread.forEach(b -> System.out.println("  " + b.getTitle() + " - " + b.getAuthor()));
        }
    }

    private static void showBookDetails() {
        listAllBooks();
        System.out.print("\nID книги: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return;

        try {
            int id = Integer.parseInt(input);
            Optional<Book> opt = storage.findById(id);
            if (opt.isPresent()) {
                System.out.println();
                System.out.println(opt.get().toDetailedString());
            } else {
                System.out.println("Книга не найдена.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректный ID.");
        }
    }
}
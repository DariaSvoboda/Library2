package org.example;

import com.google.gson.*;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GoogleBooksService {
    private static final String API_KEY = "AIzaSyA0KLTFoP5a3dUrDQKVl_JQAvnDc5skEb0";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public Book searchByIsbn(String isbn) {
        String cleanIsbn = isbn.replaceAll("[^0-9Xx]", "").trim();

        if (cleanIsbn.isEmpty() || cleanIsbn.length() < 10) {
            System.out.println("Некорректный ISBN. Должен содержать 10 или 13 цифр.");
            return null;
        }

        System.out.println("Поиск по ISBN: " + cleanIsbn);
        return searchByQuery("isbn:" + cleanIsbn);
    }

    public Book searchByTitleAndAuthor(String title, String author) {
        try {
            StringBuilder query = new StringBuilder();
            query.append("intitle:").append(URLEncoder.encode(title, StandardCharsets.UTF_8));
            if (author != null && !author.trim().isEmpty()) {
                query.append("+inauthor:").append(URLEncoder.encode(author.trim(), StandardCharsets.UTF_8));
            }

            System.out.println("Поиск: " + title + " | " + author);
            return searchByQuery(query.toString());
        } catch (Exception e) {
            System.out.println("Ошибка поиска: " + e.getMessage());
            return null;
        }
    }

    private Book searchByQuery(String query) {
        try {
            String url = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&maxResults=1";
            if (!API_KEY.isEmpty()) {
                url += "&key=" + API_KEY;
            }

            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "Library2/1.0")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("Ошибка HTTP: " + response.code());
                    return null;
                }

                String json = response.body().string();
                JsonObject root = gson.fromJson(json, JsonObject.class);

                int totalItems = root.has("totalItems") ? root.get("totalItems").getAsInt() : 0;
                if (totalItems == 0) {
                    System.out.println("Книга не найдена в Google Books.");
                    return null;
                }

                JsonArray items = root.getAsJsonArray("items");
                JsonObject volumeInfo = items.get(0).getAsJsonObject().getAsJsonObject("volumeInfo");

                Book book = new Book();

                if (volumeInfo.has("industryIdentifiers")) {
                    JsonArray identifiers = volumeInfo.getAsJsonArray("industryIdentifiers");
                    for (JsonElement id : identifiers) {
                        JsonObject idObj = id.getAsJsonObject();
                        if ("ISBN_13".equals(idObj.get("type").getAsString())) {
                            book.setIsbn(idObj.get("identifier").getAsString());
                            break;
                        }
                    }
                }
                if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
                    book.setIsbn(query.replace("isbn:", ""));
                }

                book.setTitle(volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Без названия");
                System.out.println("Название: " + book.getTitle());

                if (volumeInfo.has("authors")) {
                    JsonArray authors = volumeInfo.getAsJsonArray("authors");
                    List<String> authorList = new ArrayList<>();
                    for (JsonElement a : authors) {
                        authorList.add(a.getAsString());
                    }
                    book.setAuthor(String.join(", ", authorList));
                } else {
                    book.setAuthor("Неизвестен");
                }
                System.out.println("Автор: " + book.getAuthor());

                if (volumeInfo.has("categories")) {
                    JsonArray categories = volumeInfo.getAsJsonArray("categories");
                    book.setGenre(categories.size() > 0 ? categories.get(0).getAsString() : "Не указан");
                } else {
                    book.setGenre("Не указан");
                }
                System.out.println("Жанр: " + book.getGenre());

                if (volumeInfo.has("pageCount")) {
                    book.setTotalPages(volumeInfo.get("pageCount").getAsInt());
                    System.out.println("Страниц: " + book.getTotalPages());
                }

                book.setFormat("бумажная");
                book.setStatus("В планах");
                book.setDateAdded(java.time.LocalDate.now().toString());

                return book;
            }
        } catch (IOException e) {
            System.out.println("Ошибка сети: " + e.getMessage());
            return null;
        }
    }
}
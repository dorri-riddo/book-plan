package com.example.bookplan.book;

public enum BookSearchType {
    TITLE("title"),
    AUTHOR("authors");

    private final String field;

    BookSearchType(String field) {
        this.field = field;
    }

    public String field() {
        return field;
    }
}

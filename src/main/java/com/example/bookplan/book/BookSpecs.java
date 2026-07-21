package com.example.bookplan.book;

import org.springframework.data.jpa.domain.Specification;

public final class BookSpecs {
    private BookSpecs() {}

    // WHERE b.user_id = ?
    public static Specification<Book> ownedBy(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    // WHERE lower(b.{typeField}) LIKE '%{keyword}%'
    public static Specification<Book> keywordMatches(BookSearchType type, String keyword) {
        BookSearchType target = (type == null) ? BookSearchType.TITLE : type;
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, cb) ->
                cb.like(cb.lower(root.<String>get(target.field())), pattern);
    }
}

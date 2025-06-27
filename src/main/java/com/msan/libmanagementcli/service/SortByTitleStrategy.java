package com.msan.libmanagementcli.service;

import com.msan.libmanagementcli.model.Book;
import java.util.Comparator;
import java.util.List;

/**
 * Implementa la strategia di ordinamento dei libri per titolo.
 */
public class SortByTitleStrategy implements SortStrategy {

    /**
     * Ordina la lista di libri fornita per titolo (case-insensitive).
     *
     * @param books La lista di {@link Book} da ordinare.
     */
    @Override
    public void sort(List<Book> books) {
        if (books == null) {
            return;
        }
        books.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
    }
}
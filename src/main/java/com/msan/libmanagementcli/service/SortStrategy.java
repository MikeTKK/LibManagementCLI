package com.msan.libmanagementcli.service;

import com.msan.libmanagementcli.model.Book;
import java.util.List;

/**
 * Interfaccia per il pattern Strategy, che definisce un contratto
 * per gli algoritmi di ordinamento di una lista di libri.
 */
@FunctionalInterface
public interface SortStrategy {

    /**
     * Ordina la lista di libri fornita, modificandola direttamente (in-place).
     *
     * @param books La lista di {@link Book} da ordinare.
     */
    void sort(List<Book> books);
}
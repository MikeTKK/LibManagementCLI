package com.msan.libmanagementcli.model;

import java.util.Collections;
import java.util.Iterator;

/**
 * Interfaccia che definisce il contratto per un elemento generico della libreria.
 * Serve come componente base per il pattern Composite, permettendo di trattare
 * oggetti singoli ({@link Book}) e composizioni ({@link BookCollection}) in modo uniforme.
 */
public interface LibraryItem {

    /**
     * Restituisce il titolo dell'elemento.
     * @return Il titolo dell'elemento (es. titolo del libro o nome della collezione).
     */
    String getTitle();

    /**
     * Visualizza i dettagli dell'elemento, con un'indentazione per la gerarchia.
     * @param indent La stringa di indentazione da anteporre all'output.
     */
    void display(String indent);

    /**
     * Restituisce l'ISBN dell'elemento.
     * @return L'ISBN, o null se non applicabile (es. per una collezione).
     */
    String getIsbn();

    /**
     * Restituisce l'autore dell'elemento.
     * @return L'autore, o null se non applicabile (es. per una collezione).
     */
    String getAuthor();

    /**
     * Fornisce un iteratore per scorrere i sotto-elementi (Pattern Iterator).
     * L'implementazione di default restituisce un iteratore vuoto, adatta per
     * le "foglie" del pattern Composite come {@link Book}.
     *
     * @return Un iteratore per i figli diretti.
     */
    default Iterator<LibraryItem> createIterator() {
        return Collections.emptyIterator();
    }
}
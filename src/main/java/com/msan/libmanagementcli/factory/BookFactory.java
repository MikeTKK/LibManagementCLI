package com.msan.libmanagementcli.factory;

import com.msan.libmanagementcli.model.Book;
import com.msan.libmanagementcli.model.BookCollection;
import com.msan.libmanagementcli.exceptions.InvalidBookDataException;

/**
 * Fornisce metodi statici per la creazione di oggetti del modello.
 * Centralizza la logica di istanziazione (Simple Factory Pattern).
 */
public class BookFactory {

    /**
     * Crea una nuova istanza di {@link Book} dopo aver validato i campi essenziali.
     * Utilizza il pattern Builder per la costruzione dell'oggetto.
     *
     * @param isbn L'ISBN del libro (obbligatorio).
     * @param title Il titolo del libro (obbligatorio).
     * @param author L'autore del libro (obbligatorio).
     * @param publicationYear L'anno di pubblicazione (opzionale).
     * @param genre Il genere del libro (opzionale).
     * @return Una nuova istanza di Book.
     * @throws InvalidBookDataException se i dati obbligatori non sono validi.
     */
    public static Book createBook(String isbn, String title, String author, int publicationYear, String genre)
            throws InvalidBookDataException {
        
        // Validazione dei campi obbligatori
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new InvalidBookDataException("L'ISBN del libro non può essere nullo o vuoto.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidBookDataException("Il titolo del libro non può essere nullo o vuoto.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new InvalidBookDataException("L'autore del libro non può essere nullo o vuoto.");
        }

        // Creazione tramite Builder
        return new Book.BookBuilder(isbn.trim(), title.trim(), author.trim())
                .publicationYear(publicationYear)
                .genre(genre)
                .build();
    }

    /**
     * Crea una nuova istanza di {@link BookCollection}.
     *
     * @param collectionName Il nome della collezione (obbligatorio).
     * @return Una nuova istanza di BookCollection.
     * @throws InvalidBookDataException se il nome della collezione è nullo o vuoto.
     */
    public static BookCollection createBookCollection(String collectionName) throws InvalidBookDataException {
        if (collectionName == null || collectionName.trim().isEmpty()) {
            throw new InvalidBookDataException("Il nome della collezione non può essere nullo o vuoto.");
        }
        return new BookCollection(collectionName);
    }
}
package com.msan.libmanagementcli.factory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.msan.libmanagementcli.exceptions.InvalidBookDataException;
import com.msan.libmanagementcli.model.Book;
import com.msan.libmanagementcli.model.BookCollection;

/**
 * Classe di test unitari per la classe {@link BookFactory}.
 * Verifica la corretta creazione degli oggetti del modello.
 */
class BookFactoryTest {

    // --- Sezione Test per BookFactory.createBook(...) ---

    /**
     * Testa la creazione di un Book con dati validi e completi.
     */
    @Test
    void testCreateBook_conDatiValidi() throws InvalidBookDataException {
        // Arrange & Act
        Book book = BookFactory.createBook("978-0134685991", "Effective Modern C++", "Scott Meyers", 2014, "Programming");
        
        // Assert
        assertNotNull(book);
        assertEquals("978-0134685991", book.getIsbn());
        assertEquals("Effective Modern C++", book.getTitle());
    }

    /**
     * Testa la creazione di un Book con solo i dati obbligatori.
     */
    @Test
    void testCreateBook_conSoloDatiObbligatori() throws InvalidBookDataException {
        // Arrange & Act
        Book book = BookFactory.createBook("978-0321765723", "Effective Java", "Joshua Bloch", 0, null);
        
        // Assert
        assertNotNull(book);
        assertEquals("Effective Java", book.getTitle());
        assertEquals(0, book.getPublicationYear());
        assertNull(book.getGenre());
    }
    
    /**
     * Testa che la creazione di un Book fallisca se l'ISBN è nullo.
     */
    @Test
    void testCreateBook_conIsbnNullo_lanciaEccezione() {
        // L'asserzione verifica che l'eccezione InvalidBookDataException venga lanciata
        // quando si tenta di creare un libro con ISBN nullo.
        assertThrows(InvalidBookDataException.class, () -> {
            BookFactory.createBook(null, "Titolo", "Autore", 2000, "Genere");
        });
    }

    /**
     * Testa che la creazione di un Book fallisca se il titolo è vuoto.
     */
    @Test
    void testCreateBook_conTitoloVuoto_lanciaEccezione() {
        assertThrows(InvalidBookDataException.class, () -> {
            BookFactory.createBook("ISBN123", "   ", "Autore", 2000, "Genere");
        });
    }

    /**
     * Testa che la creazione di un Book fallisca se l'autore è nullo.
     */
    @Test
    void testCreateBook_conAutoreNullo_lanciaEccezione() {
        assertThrows(InvalidBookDataException.class, () -> {
            BookFactory.createBook("ISBN123", "Titolo", null, 2000, "Genere");
        });
    }

    // --- Sezione Test per BookFactory.createBookCollection(...) ---

    /**
     * Testa la creazione di una BookCollection con un nome valido.
     */
    @Test
    void testCreateBookCollection_conNomeValido() throws InvalidBookDataException {
        // Arrange & Act
        BookCollection collection = BookFactory.createBookCollection("Collezione Fantasy");
        
        // Assert
        assertNotNull(collection);
        assertEquals("Collezione Fantasy", collection.getTitle());
    }

    /**
     * Testa che la creazione di una BookCollection fallisca se il nome è nullo.
     */
    @Test
    void testCreateBookCollection_conNomeNullo_lanciaEccezione() {
        assertThrows(InvalidBookDataException.class, () -> {
            BookFactory.createBookCollection(null);
        });
    }

    /**
     * Testa che la creazione di una BookCollection fallisca se il nome è vuoto.
     */
    @Test
    void testCreateBookCollection_conNomeVuoto_lanciaEccezione() {
        assertThrows(InvalidBookDataException.class, () -> {
            BookFactory.createBookCollection("   ");
        });
    }
}
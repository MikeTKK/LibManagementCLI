package com.msan.libmanagementcli.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.msan.libmanagementcli.exceptions.InvalidBookDataException;

/**
 * Test per la classe {@link Book}.
 * Verifica il Builder, i metodi equals/hashCode e la conversione da/a CSV.
 */
class BookTest {

    // --- Setup ---
    private Book bookCompleto;

    @BeforeEach
    void setUp() {
        // Prepara un oggetto Book di esempio da usare in più test.
        bookCompleto = new Book.BookBuilder("978-3-16-148410-0", "Titolo di Test", "Autore di Test")
                        .publicationYear(2024)
                        .genre("Genere di Test")
                        .build();
    }

    // --- Test per il Builder ---

    /**
     * Testa la creazione di un Book con tutti i campi.
     */
    @Test
    void testBuilder_conTuttiICampi() {
        assertNotNull(bookCompleto);
        assertEquals("978-3-16-148410-0", bookCompleto.getIsbn());
        assertEquals("Titolo di Test", bookCompleto.getTitle());
        assertEquals(2024, bookCompleto.getPublicationYear());
    }

    /**
     * Testa la creazione di un Book con solo i campi obbligatori.
     */
    @Test
    void testBuilder_conSoloCampiObbligatori() {
        Book book = new Book.BookBuilder("ISBN123", "Solo Obbligatori", "Autore Obbligatorio").build();
        
        assertNotNull(book);
        assertEquals("ISBN123", book.getIsbn());
        assertEquals(0, book.getPublicationYear()); // Anno non impostato, default a 0
        assertNull(book.getGenre());            // Genere non impostato, default a null
    }

    // --- Test per equals() e hashCode() ---

    /**
     * Testa l'uguaglianza tra libri basata sull'ISBN.
     */
    @Test
    void testEquals_e_hashCode() {
        // Un oggetto deve essere uguale a se stesso.
        assertTrue(bookCompleto.equals(bookCompleto));
        
        // Due libri con lo stesso ISBN sono considerati uguali.
        Book libroConStessoIsbn = new Book.BookBuilder(bookCompleto.getIsbn(), "Titolo Diverso", "Autore Diverso").build();
        assertTrue(bookCompleto.equals(libroConStessoIsbn));
        assertEquals(bookCompleto.hashCode(), libroConStessoIsbn.hashCode(), "HashCode deve essere uguale per oggetti uguali.");
        
        // Due libri con ISBN diverso non sono uguali.
        Book libroConIsbnDiverso = new Book.BookBuilder("ALTRO-ISBN", "Titolo", "Autore").build();
        assertFalse(bookCompleto.equals(libroConIsbnDiverso));
    }
    
    // --- Test per la Conversione CSV ---

    /**
     * Testa la conversione di un oggetto Book in una stringa CSV.
     */
    @Test
    void testToCsvString() {
        // Arrange
        String expectedCsv = "978-3-16-148410-0,Titolo di Test,Autore di Test,2024,Genere di Test";
        
        // Act
        String actualCsv = bookCompleto.toCsvString();
        
        // Assert
        assertEquals(expectedCsv, actualCsv);
    }

    /**
     * Testa la creazione di un oggetto Book da una stringa CSV valida.
     */
    @Test
    void testFromCsvString_conRigaValida() throws InvalidBookDataException {
        // Arrange
        String csvLine = "978-XYZ,Un Bel Titolo,Un Grande Autore,2023,Avventura";
        
        // Act
        Book book = Book.fromCsvString(csvLine);

        // Assert
        assertNotNull(book);
        assertEquals("978-XYZ", book.getIsbn());
        assertEquals("Un Bel Titolo", book.getTitle());
        assertEquals(2023, book.getPublicationYear());
    }

    /**
     * Testa la creazione di un Book da una stringa CSV con campi opzionali mancanti.
     */
    @Test
    void testFromCsvString_conCampiMancanti() throws InvalidBookDataException {
        // Arrange
        String csvLine = "ISBN-OB,Solo Obbligatori,Autore OB"; // Anno e genere mancanti
        
        // Act
        Book book = Book.fromCsvString(csvLine);

        // Assert
        assertNotNull(book);
        assertEquals(0, book.getPublicationYear(), "L'anno mancante dovrebbe risultare in 0.");
        assertEquals("", book.getGenre(), "Il genere mancante dovrebbe risultare in una stringa vuota.");
    }

    /**
     * Testa che la creazione da una stringa CSV malformata lanci un'eccezione.
     */
    @Test
    void testFromCsvString_conRigaMalformata_lanciaEccezione() {
        // Una riga con meno di 3 campi è considerata malformata.
        String rigaInvalida = "ISBN-SoloQuesto"; 
        
        assertThrows(InvalidBookDataException.class, () -> {
            Book.fromCsvString(rigaInvalida);
        });
        
        // Anche una riga nulla o vuota dovrebbe lanciare un'eccezione.
        assertThrows(InvalidBookDataException.class, () -> {
            Book.fromCsvString(null);
        });
        assertThrows(InvalidBookDataException.class, () -> {
            Book.fromCsvString("   ");
        });
    }
}
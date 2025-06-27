package com.msan.libmanagementcli.dao;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.msan.libmanagementcli.exceptions.LibraryException;
import com.msan.libmanagementcli.model.Book;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test per la classe {@link FileStorageService}.
 * Verifica il salvataggio e il caricamento su file CSV usando file temporanei.
 */
class FileStorageServiceTest {

    // --- Setup per i Test ---
    private StorageService storageService;
    private Book book1;
    private Book book2;
    private File testFile;

    @TempDir
    Path tempDir; // JUnit creerà una cartella temporanea per i nostri file di test.

    @BeforeEach
    void setUp() {
        storageService = new FileStorageService();
        testFile = tempDir.resolve("library_test.csv").toFile();

        book1 = new Book.BookBuilder("ISBN001", "Il Signore degli Anelli", "J.R.R. Tolkien")
                        .publicationYear(1954).genre("Fantasy").build();
        book2 = new Book.BookBuilder("ISBN002", "1984", "George Orwell")
                        .publicationYear(1949).genre("Distopia").build();
    }

    // --- Sezione Test ---

    /**
     * Testa: saveBooks con una lista vuota.
     * Verifica che venga creato un file vuoto.
     */
    @Test
    void testSaveBooks_conListaVuota() throws LibraryException {
        // Arrange
        List<Book> emptyBookList = new ArrayList<>();
        
        // Act
        storageService.saveBooks(emptyBookList, testFile.getAbsolutePath());
        
        // Assert
        assertTrue(testFile.exists(), "Il file CSV dovrebbe esistere.");
        try {
            assertEquals(0, Files.lines(testFile.toPath()).count(), "Il file dovrebbe essere vuoto.");
        } catch (IOException e) {
            fail("Impossibile leggere il file di test per la verifica: " + e.getMessage());
        }
    }

    /**
     * Testa: loadBooks da un file che non esiste.
     * Verifica che restituisca una lista vuota.
     */
    @Test
    void testLoadBooks_daFileNonEsistente() throws LibraryException {
        // Arrange
        File nonExistentFile = tempDir.resolve("file_inesistente.csv").toFile();
        
        // Act
        List<Book> books = storageService.loadBooks(nonExistentFile.getAbsolutePath());
        
        // Assert
        assertNotNull(books);
        assertTrue(books.isEmpty(), "Dovrebbe restituire una lista vuota se il file non esiste.");
    }
    
    /**
     * Testa: loadBooks da un file vuoto.
     * Verifica che restituisca una lista vuota.
     */
    @Test
    void testLoadBooks_daFileVuoto() throws IOException, LibraryException {
        // Arrange
        testFile.createNewFile();
        
        // Act
        List<Book> books = storageService.loadBooks(testFile.getAbsolutePath());
        
        // Assert
        assertNotNull(books);
        assertTrue(books.isEmpty(), "Dovrebbe restituire una lista vuota se il file è vuoto.");
    }

    /**
     * Testa: salvataggio e successivo caricamento di un singolo libro.
     * Verifica che i dati rimangano integri.
     */
    @Test
    void testSaveAndLoad_conSingoloLibro() throws LibraryException {
        // Arrange
        List<Book> booksToSave = Arrays.asList(book1);

        // Act
        storageService.saveBooks(booksToSave, testFile.getAbsolutePath());
        List<Book> loadedBooks = storageService.loadBooks(testFile.getAbsolutePath());
        
        // Assert
        assertNotNull(loadedBooks);
        assertEquals(1, loadedBooks.size());
        
        Book loadedBook = loadedBooks.get(0);
        assertEquals(book1.getIsbn(), loadedBook.getIsbn());
        assertEquals(book1.getTitle(), loadedBook.getTitle());
    }

    /**
     * Testa: salvataggio e successivo caricamento di più libri.
     * Verifica che tutti i libri vengano ripristinati correttamente.
     */
    @Test
    void testSaveAndLoad_conMoltiLibri() throws LibraryException {
        // Arrange
        List<Book> booksToSave = Arrays.asList(book1, book2);

        // Act
        storageService.saveBooks(booksToSave, testFile.getAbsolutePath());
        List<Book> loadedBooks = storageService.loadBooks(testFile.getAbsolutePath());
        
        // Assert
        assertNotNull(loadedBooks);
        assertEquals(2, loadedBooks.size());
        assertTrue(loadedBooks.contains(book1), "Il libro 1 dovrebbe essere nella lista caricata.");
        assertTrue(loadedBooks.contains(book2), "Il libro 2 dovrebbe essere nella lista caricata.");
    }
    
    /**
     * Testa: loadBooks con una riga malformata nel file.
     * Verifica che la riga errata venga saltata e le altre caricate.
     */
    @Test
    void testLoadBooks_conRigaMalformata() throws IOException, LibraryException {
        // Arrange: creo un file con una riga valida e una no.
        String rigaValida = book1.toCsvString();
        String rigaMalformata = "ISBN00X,TitoloIncompleto";
        List<String> righeDaScrivere = Arrays.asList(rigaValida, rigaMalformata);
        Files.write(testFile.toPath(), righeDaScrivere);

        // Act
        List<Book> loadedBooks = storageService.loadBooks(testFile.getAbsolutePath());
        
        // Assert
        assertEquals(1, loadedBooks.size(), "Dovrebbe caricare solo il libro valido.");
        assertEquals(book1.getIsbn(), loadedBooks.get(0).getIsbn());
    }
}
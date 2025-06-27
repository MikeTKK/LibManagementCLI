package com.msan.libmanagementcli.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.msan.libmanagementcli.dao.StorageService;
import com.msan.libmanagementcli.exceptions.BookNotFoundException;
import com.msan.libmanagementcli.exceptions.InvalidBookDataException;
import com.msan.libmanagementcli.exceptions.LibraryException;
import com.msan.libmanagementcli.model.Book;
// import com.msan.libmanagementcli.model.LibraryItem; // RIMOSSO perché segnalato come non utilizzato
// import java.util.ArrayList; // RIMOSSO perché segnalato come non utilizzato
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Test per la classe di logica di business {@link LibraryService}.
 * Utilizza Mockito per simulare le dipendenze (es. {@link StorageService}).
 */
class LibraryServiceTest {

    // --- Setup ---
    private Book book1, book2, book3;

    @BeforeEach
    void setUp() {
        LibraryService.resetInstanceForTesting(); 
        book1 = new Book.BookBuilder("ISBN001", "Effective Java", "Joshua Bloch").build();
        book2 = new Book.BookBuilder("ISBN002", "Clean Code", "Robert C. Martin").build();
        book3 = new Book.BookBuilder("ISBN003", "The Pragmatic Programmer", "Andrew Hunt").build();
    }

    // --- Test Metodo addItem ---

    /**
     * Testa l'aggiunta di un libro valido.
     */
    @Test
    void testAddItem_conLibroValido() throws InvalidBookDataException {
        StorageService localMockStorage = mock(StorageService.class);
        LibraryService service = LibraryService.getInstance(localMockStorage);
        service.addItem(book1);
        assertEquals(1, service.getAllItems().size());
        assertTrue(service.getAllItems().contains(book1));
    }

    /**
     * Testa che l'aggiunta di un libro con ISBN duplicato lanci un'eccezione.
     */
    @Test
    void testAddItem_conIsbnDuplicato_lanciaEccezione() throws InvalidBookDataException {
        StorageService localMockStorage = mock(StorageService.class);
        LibraryService service = LibraryService.getInstance(localMockStorage);
        service.addItem(book1); 
        Book libroDuplicato = new Book.BookBuilder(book1.getIsbn(), "Altro Titolo", "Altro Autore").build();
        assertThrows(InvalidBookDataException.class, () -> service.addItem(libroDuplicato));
    }

    // --- Test Metodi di Ricerca e Rimozione ---

    /**
     * Testa la ricerca di un libro esistente tramite ISBN.
     */
    @Test
    void testFindBookByIsbn_conLibroEsistente() throws InvalidBookDataException {
        StorageService localMockStorage = mock(StorageService.class);
        LibraryService service = LibraryService.getInstance(localMockStorage);
        service.addItem(book1);
        Optional<Book> trovato = service.findBookByIsbn(book1.getIsbn());
        assertTrue(trovato.isPresent());
        assertEquals(book1, trovato.get());
    }

    /**
     * Testa la rimozione di un libro esistente.
     */
    @Test
    void testRemoveItemByIsbn_conLibroEsistente() throws InvalidBookDataException, BookNotFoundException {
        StorageService localMockStorage = mock(StorageService.class);
        LibraryService service = LibraryService.getInstance(localMockStorage);
        service.addItem(book1);
        service.removeItemByIsbn(book1.getIsbn());
        assertEquals(0, service.getAllItems().size());
    }

    /**
     * Testa che la rimozione di un libro non esistente lanci un'eccezione.
     */
    @Test
    void testRemoveItemByIsbn_conLibroNonEsistente_lanciaEccezione() {
        StorageService localMockStorage = mock(StorageService.class);
        LibraryService service = LibraryService.getInstance(localMockStorage);
        assertThrows(BookNotFoundException.class, () -> service.removeItemByIsbn("ISBN_INESISTENTE"));
    }

    // --- Test Metodo updateBook ---

    /**
     * Testa l'aggiornamento di un libro esistente con dati validi.
     */
    @Test
    void testUpdateBook_conDatiValidi() throws InvalidBookDataException, BookNotFoundException {
        StorageService localMockStorage = mock(StorageService.class);
        LibraryService service = LibraryService.getInstance(localMockStorage);
        service.addItem(book1);
        Book datiAggiornati = new Book.BookBuilder(book1.getIsbn(), "Titolo Aggiornato", "Autore Aggiornato").build();
        service.updateBook(book1.getIsbn(), datiAggiornati);
        Book libroRecuperato = service.findBookByIsbn(book1.getIsbn()).get();
        assertEquals("Titolo Aggiornato", libroRecuperato.getTitle());
    }

    /**
     * Testa che l'aggiornamento a un ISBN già usato da un altro libro lanci un'eccezione.
     */
    @Test
    void testUpdateBook_conNuovoIsbnDuplicato_lanciaEccezione() throws InvalidBookDataException {
        StorageService localMockStorage = mock(StorageService.class);
        LibraryService service = LibraryService.getInstance(localMockStorage);
        service.addItem(book1);
        service.addItem(book2);
        Book datiAggiornati = new Book.BookBuilder(book2.getIsbn(), "Titolo", "Autore").build();
        assertThrows(InvalidBookDataException.class, () -> service.updateBook(book1.getIsbn(), datiAggiornati));
    }
    
    // --- Test per l'Ordinamento (Strategy) ---

    /**
     * Testa l'ordinamento per autore.
     */
    @Test
    void testGetSortedBooks_perAutore() throws InvalidBookDataException {
        StorageService localMockStorage = mock(StorageService.class);
        LibraryService service = LibraryService.getInstance(localMockStorage);
        service.addItem(book1); // Autore: Bloch
        service.addItem(book2); // Autore: Martin
        service.addItem(book3); // Autore: Hunt
        
        service.setSortStrategy(new SortByAuthorStrategy());
        List<Book> libriOrdinati = service.getSortedBooks();
        
        assertEquals(book3.getAuthor(), libriOrdinati.get(0).getAuthor(), "Il primo libro dovrebbe essere di Hunt.");
        assertEquals(book1.getAuthor(), libriOrdinati.get(1).getAuthor(), "Il secondo libro dovrebbe essere di Bloch.");
        assertEquals(book2.getAuthor(), libriOrdinati.get(2).getAuthor(), "Il terzo libro dovrebbe essere di Martin.");
    }

    // --- Test per Salvataggio e Caricamento (interazione con Mock) ---

    /**
     * Testa che il metodo saveLibrary chiami correttamente lo storage service.
     */
    @Test
    void testSaveLibrary_chiamaCorrettamenteStorageService() throws LibraryException, InvalidBookDataException {
        StorageService localMockStorage = mock(StorageService.class);
        LibraryService service = LibraryService.getInstance(localMockStorage);
        service.addItem(book1);
        String percorsoTest = "test.csv";

        service.saveLibrary(percorsoTest);

        // Verifica che il metodo saveBooks del mock sia stato chiamato una volta
        // con la lista corretta e il percorso corretto.
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Book>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(localMockStorage).saveBooks(listCaptor.capture(), eq(percorsoTest));
        assertEquals(1, listCaptor.getValue().size());
        assertEquals(book1, listCaptor.getValue().get(0));
    }
    
    /**
     * Testa che il metodo loadLibrary rimpiazzi i dati correnti con quelli caricati.
     */
    @Test
    void testLoadLibrary_sostituisceDatiCorrenti() throws LibraryException, InvalidBookDataException {
        StorageService localMockStorage = mock(StorageService.class);
        LibraryService service = LibraryService.getInstance(localMockStorage);
        service.addItem(book1); // Aggiungo un libro che deve sparire dopo il caricamento.
        
        List<Book> libriDaCaricare = Arrays.asList(book2, book3);
        String percorsoTest = "libreria_da_caricare.csv";
        // Dico al mock cosa restituire quando loadBooks viene chiamato
        when(localMockStorage.loadBooks(percorsoTest)).thenReturn(libriDaCaricare);

        service.loadLibrary(percorsoTest);

        assertEquals(2, service.getAllItems().size());
        assertTrue(service.getAllItems().contains(book2));
        assertFalse(service.getAllItems().contains(book1)); // Il vecchio libro non deve più esserci.
    }
}
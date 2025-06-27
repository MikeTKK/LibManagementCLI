package com.msan.libmanagementcli.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Test per la classe {@link BookCollection}.
 * Verifica il funzionamento dei pattern Composite e Iterator.
 */
class BookCollectionTest {

    // --- Setup ---
    private BookCollection mainCollection;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        mainCollection = new BookCollection("Collezione Principale");
        book1 = new Book.BookBuilder("ISBN001", "Libro Uno", "Autore Alpha").build();
        book2 = new Book.BookBuilder("ISBN002", "Libro Due", "Autore Beta").build();
    }

    // --- Sezione Test Costruttore ---

    /**
     * Testa la creazione di una BookCollection con un nome valido.
     */
    @Test
    void testCostruttore_conNomeValido() {
        assertEquals("Collezione Principale", mainCollection.getTitle());
        assertNotNull(mainCollection.getItems());
        assertTrue(mainCollection.getItems().isEmpty());
    }

    /**
     * Testa che il costruttore lanci un'eccezione per un nome nullo.
     */
    @Test
    void testCostruttore_conNomeNullo_lanciaEccezione() {
        assertThrows(IllegalArgumentException.class, () -> new BookCollection(null));
    }

    /**
     * Testa che il costruttore lanci un'eccezione per un nome vuoto.
     */
    @Test
    void testCostruttore_conNomeVuoto_lanciaEccezione() {
        assertThrows(IllegalArgumentException.class, () -> new BookCollection("   "));
    }

    // --- Sezione Test Metodi Base ---

    /**
     * Verifica che ISBN e Autore di una collezione siano null.
     */
    @Test
    void testGetIsbnEGetAuthor_restituisceNull() {
        assertNull(mainCollection.getIsbn());
        assertNull(mainCollection.getAuthor());
    }

    /**
     * Testa l'aggiunta di un {@link Book} valido alla collezione.
     */
    @Test
    void testAddItem_conLibroValido() {
        mainCollection.addItem(book1);
        assertEquals(1, mainCollection.getItems().size());
        assertTrue(mainCollection.getItems().contains(book1));
    }

    /**
     * Testa che l'aggiunta di un item nullo venga ignorata.
     */
    @Test
    void testAddItem_conItemNullo() {
        mainCollection.addItem(null);
        assertEquals(0, mainCollection.getItems().size());
    }

    /**
     * Testa l'aggiunta di una {@link BookCollection} annidata.
     */
    @Test
    void testAddItem_conCollezioneAnnidata() {
        BookCollection collezioneAnnidata = new BookCollection("Sotto-Collezione");
        mainCollection.addItem(collezioneAnnidata);
        assertEquals(1, mainCollection.getItems().size());
        assertTrue(mainCollection.getItems().contains(collezioneAnnidata));
    }

    /**
     * Testa la rimozione di un item presente nella collezione.
     */
    @Test
    void testRemoveItem_conItemEsistente() {
        mainCollection.addItem(book1);
        mainCollection.addItem(book2);
        
        mainCollection.removeItem(book1);
        
        assertEquals(1, mainCollection.getItems().size());
        assertFalse(mainCollection.getItems().contains(book1));
        assertTrue(mainCollection.getItems().contains(book2));
    }

    /**
     * Verifica che getItems() restituisca una lista non modificabile.
     */
    @Test
    void testGetItems_restituisceListaNonModificabile() {
        mainCollection.addItem(book1);
        List<LibraryItem> itemsRecuperati = mainCollection.getItems();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            itemsRecuperati.add(book2); // Tentativo di modifica
        });
    }

    // --- Sezione Test per l'Iteratore ---

    /**
     * Testa l'iteratore su una collezione vuota.
     */
    @Test
    void testCreateIterator_suCollezioneVuota() {
        Iterator<LibraryItem> iterator = mainCollection.createIterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    /**
     * Testa che l'iteratore scorra correttamente gli elementi.
     */
    @Test
    void testCreateIterator_conElementi() {
        mainCollection.addItem(book1);
        mainCollection.addItem(book2);
        Iterator<LibraryItem> iterator = mainCollection.createIterator();

        // Itera e verifica gli elementi
        assertTrue(iterator.hasNext());
        assertEquals(book1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(book2, iterator.next());
        assertFalse(iterator.hasNext());
    }
    
    /**
     * Verifica che il metodo remove() dell'iteratore lanci l'eccezione attesa.
     */
    @Test
    void testIteratorRemove_lanciaEccezione() {
        mainCollection.addItem(book1);
        Iterator<LibraryItem> iterator = mainCollection.createIterator();
        iterator.next(); // Avanza al primo elemento
        
        assertThrows(UnsupportedOperationException.class, () -> iterator.remove());
    }
}
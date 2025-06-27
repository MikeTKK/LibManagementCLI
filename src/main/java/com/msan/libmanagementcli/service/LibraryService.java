package com.msan.libmanagementcli.service;

import com.msan.libmanagementcli.dao.StorageService;
import com.msan.libmanagementcli.exceptions.BookNotFoundException;
import com.msan.libmanagementcli.exceptions.InvalidBookDataException;
import com.msan.libmanagementcli.exceptions.LibraryException;
import com.msan.libmanagementcli.model.Book;
import com.msan.libmanagementcli.model.LibraryItem;
import com.msan.libmanagementcli.utils.ConsoleLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servizio applicativo per la gestione della logica di business della libreria.
 * Implementa il pattern Singleton e gestisce la collezione di {@link LibraryItem}.
 * Utilizza {@link StorageService} per la persistenza e {@link SortStrategy} per l'ordinamento.
 */
public class LibraryService {

    // --- Singleton ---
    private static LibraryService instance;
    private static final ConsoleLogger logger = ConsoleLogger.getInstance();

    // --- Campi d'Istanza ---
    private List<LibraryItem> items;
    private final StorageService storageService;
    private SortStrategy sortStrategy;
    private String currentFilePath;

    /**
     * Costruttore privato per implementare il pattern Singleton.
     */
    private LibraryService(StorageService storageService) {
        this.items = new ArrayList<>();
        this.storageService = storageService;
        this.sortStrategy = new SortByTitleStrategy(); // Strategia di ordinamento di default
        logger.logInfo("LibraryService (Semplificato) inizializzato.");
    }

    /**
     * Restituisce l'unica istanza di LibraryService (Singleton).
     * @param storageService L'implementazione di StorageService da utilizzare.
     * @return L'istanza singleton di LibraryService.
     */
    public static synchronized LibraryService getInstance(StorageService storageService) {
        if (storageService == null) {
            throw new IllegalArgumentException("StorageService non può essere nullo.");
        }
        if (instance == null) {
            instance = new LibraryService(storageService);
        }
        return instance;
    }

    /**
     * Metodo per resettare l'istanza del Singleton (esclusivamente per testing).
     */
    static void resetInstanceForTesting() {
        instance = null;
    }

    // --- Gestione Percorso File e Ordinamento ---

    /**
     * Imposta il percorso del file CSV predefinito.
     */
    public void setCurrentFilePath(String filePath) {
        this.currentFilePath = filePath;
        logger.logInfo("Percorso file corrente impostato a: " + (filePath != null ? filePath : "non impostato"));
    }

    /**
     * Restituisce il percorso del file CSV predefinito.
     */
    public String getCurrentFilePath() {
        return this.currentFilePath;
    }

    /**
     * Imposta la strategia di ordinamento per i libri.
     */
    public void setSortStrategy(SortStrategy strategy) {
        this.sortStrategy = strategy;
        logger.logInfo("Strategia di ordinamento impostata a: " + (strategy != null ? strategy.getClass().getSimpleName() : "Nessuna"));
    }

    /**
     * Restituisce la strategia di ordinamento attualmente configurata.
     */
    public SortStrategy getSortStrategy() {
        return this.sortStrategy;
    }

    /**
     * Restituisce una nuova lista di libri ordinati secondo la strategia corrente.
     */
    public List<Book> getSortedBooks() {
        List<Book> booksToSort = this.items.stream()
            .filter(Book.class::isInstance)
            .map(Book.class::cast)
            .collect(Collectors.toList()); 

        if (this.sortStrategy != null && !booksToSort.isEmpty()) {
            this.sortStrategy.sort(booksToSort); 
        }
        return booksToSort; 
    }
    
    /**
     * Restituisce una vista non modificabile di tutti gli {@link LibraryItem} nella libreria.
     */
    public List<LibraryItem> getAllItems() {
        return Collections.unmodifiableList(new ArrayList<>(this.items));
    }

    // --- Operazioni CRUD e Ricerca ---

    /**
     * Aggiunge un {@link LibraryItem} alla libreria.
     * Per i {@link Book}, verifica l'unicità dell'ISBN.
     * @param item L'item da aggiungere.
     * @throws InvalidBookDataException se l'item o i suoi dati essenziali non sono validi.
     */
    public void addItem(LibraryItem item) throws InvalidBookDataException {
        if (item == null) {
            throw new InvalidBookDataException("L'item da aggiungere non può essere nullo.");
        }
        if (item instanceof Book) {
            Book book = (Book) item;
            if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
                throw new InvalidBookDataException("L'ISBN del libro non può essere nullo o vuoto.");
            }
            boolean exists = this.items.stream()
                .filter(Book.class::isInstance)
                .map(Book.class::cast)
                .anyMatch(b -> book.getIsbn().equals(b.getIsbn()));
            if (exists) {
                throw new InvalidBookDataException("Un libro con ISBN " + book.getIsbn() + " esiste già.");
            }
        }
        this.items.add(item);
        logger.logInfo("Item aggiunto: " + item.getTitle());
    }

    /**
     * Rimuove un libro dalla libreria tramite il suo ISBN.
     * @param isbn L'ISBN del libro da rimuovere.
     * @throws InvalidBookDataException se l'ISBN fornito non è valido.
     * @throws BookNotFoundException se il libro non viene trovato.
     */
    public void removeItemByIsbn(String isbn) throws InvalidBookDataException, BookNotFoundException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new InvalidBookDataException("L'ISBN per la rimozione non può essere nullo o vuoto.");
        }
        boolean removed = this.items.removeIf(item -> 
            item instanceof Book && isbn.equals(((Book) item).getIsbn())
        );
        if (removed) {
            logger.logInfo("Libro rimosso con ISBN: " + isbn);
        } else {
            throw new BookNotFoundException("Libro con ISBN " + isbn + " non trovato per la rimozione.");
        }
    }

    /**
     * Aggiorna i dati di un libro esistente.
     * @param oldIsbn L'ISBN corrente del libro da aggiornare.
     * @param updatedBookData Oggetto {@link Book} con i nuovi dati.
     * @throws InvalidBookDataException se i dati forniti non sono validi.
     * @throws BookNotFoundException se il libro con oldIsbn non viene trovato.
     */
    public void updateBook(String oldIsbn, Book updatedBookData) throws InvalidBookDataException, BookNotFoundException {
        if (oldIsbn == null || oldIsbn.trim().isEmpty() || updatedBookData == null || 
            updatedBookData.getIsbn() == null || updatedBookData.getIsbn().trim().isEmpty()) {
            throw new InvalidBookDataException("Dati per l'aggiornamento non validi (ISBN o dati libro nulli/vuoti).");
        }

        Book bookToUpdate = findBookByIsbn(oldIsbn)
                .orElseThrow(() -> new BookNotFoundException("Libro con ISBN " + oldIsbn + " non trovato per l'aggiornamento."));

        String newIsbn = updatedBookData.getIsbn();
        if (!oldIsbn.equals(newIsbn)) {
            boolean newIsbnExistsForOtherBook = this.items.stream()
                .filter(item -> item instanceof Book && !((Book) item).getIsbn().equals(oldIsbn))
                .anyMatch(b -> newIsbn.equals(((Book)b).getIsbn()));
            if (newIsbnExistsForOtherBook) {
                throw new InvalidBookDataException("Impossibile aggiornare ISBN a " + newIsbn + " poiché è già utilizzato.");
            }
        }
        
        bookToUpdate.setTitle(updatedBookData.getTitle());
        bookToUpdate.setAuthor(updatedBookData.getAuthor());
        bookToUpdate.setIsbn(newIsbn); 
        bookToUpdate.setPublicationYear(updatedBookData.getPublicationYear());
        bookToUpdate.setGenre(updatedBookData.getGenre());

        logger.logInfo("Libro aggiornato: ISBN " + newIsbn);
    }

    /**
     * Trova un libro tramite il suo ISBN.
     * @param isbn L'ISBN del libro da cercare.
     * @return Un {@link Optional} contenente il libro se trovato.
     * @throws InvalidBookDataException se l'ISBN fornito non è valido.
     */
    public Optional<Book> findBookByIsbn(String isbn) throws InvalidBookDataException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new InvalidBookDataException("L'ISBN per la ricerca non può essere nullo o vuoto.");
        }
        return this.items.stream()
            .filter(Book.class::isInstance)
            .map(Book.class::cast)
            .filter(book -> isbn.equals(book.getIsbn()))
            .findFirst();
    }

    /**
     * Trova libri il cui titolo contiene la stringa di ricerca (case-insensitive).
     * @param titleQuery La stringa da cercare.
     * @return Una lista di libri corrispondenti.
     * @throws InvalidBookDataException se il titolo fornito non è valido.
     */
    public List<Book> findBooksByTitle(String titleQuery) throws InvalidBookDataException {
        if (titleQuery == null || titleQuery.trim().isEmpty()) {
            throw new InvalidBookDataException("Il titolo per la ricerca non può essere nullo o vuoto.");
        }
        String lowerCaseQuery = titleQuery.toLowerCase();
        return this.items.stream()
            .filter(Book.class::isInstance)
            .map(Book.class::cast)
            .filter(book -> book.getTitle() != null && book.getTitle().toLowerCase().contains(lowerCaseQuery))
            .collect(Collectors.toList());
    }

    /**
     * Trova libri il cui autore contiene la stringa di ricerca (case-insensitive).
     * @param authorQuery La stringa da cercare.
     * @return Una lista di libri corrispondenti.
     * @throws InvalidBookDataException se l'autore fornito non è valido.
     */
    public List<Book> findBooksByAuthor(String authorQuery) throws InvalidBookDataException {
        if (authorQuery == null || authorQuery.trim().isEmpty()) {
            throw new InvalidBookDataException("L'autore per la ricerca non può essere nullo o vuoto.");
        }
        String lowerCaseQuery = authorQuery.toLowerCase();
        return this.items.stream()
            .filter(Book.class::isInstance)
            .map(Book.class::cast)
            .filter(book -> book.getAuthor() != null && book.getAuthor().toLowerCase().contains(lowerCaseQuery))
            .collect(Collectors.toList());
    }

    // --- Persistenza ---

    /**
     * Carica i libri dal file specificato. Sostituisce la collezione corrente.
     * @param filePath Il percorso del file.
     * @throws LibraryException se si verifica un errore durante il caricamento.
     */
    public void loadLibrary(String filePath) throws LibraryException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new LibraryException("Il percorso del file per il caricamento non può essere nullo o vuoto.");
        }
        logger.logInfo("Caricamento libreria da: " + filePath);
        try {
            List<Book> loadedBooks = storageService.loadBooks(filePath); 
            this.items.clear(); 
            if (loadedBooks != null) { 
                this.items.addAll(loadedBooks);
            }
            this.currentFilePath = filePath;
            logger.logInfo("Libreria caricata. Items: " + this.items.size());
        } catch (LibraryException e) {
            logger.logError("Fallimento caricamento libreria da " + filePath, e);
            throw e; 
        }
    }

    /**
     * Salva i libri nel file predefinito, se impostato.
     * @throws LibraryException se il percorso non è impostato o errore di salvataggio.
     */
    public void saveLibrary() throws LibraryException {
        if (this.currentFilePath == null || this.currentFilePath.trim().isEmpty()) {
            throw new LibraryException("Percorso del file per il salvataggio non impostato.");
        }
        saveLibrary(this.currentFilePath);
    }

    /**
     * Salva i libri (solo {@link Book}) nel file specificato.
     * @param filePath Il percorso del file.
     * @throws LibraryException se il percorso è nullo/vuoto o errore di salvataggio.
     */
    public void saveLibrary(String filePath) throws LibraryException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new LibraryException("Il percorso del file per il salvataggio non può essere nullo o vuoto.");
        }
        
        List<Book> booksToSave = this.items.stream()
            .filter(Book.class::isInstance)
            .map(Book.class::cast)
            .collect(Collectors.toList());

        logger.logInfo("Salvataggio di " + booksToSave.size() + " libri su: " + filePath);
        try {
            storageService.saveBooks(booksToSave, filePath); 
            this.currentFilePath = filePath; 
            logger.logInfo("Libreria salvata con successo.");
        } catch (LibraryException e) {
            logger.logError("Fallimento salvataggio libreria su " + filePath, e);
            throw e;
        }
    }
}
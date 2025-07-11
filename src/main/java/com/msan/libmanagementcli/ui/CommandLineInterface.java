package com.msan.libmanagementcli.ui;

import com.msan.libmanagementcli.exceptions.BookNotFoundException;
import com.msan.libmanagementcli.exceptions.InvalidBookDataException;
import com.msan.libmanagementcli.exceptions.LibraryException;
import com.msan.libmanagementcli.factory.BookFactory;
import com.msan.libmanagementcli.model.Book;
import com.msan.libmanagementcli.service.LibraryService;
import com.msan.libmanagementcli.service.SortByAuthorStrategy;
import com.msan.libmanagementcli.service.SortStrategy;
import com.msan.libmanagementcli.service.SortByTitleStrategy;
import com.msan.libmanagementcli.utils.ConsoleLogger;
import com.msan.libmanagementcli.utils.InputValidator;

import java.io.File;
import java.util.ArrayList; 
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Gestisce l'interfaccia utente a riga di comando (CLI) per l'applicazione
 * di gestione della libreria.
 */
public class CommandLineInterface {
    private final LibraryService libraryService;
    private final Scanner scanner;
    private static final ConsoleLogger logger = ConsoleLogger.getInstance();

    // Costanti per il percorso e il nome del file di default
    private static final String DEFAULT_DIRECTORY_NAME = "LeMieLibrerie";
    private static final String DEFAULT_FILENAME = "dati_libreria_default.csv";

    /**
     * Costruisce l'interfaccia a riga di comando.
     * @param libraryService Il servizio della libreria da utilizzare. Non può essere nullo.
     */
    public CommandLineInterface(LibraryService libraryService) {
        if (libraryService == null) {
            throw new IllegalArgumentException("LibraryService non può essere nullo.");
        }
        this.libraryService = libraryService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Avvia il ciclo principale dell'interfaccia utente.
     * Gestisce l'input dell'utente e le chiamate al servizio della libreria.
     */
    public void start() {
        logger.logInfo("Interfaccia a riga di comando avviata.");
        System.out.println("Benvenuto nel Sistema di Gestione Libreria Personale!");

        String fullDefaultPath = DEFAULT_DIRECTORY_NAME + File.separator + DEFAULT_FILENAME;

        System.out.print("Inserisci il percorso del file dati (es. " + DEFAULT_DIRECTORY_NAME + File.separator + "nomefile.csv o un percorso completo).\n" +
                           "Premi Invio per usare il default (" + fullDefaultPath + "): ");
        String userInputPath = InputValidator.sanitizeString(scanner.nextLine());
        String effectiveFilePath;

        if (userInputPath == null || userInputPath.isEmpty()) {
            effectiveFilePath = fullDefaultPath;
            System.out.println("Nessun percorso fornito. Si utilizzerà il percorso predefinito: " + effectiveFilePath);
        } else {
            File providedFile = new File(userInputPath);
            if (providedFile.getParent() == null) {
                // L'utente ha fornito solo un nome di file, lo mettiamo nella directory di default
                effectiveFilePath = DEFAULT_DIRECTORY_NAME + File.separator + userInputPath;
                System.out.println("Nome file fornito. Verrà utilizzato/creato in: " + effectiveFilePath);
            } else {
                // L'utente ha fornito un percorso (relativo o assoluto)
                effectiveFilePath = userInputPath;
            }
        }
        
        ensureDirectoryExistsForFile(effectiveFilePath);
        libraryService.setCurrentFilePath(effectiveFilePath);

        try {
            libraryService.loadLibrary(effectiveFilePath);
            System.out.println("Libreria gestita tramite: " + libraryService.getCurrentFilePath());
        } catch (LibraryException e) {
            System.err.println("AVVISO: Durante il tentativo di caricamento iniziale da '" + effectiveFilePath + "': " + e.getMessage());
            logger.logWarning("Impossibile caricare la libreria da " + effectiveFilePath + ". Si parte con una libreria vuota/nuova.", e);
        }

        boolean running = true;
        while (running) {
            showMenu();
            System.out.print("Inserisci la tua scelta: ");
            String choice = InputValidator.sanitizeString(scanner.nextLine());

            try {
                switch (choice) {
                    case "1": addBook(); break;
                    case "2": viewAllBooks(); break;
                    case "3": findBook(); break;
                    case "4": removeBook(); break;
                    case "5": updateBook(); break;
                    case "6": setSortStrategy(); break;
                    case "7": saveLibraryData(); break;
                    case "8": loadLibraryData(); break;
                    case "0": running = false; break;
                    default: System.out.println("Scelta non valida. Riprova.");
                }
            } catch (BookNotFoundException | InvalidBookDataException e) {
                System.err.println("ERRORE: " + e.getMessage());
                logger.logWarning("Operazione utente fallita: " + e.getMessage());
            } catch (LibraryException e) {
                System.err.println("ERRORE DI LIBRERIA: " + e.getMessage());
                logger.logError("Errore di sistema della libreria: " + e.getMessage(), e);
            } catch (Exception e) {
                System.err.println("ERRORE INASPETTATO: Si è verificato un errore imprevisto.");
                logger.logError("Errore di sistema inaspettato nella CLI: " + e.getMessage(), e);
            }
            if (running) {
                System.out.println("\nPremi Invio per continuare...");
                scanner.nextLine(); 
            }
        }
        
        handleSaveOnExit();
        System.out.println("Uscita dal Sistema di Gestione Libreria. Arrivederci!");
        logger.logInfo("Interfaccia a riga di comando terminata.");
        scanner.close();
    }

    /**
     * Assicura che la directory genitore del percorso file specificato esista.
     * Se non esiste, tenta di crearla.
     * @param filePath Il percorso completo del file per cui assicurare l'esistenza della directory genitore.
     */
    private void ensureDirectoryExistsForFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) return;

        File file = new File(filePath);
        File parentDir = file.getParentFile(); 

        if (parentDir != null && !parentDir.exists()) {
            System.out.println("La directory '" + parentDir.getAbsolutePath() + "' non esiste. Tentativo di creazione...");
            if (parentDir.mkdirs()) {
                System.out.println("Directory creata con successo: " + parentDir.getAbsolutePath());
            } else {
                System.err.println("ATTENZIONE: Impossibile creare la directory: " + parentDir.getAbsolutePath() + 
                                   ". Il salvataggio o caricamento potrebbe fallire se il percorso non è scrivibile.");
            }
        }
    }
    
    /**
     * Chiede all'utente se vuole salvare la libreria prima di uscire,
     * se un percorso file è attualmente impostato.
     */
    private void handleSaveOnExit() {
        String currentPath = libraryService.getCurrentFilePath();
        if (currentPath != null && !currentPath.isEmpty()) {
            System.out.print("Vuoi salvare le modifiche su '" + currentPath + "' prima di uscire? (si/no): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("si")) {
                try {
                    ensureDirectoryExistsForFile(currentPath); // Assicura che la dir esista anche qui
                    libraryService.saveLibrary(); // Usa il currentFilePath impostato nel service
                    System.out.println("Libreria salvata.");
                } catch (LibraryException e) {
                    System.err.println("ERRORE: Impossibile salvare la libreria prima di uscire: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Mostra il menu principale delle opzioni all'utente.
     */
    private void showMenu() {
        System.out.println("\n--- Menu Libreria (Semplificato) ---");
        System.out.println("1. Aggiungi Libro");
        System.out.println("2. Visualizza Tutti i Libri");
        System.out.println("3. Trova Libro (per ISBN, Titolo, Autore)");
        System.out.println("4. Rimuovi Libro (per ISBN)");
        System.out.println("5. Aggiorna Libro (per ISBN)");
        System.out.println("6. Imposta Strategia di Ordinamento Libri");
        System.out.println("7. Salva Libreria su File");
        System.out.println("8. Carica Libreria da File");
        System.out.println("0. Esci");
        System.out.println("-------------------------------------");
    }

    /**
     * Gestisce l'aggiunta di un nuovo libro.
     */
    private void addBook() throws InvalidBookDataException {
        System.out.println("\n--- Aggiungi Nuovo Libro ---");
        System.out.print("Inserisci ISBN: ");
        String isbn = InputValidator.sanitizeString(scanner.nextLine());

        System.out.print("Inserisci Titolo: ");
        String title = InputValidator.sanitizeString(scanner.nextLine());

        System.out.print("Inserisci Autore: ");
        String author = InputValidator.sanitizeString(scanner.nextLine());
        
        System.out.print("Inserisci Anno di Pubblicazione (opzionale, premi Invio per saltare): ");
        String yearStr = InputValidator.sanitizeString(scanner.nextLine());
        int year = 0;
        if (yearStr != null && !yearStr.isEmpty()) {
            if (InputValidator.isValidYear(yearStr)) {
                year = Integer.parseInt(yearStr);
            } else {
                 System.out.println("Formato anno non valido. L'anno non verrà impostato.");
            }
        }

        System.out.print("Inserisci Genere (opzionale, premi Invio per saltare): ");
        String genre = InputValidator.sanitizeString(scanner.nextLine());

        Book newBook = BookFactory.createBook(isbn, title, author, year, genre);
        libraryService.addItem(newBook);
        System.out.println("Libro '" + newBook.getTitle() + "' aggiunto con successo!");
    }

    /**
     * Visualizza tutti i libri, ordinati secondo la strategia corrente.
     */
    private void viewAllBooks() {
        System.out.println("\n--- Tutti i Libri in Libreria ---");
        List<Book> books = libraryService.getSortedBooks(); 
        
        if (books.isEmpty()) {
            System.out.println("La libreria è vuota.");
            return;
        }
        // Mostra la strategia di ordinamento corrente
        SortStrategy currentStrategy = libraryService.getSortStrategy();
        String strategyName = "Default (Titolo)"; // Assumendo che SortByTitleStrategy sia il default se null o non riconoscibile
        if (currentStrategy instanceof SortByTitleStrategy) {
            strategyName = "Titolo";
        } else if (currentStrategy instanceof SortByAuthorStrategy) {
            strategyName = "Autore";
        } else if (currentStrategy != null) {
            strategyName = currentStrategy.getClass().getSimpleName().replace("SortBy", "").replace("Strategy","");
        }
        System.out.println("(Ordinati per: " + strategyName + ")");

        for (Book book : books) {
            book.display("  "); // Aggiunge una piccola indentazione per ogni libro
        }
        System.out.println("--------------------------------");
    }

    /**
     * Gestisce la ricerca di libri.
     */
    private void findBook() throws InvalidBookDataException {
        System.out.println("\n--- Trova Libro ---");
        System.out.print("Cerca per (1-ISBN, 2-Titolo, 3-Autore): ");
        String criteriaChoice = InputValidator.sanitizeString(scanner.nextLine());
        
        List<Book> foundBooks = new ArrayList<>(); 

        switch (criteriaChoice) {
            case "1":
                System.out.print("Inserisci ISBN da cercare: ");
                String isbn = InputValidator.sanitizeString(scanner.nextLine());
                Optional<Book> bookOpt = libraryService.findBookByIsbn(isbn);
                if (bookOpt.isPresent()) {
                    System.out.println("Libro trovato:");
                    bookOpt.get().display("  ");
                } else {
                    System.out.println("Nessun libro trovato con ISBN: " + isbn);
                }
                return; 
            case "2":
                System.out.print("Inserisci Titolo (o parte) da cercare: ");
                String titleQuery = InputValidator.sanitizeString(scanner.nextLine());
                foundBooks = libraryService.findBooksByTitle(titleQuery);
                break;
            case "3":
                System.out.print("Inserisci Autore (o parte) da cercare: ");
                String authorQuery = InputValidator.sanitizeString(scanner.nextLine());
                foundBooks = libraryService.findBooksByAuthor(authorQuery);
                break;
            default:
                System.out.println("Criterio di ricerca non valido.");
                return;
        }

        if (!foundBooks.isEmpty()) {
            System.out.println("Libri trovati (" + foundBooks.size() + "):");
            for (Book book : foundBooks) {
                book.display("  ");
            }
        } else if (criteriaChoice.equals("2") || criteriaChoice.equals("3")) { 
            System.out.println("Nessun libro trovato con i criteri specificati.");
        }
    }

    /**
     * Gestisce la rimozione di un libro.
     */
    private void removeBook() throws InvalidBookDataException, BookNotFoundException {
        System.out.println("\n--- Rimuovi Libro ---");
        System.out.print("Inserisci ISBN del libro da rimuovere: ");
        String isbn = InputValidator.sanitizeString(scanner.nextLine());
        
        // Per un feedback migliore, potremmo prima verificare se il libro esiste
        Optional<Book> bookOpt = libraryService.findBookByIsbn(isbn); // findBookByIsbn valida l'input ISBN
        if(bookOpt.isPresent()){
            libraryService.removeItemByIsbn(isbn); // Ora la rimozione dovrebbe avere successo
            System.out.println("Libro '" + bookOpt.get().getTitle() + "' (ISBN: " + isbn + ") rimosso.");
        } else {
            // Se findBookByIsbn (che valida l'input) non lo trova, BookNotFoundException sarà lanciata da lì
            // o da removeItemByIsbn se chiamato direttamente.
            // Per evitare doppia logica di errore, possiamo affidarci a removeItemByIsbn
            // e lasciare che il blocco catch principale in start() gestisca BookNotFoundException.
            // Oppure, per un messaggio più diretto qui:
            System.out.println("Libro con ISBN " + isbn + " non trovato. Nessuna rimozione effettuata.");
            // Se vogliamo che lanci l'eccezione per essere catturata centralmente:
            // libraryService.removeItemByIsbn(isbn); // Questo lancerà BookNotFoundException
        }
    }
    
    /**
     * Gestisce l'aggiornamento di un libro esistente.
     */
    private void updateBook() throws InvalidBookDataException, BookNotFoundException {
        System.out.println("\n--- Aggiorna Libro ---");
        System.out.print("Inserisci ISBN del libro da aggiornare: ");
        String oldIsbn = InputValidator.sanitizeString(scanner.nextLine());

        Optional<Book> bookToUpdateOpt = libraryService.findBookByIsbn(oldIsbn);
        if (!bookToUpdateOpt.isPresent()) {
            System.out.println("Libro con ISBN " + oldIsbn + " non trovato. Impossibile aggiornare.");
            return; 
        }
        
        Book currentBook = bookToUpdateOpt.get();
        System.out.println("Libro trovato: " + currentBook.getTitle());
        System.out.println("Inserisci i nuovi dati (premi Invio per mantenere il valore corrente):");

        System.out.print("Nuovo Titolo (" + currentBook.getTitle() + "): ");
        String newTitle = InputValidator.sanitizeString(scanner.nextLine());
        newTitle = (newTitle == null || newTitle.isEmpty()) ? currentBook.getTitle() : newTitle;

        System.out.print("Nuovo Autore (" + currentBook.getAuthor() + "): ");
        String newAuthor = InputValidator.sanitizeString(scanner.nextLine());
        newAuthor = (newAuthor == null || newAuthor.isEmpty()) ? currentBook.getAuthor() : newAuthor;
        
        System.out.print("Nuovo ISBN (" + currentBook.getIsbn() + "): ");
        String newIsbn = InputValidator.sanitizeString(scanner.nextLine());
        if (newIsbn == null || newIsbn.isEmpty()) {
            newIsbn = currentBook.getIsbn();
        } else if (!InputValidator.isValidISBN(newIsbn)){
             System.out.println("Nuovo ISBN fornito non è valido. Verrà mantenuto il vecchio ISBN: " + currentBook.getIsbn());
             newIsbn = currentBook.getIsbn();
        }

        System.out.print("Nuovo Anno (" + (currentBook.getPublicationYear() > 0 ? currentBook.getPublicationYear() : "N/A") + "): ");
        String newYearStr = InputValidator.sanitizeString(scanner.nextLine());
        int newYear = currentBook.getPublicationYear();
        if (newYearStr != null && !newYearStr.isEmpty()) {
            if (InputValidator.isValidYear(newYearStr)){
                newYear = Integer.parseInt(newYearStr);
            } else {
                 System.out.println("Nuovo anno non valido. Verrà mantenuto: " + newYear);
            }
        }

        System.out.print("Nuovo Genere (" + (currentBook.getGenre() != null && !currentBook.getGenre().isEmpty() ? currentBook.getGenre() : "N/A") + "): ");
        String newGenre = InputValidator.sanitizeString(scanner.nextLine());
        newGenre = (newGenre == null || newGenre.isEmpty()) ? currentBook.getGenre() : newGenre;

        Book updatedBookData = BookFactory.createBook(newIsbn, newTitle, newAuthor, newYear, newGenre);
        libraryService.updateBook(oldIsbn, updatedBookData); 
        System.out.println("Libro aggiornato con successo!");
    }

    /**
     * Permette all'utente di cambiare la strategia di ordinamento dei libri.
     */
    private void setSortStrategy() {
        System.out.println("\n--- Imposta Strategia di Ordinamento ---");
        System.out.println("1. Ordina per Titolo");
        System.out.println("2. Ordina per Autore");
        System.out.print("Scegli la strategia: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                libraryService.setSortStrategy(new SortByTitleStrategy());
                System.out.println("I libri verranno ora ordinati per titolo.");
                break;
            case "2":
                libraryService.setSortStrategy(new SortByAuthorStrategy());
                System.out.println("I libri verranno ora ordinati per autore.");
                break;
            default:
                System.out.println("Scelta non valida. Strategia non modificata.");
        }
    }

    /**
     * Gestisce il salvataggio dei dati della libreria su file.
     */
    private void saveLibraryData() throws LibraryException { 
        String currentPath = libraryService.getCurrentFilePath();
        System.out.println("\n--- Salva Libreria su File ---");
        System.out.print("Percorso file per il salvataggio (Invio per usare '" + 
            (currentPath != null && !currentPath.isEmpty() ? currentPath : DEFAULT_DIRECTORY_NAME + File.separator + DEFAULT_FILENAME) + "'): ");
        String pathToSave = InputValidator.sanitizeString(scanner.nextLine());

        if (pathToSave == null || pathToSave.isEmpty()) {
            pathToSave = currentPath; // Usa il percorso corrente (che dovrebbe essere il default se non modificato)
            if (pathToSave == null || pathToSave.isEmpty()) { // Ulteriore fallback se currentPath fosse in qualche modo vuoto
                 pathToSave = DEFAULT_DIRECTORY_NAME + File.separator + DEFAULT_FILENAME;
                 System.out.println("Nessun percorso specificato, si usa il default: " + pathToSave);
            }
        } else {
            File providedFile = new File(pathToSave);
            if (providedFile.getParent() == null) { // Solo nome file
                pathToSave = DEFAULT_DIRECTORY_NAME + File.separator + pathToSave;
                System.out.println("Nome file fornito. Verrà salvato in: " + pathToSave);
            }
        }
        
        ensureDirectoryExistsForFile(pathToSave);
        libraryService.saveLibrary(pathToSave); 
        System.out.println("Libreria salvata con successo su " + libraryService.getCurrentFilePath());
    }

    /**
     * Gestisce il caricamento dei dati della libreria da file.
     */
    private void loadLibraryData() throws LibraryException { 
        System.out.println("\n--- Carica Libreria da File ---");
        System.out.print("Inserisci il percorso del file da cui caricare: ");
        String path = InputValidator.sanitizeString(scanner.nextLine());
        if (path == null || path.isEmpty()) {
            System.out.println("Caricamento annullato: nessun percorso file fornito.");
            return;
        }
        
        File providedFile = new File(path);
        if (providedFile.getParent() == null) { // Solo nome file
            path = DEFAULT_DIRECTORY_NAME + File.separator + path;
            System.out.println("Nome file fornito. Si tenterà di caricare da: " + path);
        }

        System.out.print("ATTENZIONE: Il caricamento sostituirà la libreria corrente. Continuare? (si/no): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("si")) {
             System.out.println("Caricamento annullato.");
             return;
        }
        
        ensureDirectoryExistsForFile(path); // Anche se per caricare, la dir potrebbe non esistere,
                                            // FileStorageService gestirà file non trovato.
                                            // Questo è più per coerenza se l'utente si aspetta che la dir venga creata.
        libraryService.loadLibrary(path);
        System.out.println("Libreria caricata/tentato caricamento da " + path);
    }
}

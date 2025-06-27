package com.msan.libmanagementcli.model;

import com.msan.libmanagementcli.exceptions.InvalidBookDataException;
import com.msan.libmanagementcli.factory.BookFactory; 
import java.util.Objects;

/**
 * Rappresenta un libro singolo, l'elemento base della libreria.
 * Implementa l'interfaccia LibraryItem e usa il pattern Builder per l'istanziazione.
 */
public class Book implements LibraryItem {

    // --- Campi ---
    private String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private String genre;

    /** Delimitatore per la conversione da/a CSV. */
    private static final String CSV_DELIMITER = ",";

    /**
     * Costruttore privato, la creazione è gestita tramite {@link BookBuilder}.
     */
    private Book(BookBuilder builder) {
        this.isbn = builder.isbn;
        this.title = builder.title;
        this.author = builder.author;
        this.publicationYear = builder.publicationYear;
        this.genre = builder.genre;
    }

    // --- Metodi dell'interfaccia LibraryItem ---

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void display(String indent) {
        String yearDisplay = (publicationYear > 0) ? String.valueOf(publicationYear) : "N/A";
        String genreDisplay = (genre != null && !genre.trim().isEmpty()) ? genre : "N/A";
        System.out.println(indent + "Libro: \"" + title + "\" di " + author + 
                           " (ISBN: " + isbn + ", Anno: " + yearDisplay + 
                           ", Genere: " + genreDisplay + ")");
    }

    @Override
    public String getIsbn() {
        return isbn;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    // --- Getters e Setters ---

    public void setTitle(String title) {
        this.title = (title != null ? title.trim() : null);
    }
    public void setAuthor(String author) {
        this.author = (author != null ? author.trim() : null);
    }
    public void setIsbn(String isbn) {
        this.isbn = (isbn != null ? isbn.trim() : null);
    }
    public int getPublicationYear() {
        return publicationYear;
    }
    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = (genre != null ? genre.trim() : null);
    }

    // --- Metodi per Conversione CSV ---

    /**
     * Converte l'oggetto in una stringa in formato CSV.
     * Ordine: ISBN,Titolo,Autore,Anno,Genere.
     * @return La rappresentazione CSV del libro.
     */
    public String toCsvString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getIsbn() != null ? getIsbn() : "").append(CSV_DELIMITER);
        sb.append(getTitle() != null ? getTitle() : "").append(CSV_DELIMITER);
        sb.append(getAuthor() != null ? getAuthor() : "").append(CSV_DELIMITER);
        sb.append(getPublicationYear()).append(CSV_DELIMITER);
        sb.append(getGenre() != null ? getGenre() : "");
        return sb.toString();
    }

    /**
     * Crea un oggetto Book da una stringa in formato CSV.
     * @param csvLine La riga CSV da cui effettuare il parsing.
     * @return Una nuova istanza di {@link Book}.
     * @throws InvalidBookDataException se la riga non è valida.
     */
    public static Book fromCsvString(String csvLine) throws InvalidBookDataException {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            throw new InvalidBookDataException("La riga CSV fornita per creare un libro non può essere nulla o vuota.");
        }

        String[] parts = csvLine.split(CSV_DELIMITER, 5);

        if (parts.length < 3) { 
            throw new InvalidBookDataException("Riga CSV malformata per Book (campi insufficienti): '" + csvLine + "'");
        }

        String isbn = parts[0].trim();
        String title = parts[1].trim();
        String author = parts[2].trim();
        
        int publicationYear = 0;
        if (parts.length > 3 && parts[3] != null && !parts[3].trim().isEmpty()) {
            try {
                publicationYear = Integer.parseInt(parts[3].trim());
            } catch (NumberFormatException e) {
                // Se l'anno non è un numero, viene loggato un avviso e l'anno rimane 0.
                System.err.println("AVVISO: Formato anno non valido in CSV: '" + parts[3].trim() + "'. Anno impostato a 0.");
            }
        }

        String genre = (parts.length > 4 && parts[4] != null) ? parts[4].trim() : ""; 
        
        // Delega la creazione e la validazione finale a BookFactory.
        try {
            return BookFactory.createBook(isbn, title, author, publicationYear, genre);
        } catch (InvalidBookDataException e) {
            throw new InvalidBookDataException("Errore da BookFactory durante creazione da CSV '" + csvLine + "': " + e.getMessage(), e);
        }
    }

    // --- Metodi Standard Java ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        // L'uguaglianza si basa sull'ISBN come chiave univoca.
        return Objects.equals(getIsbn(), book.getIsbn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIsbn());
    }

    @Override
    public String toString() {
        String yearStr = (getPublicationYear() > 0) ? String.valueOf(getPublicationYear()) : "N/A";
        String genreStr = (getGenre() != null && !getGenre().trim().isEmpty()) ? getGenre() : "N/A";
        return "Book [ISBN: \"" + getIsbn() + "\", Titolo: \"" + getTitle() + 
               "\", Autore: " + getAuthor() + ", Anno: " + yearStr + 
               ", Genere: " + genreStr + "]";
    }

    // --- Inner Class: Builder ---
    
    /**
     * Builder per la creazione facilitata di oggetti {@link Book}.
     */
    public static class BookBuilder {
        private final String isbn;
        private final String title;
        private final String author;
        private int publicationYear; 
        private String genre;       

        /**
         * Costruttore del Builder con i campi obbligatori.
         */
        public BookBuilder(String isbn, String title, String author) {
            this.isbn = (isbn != null ? isbn.trim() : null);
            this.title = (title != null ? title.trim() : null);
            this.author = (author != null ? author.trim() : null);
        }

        /**
         * Imposta l'anno di pubblicazione.
         * @return Riferimento al builder per chiamate concatenate.
         */
        public BookBuilder publicationYear(int publicationYear) {
            this.publicationYear = publicationYear;
            return this;
        }

        /**
         * Imposta il genere del libro.
         * @return Riferimento al builder per chiamate concatenate.
         */
        public BookBuilder genre(String genre) {
            this.genre = (genre != null ? genre.trim() : null);
            return this;
        }

        /**
         * Costruisce e restituisce l'oggetto {@link Book}.
         * @return La nuova istanza di Book.
         */
        public Book build() {
            return new Book(this);
        }
    }
}
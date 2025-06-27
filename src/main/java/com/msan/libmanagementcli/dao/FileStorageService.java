package com.msan.libmanagementcli.dao;

import com.msan.libmanagementcli.exceptions.InvalidBookDataException;
import com.msan.libmanagementcli.exceptions.LibraryException;
import com.msan.libmanagementcli.model.Book;
import com.msan.libmanagementcli.utils.ConsoleLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione di {@link StorageService} per la persistenza su file CSV.
 * Utilizza i metodi {@code toCsvString} e {@code Book.fromCsvString} per la conversione.
 */
public class FileStorageService implements StorageService {

    private static final ConsoleLogger logger = ConsoleLogger.getInstance();

    @Override
    public void saveBooks(List<Book> books, String filePath) throws LibraryException {
        logger.logInfo("Tentativo di salvataggio su file: " + filePath);
        
        // Uso try-with-resources per la gestione automatica del writer.
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Book book : books) {
                if (book != null) {
                    writer.println(book.toCsvString());
                }
            }
            logger.logInfo("Salvataggio completato: " + books.size() + " libri scritti.");
        } catch (IOException e) {
            // Incapsula l'eccezione I/O in un'eccezione custom.
            logger.logError("Impossibile salvare su file: " + filePath, e);
            throw new LibraryException("Errore durante il salvataggio su file: " + filePath, e);
        }
    }

    @Override
    public List<Book> loadBooks(String filePath) throws LibraryException {
        logger.logInfo("Tentativo di caricamento da file: " + filePath);
        List<Book> loadedBooks = new ArrayList<>();
        File file = new File(filePath);

        // Se il file non esiste, restituisce una lista vuota come da contratto.
        if (!file.exists()) {
            logger.logWarning("File non trovato: " + filePath + ". Si parte con una libreria vuota.");
            return loadedBooks;
        }

        // Legge il file riga per riga, gestendo eventuali errori.
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String currentLine;
            int lineNumber = 0;
            while ((currentLine = reader.readLine()) != null) {
                lineNumber++;
                if (currentLine.trim().isEmpty()) {
                    continue; // Salta le righe vuote.
                }

                try {
                    loadedBooks.add(Book.fromCsvString(currentLine));
                } catch (InvalidBookDataException e) {
                    // Logga un avviso per righe malformate e continua con il resto del file.
                    logger.logWarning("Riga " + lineNumber + " saltata (dati non validi): '" + currentLine + "'. Errore: " + e.getMessage());
                }
            }
            logger.logInfo("Caricamento completato: " + loadedBooks.size() + " libri letti.");
        } catch (IOException e) {
            // Incapsula l'eccezione I/O in un'eccezione custom.
            logger.logError("Impossibile caricare da file: " + filePath, e);
            throw new LibraryException("Errore durante il caricamento da file: " + filePath, e);
        }
        return loadedBooks;
    }
}
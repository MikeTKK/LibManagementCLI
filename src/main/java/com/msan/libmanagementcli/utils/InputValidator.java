package com.msan.libmanagementcli.utils;

import java.time.Year;

/**
 * Classe di utilità con metodi statici per la validazione e la sanitizzazione dell'input.
 */
public class InputValidator {

    private static final ConsoleLogger logger = ConsoleLogger.getInstance();

    /**
     * Sanitizza una stringa rimuovendo gli spazi bianchi iniziali e finali.
     * @param input La stringa da sanitizzare.
     * @return La stringa "trimmata", o null se l'input era null.
     */
    public static String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }

    /**
     * Esegue una validazione di base sull'ISBN.
     * Verifica che la stringa non sia nulla o vuota dopo il trim.
     * @param isbn La stringa ISBN da validare.
     * @return true se l'ISBN è considerato valido, false altrimenti.
     */
    public static boolean isValidISBN(String isbn) {
        String sanitizedIsbn = sanitizeString(isbn);
        return sanitizedIsbn != null && !sanitizedIsbn.isEmpty();
    }

    /**
     * Verifica la validità di una stringa che rappresenta un anno.
     * Un input nullo o vuoto è considerato valido poiché l'anno è opzionale.
     * Un anno valido è un numero positivo non troppo nel futuro.
     *
     * @param yearString La stringa dell'anno da validare.
     * @return true se l'anno è valido o opzionale (vuoto/nullo), false altrimenti.
     */
    public static boolean isValidYear(String yearString) {
        String sanitizedYear = sanitizeString(yearString);

        if (sanitizedYear == null || sanitizedYear.isEmpty()) {
            return true; // L'anno è opzionale.
        }

        try {
            int year = Integer.parseInt(sanitizedYear);
            int currentYear = Year.now().getValue();
            // La logica di validazione permette anni fino all'anno successivo a quello corrente.
            return year > 0 && year <= (currentYear + 1);
        } catch (NumberFormatException e) {
            logger.logWarning("Formato anno non valido per l'input: \"" + sanitizedYear + "\"");
            return false;
        }
    }
}
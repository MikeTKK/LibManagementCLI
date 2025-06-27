package com.msan.libmanagementcli.exceptions;

/**
 * Lanciata quando un libro cercato tramite ISBN non viene trovato.
 */
public class BookNotFoundException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
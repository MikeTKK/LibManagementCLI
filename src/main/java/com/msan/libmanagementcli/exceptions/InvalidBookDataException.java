package com.msan.libmanagementcli.exceptions;

/**
 * Lanciata quando i dati forniti per un libro non sono validi o sono incompleti.
 */
public class InvalidBookDataException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public InvalidBookDataException(String message) {
        super(message);
    }

    public InvalidBookDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
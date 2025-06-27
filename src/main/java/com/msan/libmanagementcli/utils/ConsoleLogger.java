package com.msan.libmanagementcli.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Fornisce un'istanza Singleton per il logging di messaggi su console.
 * Supporta diversi livelli di log per filtrare l'output in base alla severità.
 */
public class ConsoleLogger {

    // --- Singleton e Campi Statici ---
    private static ConsoleLogger instance;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // --- Campi d'Istanza ---
    private LogLevel currentLevel = LogLevel.INFO; // Livello di logging di default

    /**
     * Enum che definisce i livelli di log disponibili, in ordine di severità crescente.
     */
    public enum LogLevel {
        DEBUG, INFO, WARNING, ERROR, NONE
    }

    /**
     * Costruttore privato per implementare il pattern Singleton.
     */
    private ConsoleLogger() {}

    /**
     * Restituisce l'unica istanza di ConsoleLogger (Singleton).
     * @return L'istanza singleton.
     */
    public static synchronized ConsoleLogger getInstance() {
        if (instance == null) {
            instance = new ConsoleLogger();
        }
        return instance;
    }

    // --- Configurazione del Logger ---

    /**
     * Imposta il livello minimo di logging.
     * @param level Il nuovo livello di log da utilizzare.
     */
    public void setLogLevel(LogLevel level) {
        if (level != null) {
            this.currentLevel = level;
        }
    }

    /**
     * Restituisce il livello di logging attualmente impostato.
     * @return Il LogLevel corrente.
     */
    public LogLevel getCurrentLogLevel() {
        return this.currentLevel;
    }

    // --- Metodi di Logging Pubblici ---

    /**
     * Registra un messaggio a livello DEBUG.
     * @param message Il messaggio da registrare.
     */
    public void logDebug(String message) {
        log(LogLevel.DEBUG, message, null);
    }

    /**
     * Registra un messaggio a livello INFO.
     * @param message Il messaggio da registrare.
     */
    public void logInfo(String message) {
        log(LogLevel.INFO, message, null);
    }

    /**
     * Registra un messaggio a livello WARNING.
     * @param message Il messaggio da registrare.
     */
    public void logWarning(String message) {
        log(LogLevel.WARNING, message, null);
    }
    
    /**
     * Registra un messaggio a livello WARNING con un'eccezione associata.
     * @param message Il messaggio da registrare.
     * @param t L'eccezione associata.
     */
    public void logWarning(String message, Throwable t) {
        log(LogLevel.WARNING, message, t);
    }

    /**
     * Registra un messaggio a livello ERROR.
     * @param message Il messaggio da registrare.
     */
    public void logError(String message) {
        log(LogLevel.ERROR, message, null);
    }
    
    /**
     * Registra un messaggio a livello ERROR con un'eccezione associata.
     * @param message Il messaggio da registrare.
     * @param t L'eccezione associata.
     */
    public void logError(String message, Throwable t) {
        log(LogLevel.ERROR, message, t);
    }

    // --- Logica Interna Privata ---

    /**
     * Metodo privato che decide se un messaggio deve essere effettivamente loggato
     * in base al livello di log corrente.
     */
    private boolean shouldLog(LogLevel messageLevel) {
        if (this.currentLevel == LogLevel.NONE) {
            return false;
        }
        // Confronta la severità basandosi sulla posizione nell'enum.
        return messageLevel.ordinal() >= this.currentLevel.ordinal();
    }

    /**
     * Metodo privato centrale che formatta e stampa il messaggio di log.
     */
    private void log(LogLevel level, String message, Throwable throwable) {
        if (shouldLog(level)) {
            String logMessage = dtf.format(LocalDateTime.now()) + " [" + level + "] " + message;
            if (throwable != null) {
                logMessage += " - Exception: " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
            }
            System.out.println(logMessage);
        }
    }
}
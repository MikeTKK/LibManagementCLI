package com.msan.libmanagementcli;

import com.msan.libmanagementcli.dao.FileStorageService;
import com.msan.libmanagementcli.dao.StorageService;
import com.msan.libmanagementcli.service.LibraryService;
import com.msan.libmanagementcli.ui.CommandLineInterface;
import com.msan.libmanagementcli.utils.ConsoleLogger;

/**
 * Classe principale per l'avvio dell'applicazione LibManagementCLI.
 */
public class Main {

    /**
     * Punto di ingresso (entry point) dell'applicazione.
     * @param args Argomenti da riga di comando (non utilizzati).
     */
    public static void main(String[] args) {

        // --- Setup del Logger ---
        ConsoleLogger logger = ConsoleLogger.getInstance();
        logger.setLogLevel(ConsoleLogger.LogLevel.INFO);
        logger.logInfo("Applicazione LibManagementCLI in avvio...");

        // --- Creazione Componenti (Dependency Injection) ---
        StorageService storageService = new FileStorageService();
        LibraryService libraryService = LibraryService.getInstance(storageService);

        // --- Avvio Interfaccia Utente ---
        CommandLineInterface cli = new CommandLineInterface(libraryService);

        try {
            cli.start();
        } catch (Exception e) {
            logger.logError("Errore critico non gestito nell'esecuzione principale.", e);
            System.err.println("ERRORE CRITICO: L'applicazione si è interrotta a causa di un errore imprevisto.");
        } finally {
            logger.logInfo("Applicazione LibManagementCLI terminata.");
        }
    }
}
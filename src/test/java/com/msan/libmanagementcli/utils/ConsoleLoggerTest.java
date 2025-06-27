package com.msan.libmanagementcli.utils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test per la classe di utilità {@link ConsoleLogger}.
 * Verifica il comportamento del pattern Singleton e la gestione dei livelli di log.
 */
class ConsoleLoggerTest {

    private ConsoleLogger loggerInstance1;
    private ConsoleLogger loggerInstance2;

    /**
     * Prepara l'ambiente per ogni test, garantendone l'indipendenza.
     */
    @BeforeEach
    void setUp() {
        // Ottiene due istanze per verificare il comportamento del Singleton.
        loggerInstance1 = ConsoleLogger.getInstance();
        loggerInstance2 = ConsoleLogger.getInstance();
        
        // Reimposta un livello di log di default prima di ogni esecuzione.
        loggerInstance1.setLogLevel(ConsoleLogger.LogLevel.INFO);
    }

    /**
     * Testa il pattern Singleton.
     * Verifica che più chiamate a getInstance() restituiscano sempre la stessa istanza.
     */
    @Test
    void testGetInstance_restituisceSempreStessaIstanza() {
        assertSame(loggerInstance1, loggerInstance2, "Le due istanze dovrebbero essere lo stesso oggetto.");
    }

    /**
     * Testa l'impostazione e la lettura del livello di log.
     * Verifica che il livello venga cambiato e letto correttamente.
     */
    @Test
    void testSetAndGetLogLevel() {
        // Testa l'impostazione per ogni livello possibile.
        loggerInstance1.setLogLevel(ConsoleLogger.LogLevel.DEBUG);
        assertEquals(ConsoleLogger.LogLevel.DEBUG, loggerInstance1.getCurrentLogLevel());

        loggerInstance1.setLogLevel(ConsoleLogger.LogLevel.WARNING);
        assertEquals(ConsoleLogger.LogLevel.WARNING, loggerInstance1.getCurrentLogLevel());
        
        loggerInstance1.setLogLevel(ConsoleLogger.LogLevel.ERROR);
        assertEquals(ConsoleLogger.LogLevel.ERROR, loggerInstance1.getCurrentLogLevel());

        loggerInstance1.setLogLevel(ConsoleLogger.LogLevel.NONE);
        assertEquals(ConsoleLogger.LogLevel.NONE, loggerInstance1.getCurrentLogLevel());
        
        loggerInstance1.setLogLevel(ConsoleLogger.LogLevel.INFO); 
        assertEquals(ConsoleLogger.LogLevel.INFO, loggerInstance1.getCurrentLogLevel());
    }

    /**
     * Smoke test per i metodi di log pubblici.
     * Verifica che le chiamate ai metodi di log non causino eccezioni.
     * Nota: questo test non verifica l'output su console.
     */
    @Test
    void testMetodiLog_nonLanciaEccezioni() {
        // Esegue i metodi di log per assicurarsi che non si verifichino errori a runtime.
        assertDoesNotThrow(() -> loggerInstance1.logDebug("Messaggio di debug di test."));
        assertDoesNotThrow(() -> loggerInstance1.logInfo("Messaggio informativo di test."));
        assertDoesNotThrow(() -> loggerInstance1.logWarning("Messaggio di avviso di test."));
        assertDoesNotThrow(() -> loggerInstance1.logError("Messaggio di errore di test."));
        assertDoesNotThrow(() -> loggerInstance1.logError("Errore di test con eccezione.", new RuntimeException("Test")));
    }
}
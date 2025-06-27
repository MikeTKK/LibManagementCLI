package com.msan.libmanagementcli.utils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.Year;

/**
 * Test per la classe di utilità {@link InputValidator}.
 */
class InputValidatorTest {

    // --- Sezione Test per il metodo sanitizeString ---

    /**
     * Verifica che il metodo gestisca correttamente un input nullo.
     */
    @Test
    void testSanitizeString_conInputNullo() {
        assertNull(InputValidator.sanitizeString(null));
    }

    /**
     * Verifica che il metodo rimuova gli spazi bianchi esterni e preservi quelli interni.
     */
    @Test
    void testSanitizeString_conSpazi() {
        assertEquals("testo con spazi", InputValidator.sanitizeString("  testo con spazi  "));
        assertEquals("test", InputValidator.sanitizeString("test"));
        assertEquals("", InputValidator.sanitizeString("   "));
    }

    // --- Sezione Test per il metodo isValidISBN ---

    /**
     * Verifica che un ISBN valido (non vuoto) venga accettato.
     */
    @Test
    void testIsValidISBN_conInputValido() {
        assertTrue(InputValidator.isValidISBN("978-3-16-148410-0"));
    }

    /**
     * Verifica che un ISBN non valido (nullo, vuoto, blank) venga rifiutato.
     */
    @Test
    void testIsValidISBN_conInputNonValido() {
        assertFalse(InputValidator.isValidISBN(null), "Un ISBN nullo non deve essere valido.");
        assertFalse(InputValidator.isValidISBN(""), "Un ISBN vuoto non deve essere valido.");
        assertFalse(InputValidator.isValidISBN("   "), "Un ISBN di soli spazi non deve essere valido.");
    }
    
    // --- Sezione Test per il metodo isValidYear ---

    /**
     * Verifica che un anno valido (numerico e nel range corretto) venga accettato.
     */
    @Test
    void testIsValidYear_conInputValido() {
        String annoCorrente = String.valueOf(Year.now().getValue());
        assertTrue(InputValidator.isValidYear(annoCorrente));
        assertTrue(InputValidator.isValidYear("1995"));
    }
    
    /**
     * Verifica che un anno opzionale (nullo o vuoto) sia considerato valido.
     */
    @Test
    void testIsValidYear_conInputOpzionale() {
        assertTrue(InputValidator.isValidYear(null), "Un anno nullo (opzionale) deve essere valido.");
        assertTrue(InputValidator.isValidYear(""), "Un anno vuoto (opzionale) deve essere valido.");
        assertTrue(InputValidator.isValidYear("   "), "Un anno di soli spazi (opzionale) deve essere valido.");
    }
    
    /**
     * Verifica che un anno non valido venga rifiutato.
     */
    @Test
    void testIsValidYear_conInputNonValido() {
        // Il logger stamperà un avviso in console durante questi test, è normale.
        assertFalse(InputValidator.isValidYear("duemila"), "Un anno non numerico non deve essere valido.");
        assertFalse(InputValidator.isValidYear("0"), "L'anno 0 non deve essere valido.");
        assertFalse(InputValidator.isValidYear("-200"), "Un anno negativo non deve essere valido.");
        
        String annoTroppoFuturo = String.valueOf(Year.now().getValue() + 5);
        assertFalse(InputValidator.isValidYear(annoTroppoFuturo), "Un anno troppo nel futuro non deve essere valido.");
    }
}
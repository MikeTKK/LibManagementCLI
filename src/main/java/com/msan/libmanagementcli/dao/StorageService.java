package com.msan.libmanagementcli.dao;

import com.msan.libmanagementcli.model.Book;
import com.msan.libmanagementcli.exceptions.LibraryException;

import java.util.List;

/**
 * Interfaccia che definisce il contratto per i servizi di persistenza dei dati.
 * Disaccoppia la logica di business dalla specifica implementazione dello storage.
 */
public interface StorageService {

    /**
     * Salva una lista di libri in un percorso specifico.
     *
     * @param books La lista di libri da salvare.
     * @param filePath Il percorso del file di destinazione.
     * @throws LibraryException Se il salvataggio fallisce.
     */
    void saveBooks(List<Book> books, String filePath) throws LibraryException;

    /**
     * Carica una lista di libri da un percorso specifico.
     *
     * @param filePath Il percorso del file da cui leggere.
     * @return La lista dei libri letti dal file.
     * @throws LibraryException Se il caricamento fallisce.
     */
    List<Book> loadBooks(String filePath) throws LibraryException;
}
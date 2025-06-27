package com.msan.libmanagementcli.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Rappresenta una collezione di {@link LibraryItem} (pattern Composite).
 * Permette di raggruppare libri e altre collezioni in una struttura gerarchica.
 * In questa versione, le collezioni sono usate solo per raggruppamenti in memoria.
 */
public class BookCollection implements LibraryItem {

    // --- Campi ---
    private String collectionName;
    private List<LibraryItem> items;

    // --- Costruttore ---
    /**
     * Costruisce una nuova BookCollection con il nome specificato.
     * @param collectionName Il nome della collezione. Non può essere nullo o vuoto.
     * @throws IllegalArgumentException se il nome fornito non è valido.
     */
    public BookCollection(String collectionName) {
        if (collectionName == null || collectionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della collezione non può essere nullo o vuoto.");
        }
        this.collectionName = collectionName.trim();
        this.items = new ArrayList<>();
    }

    // --- Gestione degli Elementi Figli ---

    /**
     * Aggiunge un {@link LibraryItem} a questa collezione. Ignora gli item nulli.
     * @param item L'elemento da aggiungere.
     */
    public void addItem(LibraryItem item) {
        if (item != null) {
            this.items.add(item);
        }
    }

    /**
     * Rimuove un {@link LibraryItem} da questa collezione.
     * @param item L'elemento da rimuovere.
     */
    public void removeItem(LibraryItem item) {
        this.items.remove(item);
    }

    /**
     * Restituisce una vista non modificabile degli elementi contenuti nella collezione.
     * @return Una lista non modificabile dei LibraryItem figli.
     */
    public List<LibraryItem> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    // --- Implementazione dei Metodi dell'Interfaccia LibraryItem ---

    @Override
    public String getTitle() {
        return this.collectionName;
    }

    /**
     * Mostra il nome della collezione e ricorsivamente i suoi contenuti.
     * @param indent Stringa di indentazione per la visualizzazione gerarchica.
     */
    @Override
    public void display(String indent) {
        System.out.println(indent + "Collezione: " + collectionName + " (" + items.size() + " elementi)");
        for (LibraryItem item : items) {
            item.display(indent + "  ");
        }
    }

    @Override
    public String getIsbn() {
        // Le collezioni non hanno un ISBN.
        return null; 
    }

    @Override
    public String getAuthor() {
        // Le collezioni non hanno un singolo autore.
        return null; 
    }

    /**
     * Crea un iteratore per scorrere i figli diretti di questa collezione (Pattern Iterator).
     * @return Un iteratore per i LibraryItem contenuti.
     */
    @Override
    public Iterator<LibraryItem> createIterator() {
        return new DirectChildrenIterator(this.items);
    }

    /**
     * Iteratore privato che scorre i figli diretti della collezione.
     */
    private static class DirectChildrenIterator implements Iterator<LibraryItem> {
        private Iterator<LibraryItem> internalIterator;

        public DirectChildrenIterator(List<LibraryItem> items) {
            this.internalIterator = items.iterator();
        }

        @Override
        public boolean hasNext() {
            return internalIterator.hasNext();
        }

        @Override
        public LibraryItem next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Non ci sono più elementi nella collezione.");
            }
            return internalIterator.next();
        }

        /**
         * L'operazione di rimozione non è supportata da questo iteratore.
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("La rimozione tramite iteratore non è supportata.");
        }
    }
}
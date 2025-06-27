# LibManagementCLI - Gestione Libreria Personale

`LibManagementCLI` è un'applicazione software a riga di comando (CLI) sviluppata in Java per la gestione di una libreria personale.
Per garantire un design pulito e manutenibile, il codice è suddiviso in package modulari. Ognuno di questi rappresenta uno strato dell'architettura con una responsabilità ben definita.

## Cosa Fa l'Applicazione
L'applicazione permette di fare le operazioni base per gestire una collezione di libri:
* **Aggiungere** nuovi libri specificando ISBN, titolo, autore, anno e genere.
* **Visualizzare** tutti i libri presenti nella libreria.
* **Ordinare** la visualizzazione dei libri per titolo o per autore.
* **Cercare** i libri per ISBN, titolo o autore.
* **Rimuovere** un libro dalla libreria usando il suo ISBN.
* **Aggiornare** le informazioni di un libro già esistente.
* **Salvare e Caricare** la libreria su un file CSV, per non perdere i dati quando si chiude il programma.

## Tecnologie Utilizzate

* **Java SE (JDK 17):** È il linguaggio di programmazione su cui si basa tutta l'applicazione.
* **Maven:** È lo strumento che gestisce la struttura del progetto e le sue dipendenze (le librerie esterne). Ad esempio, si occupa di scaricare e configurare JUnit e Mockito per i test.
* **JUnit 5:** È il framework che ho usato per scrivere i test unitari. Ogni test verifica che una piccola parte del codice (un metodo o una classe) funzioni come previsto.
* **Mockito:** È una libreria che aiuta a scrivere i test. L'ho usata per creare una versione "finta" (mock) del `StorageService` quando ho testato il `LibraryService`, in modo da poter testare la logica di business senza dover leggere o scrivere file reali.
* **Java Collections Framework:** Ho usato `List`, `ArrayList` e `Optional` per gestire le liste di libri in memoria.
* **Java Stream API:** In alcuni punti, ho usato le Stream API per lavorare sulle collezioni, ad esempio per filtrare i libri durante una ricerca.
* **Java I/O:** Per leggere e scrivere sul file CSV dove vengono salvati i dati dei libri.

## Design Pattern Implementati:

* **Singleton:**
    * *Cosa fa:* Le classi `ConsoleLogger` e `LibraryService` usano questo pattern. Assicura che di queste classi esista una sola istanza in tutta l'applicazione.
* **Factory (Simple Factory):**
    * *Cosa fa:* La classe `BookFactory` ha il compito di creare gli oggetti `Book` e `BookCollection`. Centralizza la creazione degli oggetti in un unico punto.
* **Builder:**
    * *Cosa fa:* All'interno della classe `Book` c'è una sotto-classe `BookBuilder`. Serve a costruire un nuovo oggetto `Book` un pezzo alla volta, rendendo il codice più leggibile.
* **Composite:**
    * *Cosa fa:* L'interfaccia `LibraryItem` e le classi `Book` e `BookCollection` lavorano insieme. Questo pattern mi permette di trattare un libro singolo e una collezione di libri allo stesso modo. In questa versione, le `BookCollection` sono usate solo per raggruppare i libri in memoria.
* **Iterator:**
    * *Cosa fa:* La classe `BookCollection` ha un metodo `createIterator()` che fornisce un modo standard per scorrere gli elementi che contiene.
* **Strategy:**
    * *Cosa fa:* Questo pattern è usato per l'ordinamento. Ci sono due strategie, `SortByTitleStrategy` e `SortByAuthorStrategy`. Il `LibraryService` usa una di queste per ordinare la lista dei libri prima di visualizzarla, e l'utente può scegliere quale usare.
* **Gerarchia di Eccezioni Custom:**
    * *Cosa fa:* Ho creato delle eccezioni personalizzate (`LibraryException`, `BookNotFoundException`, `InvalidBookDataException`). Vengono usate per segnalare errori specifici dell'applicazione, rendendo il codice più chiaro e la gestione degli errori più precisa.

## Struttura del Progetto
Il codice è diviso in diversi package per tenerlo organizzato:
* `com.msan.libmanagementcli`: Contiene la classe `Main` per avviare il programma.
* `dao`:(Data Access Object). Gestisce la persistenza dei dati (lettura e scrittura su file).
* `exceptions`: Contiene le mie classi di eccezione personalizzate.
* `factory`: Contiene la `BookFactory`.
* `model`: Contiene le classi che rappresentano i dati (come `Book`).
* `service`: Contiene la logica di business principale (`LibraryService`) e le strategie di ordinamento.
* `ui`: Contiene l'interfaccia utente a riga di comando (`CommandLineInterface`).
* `utils`: Contiene classi di aiuto come il logger e il validatore di input.

## Istruzioni per Avviare il Progetto

### Prerequisiti
* Java Development Kit (JDK) 11 o superiore.
* Apache Maven.
* Eclipse IDE (o un altro IDE che supporti i progetti Maven).

### Setup e Avvio(ECLIPSE)
1.  **Importa il Progetto:**
    * Apri Eclipse e seleziona `File > Import...`.
    * Scegli `Maven > Existing Maven Projects`.
    * Seleziona la cartella principale del progetto (quella con il file `pom.xml`) e clicca `Finish`. Maven scaricherà automaticamente le dipendenze necessarie.
2.  **Avvia l'Applicazione:**
    * Trova il file `Main.java` nel package `com.msan.libmanagementcli`.
    * Fai clic con il tasto destro su `Main.java` e seleziona `Run As > Java Application`.
3.  **Uso Iniziale:**
    * Al primo avvio, l'applicazione ti chiederà il percorso di un file CSV.
    * Puoi premere **Invio** per usare il percorso di default (`LeMieLibrerie/dati_default.csv`), che verrà creato all'interno della cartella del tuo progetto. Oppure puoi specificare un percorso completo a un tuo file.

### Eseguire i Test
1.  Nel "Package Explorer" di Eclipse, fai clic con il tasto destro sul nome del progetto (`LibManagementCLI`).
2.  Seleziona `Run As > JUnit Test`.
3.  I risultati verranno mostrati nella vista "JUnit".

## Formato del File CSV
Il programma salva i libri in un file CSV semplice. Ogni riga è un libro e le colonne sono separate da virgola, in questo ordine:
`ISBN,Titolo,Autore,AnnoDiPubblicazione,Genere`

**Importante:** I campi (come il titolo) non devono contenere virgole.


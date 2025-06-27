/**
 * Definisce il modulo principale per l'applicazione di gestione della libreria.
 * Specifica le dipendenze del modulo (se presenti per il codice principale)
 * e apre i package necessari ai framework di test come JUnit e Mockito
 * per consentire l'accesso riflessivo durante l'esecuzione dei test.
 */
module LibManagementCLI { // Assicurati che questo sia il nome del modulo che Eclipse ha generato/riconosce

    // Per ora, non dichiariamo 'requires' per JUnit o Mockito qui,
    // poiché sono dipendenze con scope 'test' e Maven dovrebbe gestirle
    // per il classpath/modulepath dei test. Le aggiungeremo solo se strettamente necessario
    // per la compilazione del codice di test stesso contro le API del modulo principale.

    // Esportazioni (se altri moduli dovessero usare questo):
    // Per un'applicazione standalone semplice, gli exports potrebbero non essere tutti necessari
    // a meno che non si preveda una struttura multi-modulo più complessa in futuro.
    // Esportiamo i package principali che contengono le interfacce e i modelli pubblici.
    exports com.msan.libmanagementcli.model;
    exports com.msan.libmanagementcli.service; // Se l'interfaccia SortStrategy è qui e usata esternamente
    exports com.msan.libmanagementcli.dao;     // Per l'interfaccia StorageService
    exports com.msan.libmanagementcli.exceptions;
    // exports com.msan.libmanagementcli.factory; // Se la factory deve essere usata da fuori
    // exports com.msan.libmanagementcli.ui; // Di solito l'UI non viene esportata come API


    // Queste direttive 'opens' sono cruciali per i test.
    // Permettono a JUnit 5 (tramite org.junit.platform.commons) e a Mockito (tramite org.mockito)
    // di accedere ai membri delle classi nei tuoi package tramite reflection,
    // anche se non sono pubblici. Questo è necessario per l'iniezione dei mock,
    // la scoperta dei test, l'esecuzione, ecc.
    // Devi includere ogni package che contiene classi che verranno testate
    // o classi che vengono mockate/iniettate nei test.

    opens com.msan.libmanagementcli.model to org.junit.platform.commons, org.mockito;
    opens com.msan.libmanagementcli.factory to org.junit.platform.commons, org.mockito;
    opens com.msan.libmanagementcli.service to org.junit.platform.commons, org.mockito;
    opens com.msan.libmanagementcli.dao to org.junit.platform.commons, org.mockito;
    opens com.msan.libmanagementcli.utils to org.junit.platform.commons, org.mockito;
    opens com.msan.libmanagementcli.exceptions to org.junit.platform.commons, org.mockito;
    // Aggiungeremo 'opens' per altri package (es. ui, patterns specifici) quando li creeremo e testeremo.
}
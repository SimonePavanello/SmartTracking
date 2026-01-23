# SmartTracking: Logistics for Fragile and Sensitive Items

## 1. Introduzione e Contesto
 Questo progetto è stato sviluppato nel ruolo di CTO di una società di software. L'obiettivo è fornire una soluzione logistica per il monitoraggio di beni fragili e sensibili utilizzando sensori IoT. Il sistema garantisce la trasparenza e l'integrità dei dati ambientali (temperatura, umidità, vibrazioni) durante il trasporto, rendendoli accessibili a utenti, consumatori e autorità di certificazione.

## 2. Key Features
Il componente software implementa le seguenti caratteristiche chiave richieste dal progetto:
* **Tracking**: Registrazione periodica delle condizioni ambientali tramite sensori.
* **Low Cost**: Utilizzo di dispositivi IoT economici e riutilizzabili per diverse spedizioni.
* **Scalability**: Infrastruttura cloud ottimizzata per gestire i dati in modo efficiente.
* **Security**: Garanzia che il software/hardware dei sensori sia genuino e non manomesso.
* **Trust**: Accesso semplificato per la verifica dei dati da parte di autorità e consumatori.

## 3. Scenari d'Uso (User Stories)
In base alle specifiche per un team di 3 partecipanti, sono stati definiti i seguenti 9 scenari:

| ID | Attore | Scenario | Descrizione |
| :--- | :--- | :--- | :--- |
| **S1** | Admin | **Provisioning Device** | Registrazione di un nuovo sensore (tramite QR-code) per renderlo disponibile nel sistema. |
| **S2** | Admin | **Configurazione Soglie** | Impostazione dei limiti di temperatura e vibrazione per un device specifico. |
| **S3** | Utente | **Creazione Spedizione** | Inserimento di una nuova spedizione descrivendo il bene fragile trasportato. |
| **S4** | Utente | **Associazione Device** | Vincolo di un sensore attivo a una specifica spedizione per iniziare il monitoraggio. |
| **S5** | Device | **Invio Telemetria** | Invio periodico dei dati ambientali tramite API con ricezione di ACK dal server. |
| **S6** | Utente | **Monitoraggio Live** | Visualizzazione su mappa web dello stato e della posizione delle spedizioni attive. |
| **S7** | Autorità | **Verifica Pubblica** | Accesso ai dati storici tramite API-Key per certificare la qualità del trasporto. |
| **S8** | Admin | **Decommissioning** | Rimozione logica di un device obsoleto o danneggiato dal sistema. |
| **S9** | Sistema | **Rilevazione Anomalia** | Marcatura automatica della spedizione come "Compromessa" se i dati sono fuori soglia. |



## 4. Analisi Tecnica e Implementazione
Il sistema è realizzato come un componente Java eseguibile utilizzando Gradle.

### 4.1 Modello Dati
L'entità principale `UserRegistered` gestisce l'anagrafica completa richiesta:
* **Credenziali**: Username, Password, Ruolo (Admin/User).
* **Anagrafica**: Nome, Cognome, Codice Fiscale, Genere.
* **Recapiti**: Email, Telefono, Città, Indirizzo.

### 4.2 Controllo Accessi
* **Bootstrap**: Il sistema inizializza automaticamente un utente Admin al primo avvio se il database è vuoto.
* **Registrazione**: Gli utenti possono registrarsi autonomamente tramite l'interfaccia Web (ruolo USER di default).
* **Sicurezza**: L'accesso alle API di lettura dati richiede una API-Key univoca generata per l'utente.

## 5. Quality Assurance (QA)
Il progetto segue rigorosi standard di validazione:
* **Unit Tests**: Implementati con JUnit per garantire un'alta code coverage della logica di business.
* **Acceptance Tests (Selenium)**: Test end-to-end dell'interfaccia web sviluppati con **Page Object Pattern** e **WebDriverManager**.
* **Acceptance Tests (REST Assured)**: Validazione automatizzata degli endpoint API.
* **Mocking**: Le dipendenze esterne sono simulate per rendere il componente eseguibile in isolamento.

## 6. Istruzioni per la Build ed Esecuzione
1. **Prerequisiti**: Java JDK 17+ e Gradle installato.
2. **Build**: `./gradlew build`.
3. **Esecuzione**: `./gradlew bootRun`.
4. **Accesso**: Navigare su `http://localhost:8080`.
    * *Nota*: Al primo avvio usare `admin` / `123456789` per l'accesso amministrativo.

---
**Sviluppato per il corso di:** Progettazione e Validazione di Sistemi Software
**Docente:** Mariano Ceccato   
**Università:** Università degli Studi di Verona   
**Data esame:** Febbraio 2026 
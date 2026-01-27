# SmartTracking: Sistema di Monitoraggio Logistico per Beni Fragili e Sensibili

**Report di Progetto - Corso di Progettazione e Validazione di Sistemi Software**

**Università degli Studi di Verona**  
**Docente**: Prof. Mariano Ceccato  
**Anno Accademico**: 2025/2026  
**Data**: Febbraio 2026
**Studente**: Febbraio 2026

---

## 📑 Indice

1. [Introduzione](#1-introduzione)
2. [Requisiti](#2-Requisiti)
3. [Attori](#3-Attori)
4. [Scenari](#4-Scenari)
5. [Test Selection](#5-test-selection)
6. [Test Coverage](#6-test-coverage)
7. [Conclusioni](#7-conclusioni)
8. [Appendici](#8-appendici)

---

## 1. Introduzione
SmartTracking è un sistema software sviluppato per il monitoraggio real-time di spedizioni contenenti beni fragili 
e sensibili (prodotti farmaceutici, alimentari, opere d'arte, etc.) attraverso l'utilizzo di dispositivi IoT a basso costo.
Il progetto è stato realizzato nell'ambito del corso di Progettazione e Validazione di Sistemi Software presso 
l'Università degli Studi di Verona, con l'obiettivo di fornire una soluzione completa che garantisca:

- Trasparenza nella filiera logistica
- Certificabilità dei dati ambientali
- Scalabilità per gestire flotte di sensori
- Sicurezza contro manomissioni hardware/software

Contesto Operativo
Il sistema simula l'ambiente di una società logistica in cui:

- Gli amministratori gestiscono l'infrastruttura IoT
- Gli operatori logistici creano e monitorano le spedizioni
- I sensori IoT raccolgono automaticamente dati ambientali

## 2. Requisiti
### 2.1 Requisiti Funzionali
| Requisito | Descrizione | Priorità |
| :--- | :--- | :--- |
| **RF1** | Registrazione e gestione dispositivi IoT (provisioning/decommissioning) | Alta |
| **RF2** | Configurazione parametri di campionamento per ogni sensore | Alta |
| **RF3** | Creazione e gestione spedizioni con associazione sensori | Alta |
| **RF4** | Ricezione telemetria automatica da dispositivi (temp, umidità, GPS) | Alta |
| **RF5** | Visualizzazione real-time su mappa interattiva | Media |
| **RF6** | API pubblica per verifica storico dati | Alta |
| **RF7** | Gestione utenti con controllo accessi basato su ruoli | Media |
| **RF8** | Riutilizzo sensori per multiple spedizioni (ciclo di vita) | Alta |

### 2.2 Requisiti Non Funzionali
- **Sicurezza**: Autenticazione API-Key per dispositivi, password cifrate (BCrypt)
- **Usabilità**: Interfaccia web responsive con Tailwind CSS
- **Manutenibilità**: Architettura MVC con Service Layer, logging estensivo
- **Testabilità**: Coverage >70% con unit test e acceptance test automatizzati

## 3. Attori
### 3.1 Admin (Amministratore)
Descrizione: Responsabile tecnico dell'infrastruttura IoT e della gestione utenti.

**Responsabilità:**
- Eseguire il provisioning di nuovi dispositivi fisici
- Configurare parametri di campionamento (intervalli, soglie)
- Dismettere dispositivi obsoleti o malfunzionanti
- Gestire anagrafica utenti (visualizzazione, eliminazione)

### 3.2 User (Operatore Logistico)
Descrizione: Utente standard che gestisce le operazioni quotidiane di spedizione.
**Responsabilità:**
- Creare nuove spedizioni specificando destinazione e contenuto
- Associare sensori disponibili alle spedizioni
- Monitorare lo stato real-time delle spedizioni attive
- Completare spedizioni e liberare i sensori per riutilizzo

### 3.3 Device IoT (Sensore Hardware)
Descrizione: Dispositivo fisico embedded con capacità di rilevazione ambientale e connettività wireless.

**Funzioni Tecniche:**
- Acquisizione ciclica di:
  - Temperatura 
  - Umidità relativa (
  - Coordinate GPS 
- Trasmissione dati via HTTP POST ogni N secondi (configurabile)
- Autenticazione tramite API-Key univoca (UUID v4)


**Lifecycle Stati:**:
1. REGISTERED: Dispositivo registrato ma non in uso
2. ACTIVE: Associato a spedizione attiva, invia telemetria
3. ECOMMISSIONED: Dismesso, non più utilizzabile

### 4. Scenari

#### UC1 – Provisioning di un Nuovo Dispositivo

- **ID:** UC1
- **Attore Primario:** Admin
- **Livello:** Funzione di sistema

##### Precondizioni
- L'admin è autenticato con ruolo **ADMIN**
- Il dispositivo fisico è disponibile e operativo

#### Postcondizioni di Successo
- Il dispositivo è registrato nel database con stato **REGISTERED**
- Una **API-Key** univoca è generata e associata
- I parametri di default sono impostati:
   - sampling interval: **60s**

#### Flusso Principale
1. L'admin naviga su `/web/provision`
2. Scansiona il QR-code presente sul dispositivo fisico
   - Ottiene un UID univoco (es. `SN-2026-XF8`)
3. Inserisce l'UID nel campo di testo del form
4. Clicca **"COMPLETA PROVISIONING"**
5. Il sistema verifica l'univocità dell'UID nel database
6. Il sistema genera:
   - `UUID apiKey = UUID.randomUUID().toString()`
   - `status = DeviceStatus.REGISTERED`
   - `samplingIntervalSeconds = 60` (default)
7. Il dispositivo viene persistito tramite `DeviceRepository.save()`
8. L'admin visualizza un messaggio di conferma con i dettagli della API-Key

#### Flussi Alternativi

##### 5a – UID Duplicato
- Il sistema rileva:
   - `deviceRepository.existsDeviceByUuid(uid) == true`
- Lancia:
   - `RuntimeException("Device already registered with uuid: " + uuid)`
- Mostra errore:
   - *"Il dispositivo è già registrato nel sistema"*
- Il caso d'uso termina in fallimento

##### 3a – UID Formato Invalido
- Il sistema valida il formato:
   - pattern esempio: `SN-YYYY-XXX`
- Mostra errore di validazione **client-side**
- L'admin corregge l'input e riprova

#### UC2 – Configurazione Parametri Dispositivo

- **ID:** UC2
- **Attore Primario:** Admin
- **Livello:** Funzione di sistema

##### Precondizioni
- Il dispositivo esiste nel sistema (qualsiasi stato)
- L'admin ha accesso alla pagina di configurazione

##### Postcondizioni di Successo
- I nuovi parametri sono salvati nel database
- *(Opzionale)* La configurazione è inviata al dispositivo hardware via protocollo IoT

##### Flusso Principale – Aggiornamento Database
1. L'admin accede a `/web/devices`
2. Identifica il dispositivo target nella tabella
3. Clicca sull'icona **"Configura"**
4. Il sistema carica la pagina `/web/configDevice/{uuid}` con i dati correnti
5. L'admin visualizza:
   - UID dispositivo
   - Stato corrente (`REGISTERED` / `ACTIVE` / `DECOMMISSIONED`)
   - Spedizione associata (se presente)
   - Intervallo di campionamento (campo editabile)
6. Modifica il valore **"Sampling Interval"**
   - Range consentito: **10–3600 secondi**
7. Clicca **"SALVA CONFIGURAZIONE"**
8. Il sistema aggiorna `device.samplingIntervalSeconds` nel database
9. Reindirizza a `/web/configDevice/{uuid}?updated=true`
10. Mostra banner verde:
   - **"Configurazione salvata nel database"**

##### Flusso Secondario – Push Configurazione a Hardware
1. Dopo il salvataggio, l'admin clicca **"📡 INVIA ORA"**
2. Il sistema invoca `deviceService.pushConfigToHardware(uuid)`
3. *[MOCK]* Simula l'invio tramite protocollo **MQTT/HTTP** al dispositivo
4. Il dispositivo conferma la ricezione
5. Mostra banner blu:
   - **"Configurazione inviata con successo al dispositivo hardware"**

##### Flussi Alternativi

###### 6a – Valore Fuori Range
- L'admin inserisce un valore:
   - `< 10` oppure `> 3600` secondi
- La validazione **HTML5** previene il submit:
   - `min="10"`
   - `max="3600"`
- Il browser mostra un tooltip di errore

###### 3a – Dispositivo Eliminato Durante Modifica
- Il sistema lancia `NoSuchElementException` durante il caricamento
- Mostra pagina **404 – "Dispositivo non trovato"**

#### UC3 - Creazione Nuova Spedizione

**ID**: UC3  
**Attore Primario**: User (Operatore Logistico)  
**Livello**: Obiettivo utente  
**Precondizioni**:
- L'utente è autenticato (ruolo USER o ADMIN)

**Postcondizioni di Successo**:
- Una nuova spedizione è creata con stato `active = true`
- La spedizione è visibile nella lista generale
- Nessun dispositivo è ancora associato

#### Flusso Principale
1. L'utente accede alla dashboard e clicca "+ Nuova Spedizione"
2. Il sistema mostra il form `/web/newShipment`
3. L'utente compila i campi obbligatori:
   - **Codice Identificativo**: ID univoco (es. "SH-2026-001")
   - **Destinazione**: Indirizzo o città di arrivo
   - **Descrizione**: Tipo di merce trasportata
4. Esempio di compilazione:
```
   Codice: SH-2026-PHARMA-MI
   Destinazione: Milano, Via Dante 15
   Descrizione: Vaccini COVID-19, catena del freddo 2-8°C
```
5. Clicca "REGISTRA SPEDIZIONE"
6. Il sistema valida: shipmentId non esiste già nel database e Tutti i campi obbligatori sono compilati
7. Crea entità Shipment
8. Reindirizza a `/web/shipments` con la nuova entry visualizzata
9. 
#### Flussi Alternativi
- **6a - Codice Spedizione Duplicato**:
   1. Il sistema rileva entry esistente tramite `findShipmentByShipmentId()`
   2. Lancia `DataIntegrityViolationException`
   3. Mostra errore: "Codice spedizione già utilizzato"
   4. L'utente modifica il codice e riprova
- **6b - Campi Mancanti**:
   1. Il browser previene submit per campi `required` vuoti
   2. Evidenzia i campi in rosso
   3. L'utente completa il form

#### Diagramma Stati Spedizione
```
[CREATA] ──────────────────────────────────────────> [COMPLETATA]
active=true                                          active=false
Può ricevere device                                  Archiviata, dati read-only
Accetta telemetria                                   Device liberati
```

#### UC4 – Associazione Dispositivo a Spedizione

- **ID:** UC4
- **Attore Primario:** User
- **Livello:** Sottofunzione

##### Precondizioni
- Esiste almeno una spedizione con `active = true`
- Esistono dispositivi con stato **REGISTERED** (disponibili)

#### Postcondizioni di Successo
- Il dispositivo cambia stato da **REGISTERED** a **ACTIVE**
- Il dispositivo è collegato alla spedizione tramite **FK**
- Il dispositivo inizia a inviare telemetria valida

#### Flusso Principale
1. L'utente visualizza la lista delle spedizioni (`/web/shipments`)
2. Identifica la spedizione target
   - esempio: `SH-2026-PHARMA-MI`
3. Clicca sull'icona **"Associa Sensore"**
4. Il sistema carica la pagina `/web/shipmentAllocate/{id}` mostrando:
   - Dettagli spedizione:
      - codice
      - destinazione
      - dispositivi già associati
   - Dropdown dei dispositivi disponibili:
      - solo dispositivi con `status = REGISTERED`
5. Il sistema esegue la query per recuperare i dispositivi disponibili
6. L'utente seleziona un dispositivo dal menu:
   - esempio: `SN-2026-XF8 (Interval: 60s)`
7. L'utente clicca **"CONFERMA ALLOCAZIONE"**
8. Il sistema esegue le validazioni:
   - La spedizione è ancora `active = true`
   - Il dispositivo è ancora `status = REGISTERED` (gestione concorrenza)
9. Il sistema aggiorna il database:
   - Associa il dispositivo alla spedizione tramite FK
   - Imposta `device.status = DeviceStatus.ACTIVE`
10. Reindirizza a `/web/shipments`
11. L'utente vede il contatore "Device Associati" incrementato

#### Flussi Alternativi
- **5a - Nessun Device Disponibile**:
   1. Il sistema trova `availableDevices.isEmpty() == true`
   2. Mostra warning arancione: "Non ci sono sensori in stato READY"
   3. Pulsante "Fai Provisioning" rimanda a `/web/provision`
   4. L'admin deve prima registrare nuovi dispositivi

- **8a - Spedizione Disattivata Durante Selezione**:
   1. Un altro utente ha completato la spedizione concorrentemente
   2. Il sistema lancia `IllegalStateException`
   3. Mostra errore: "Impossibile associare device a spedizione non attiva"
   4. Suggerisce di selezionare un'altra spedizione

- **8b - Device Già Allocato**:
   1. Un altro utente ha associato il device concorrentemente
   2. Validazione fallisce: `device.getStatus() != REGISTERED`
   3. Mostra errore: "Il dispositivo è già in uso"
   4. Ricarica la lista aggiornata

#### Vincoli di Integrità
- Un device può essere associato a **una sola spedizione attiva** per volta
- Una spedizione può avere **N dispositivi** (tracking ridondante per merci critiche)
- La relazione è implementata come `@ManyToOne` unidirezionale da Device a Shipment

### Diagramma Sequenza (semplificato)
```
User -> Controller: POST /web/shipmentAllocate
Controller -> Service: associateDeviceToShipment(shipmentId, deviceUid)
Service -> Repository: findById(shipmentId)
Service -> Repository: findDeviceByUuid(deviceUid)
Service -> [Validation]: checkActive() & checkRegistered()
Service -> Device: setShipment(shipment)
Service -> Device: setStatus(ACTIVE)
Service -> Repository: save(device)
Service -> Controller: success
Controller -> User: redirect /web/shipments
```
#### UC5 – Invio Telemetria da Dispositivo IoT

- **ID:** UC5
- **Attore Primario:** Device IoT
- **Livello:** Sottofunzione

##### Precondizioni
- Il dispositivo è in stato **ACTIVE**
- Il dispositivo è associato a una spedizione con `active = true`
- Il dispositivo possiede una **API-Key** valida

#### Postcondizioni di Successo
- Un record **TrackData** è creato nel database
- Il timestamp corrente è registrato
- I dati sono disponibili per la visualizzazione immediata

#### Flusso Principale
1. Il dispositivo IoT rileva i parametri ambientali:
   - Temperatura: `4.2°C` (sensore DS18B20)
   - Umidità: `65%` (sensore DHT22)
   - GPS:
      - Latitudine: `45.4642`
      - Longitudine: `9.1900` (modulo NEO-6M)
2. Il dispositivo costruisce un payload JSON contenente:
   - temperature
   - humidity
   - latitude
   - longitude
3. Il dispositivo invia una richiesta **HTTP POST** a `/api/tracking/data`:
   - Content-Type: `application/json`
   - Header di autenticazione: `X-API-KEY`
4. Il server valida la **API-Key**
5. Il server verifica che il dispositivo sia in stato **ACTIVE**
6. Il server verifica che:
   - il dispositivo sia associato a una spedizione
   - la spedizione sia ancora `active = true`
7. Il server crea una nuova entità **TrackData** valorizzando:
   - temperatura
   - umidità
   - coordinate GPS
   - timestamp corrente
   - riferimento al dispositivo
   - riferimento alla spedizione
8. Il server persiste il dato nel database
9. Il server risponde con **200 OK** e messaggio di conferma
10. Il dispositivo riceve l’ACK e attende il prossimo intervallo di invio

#### Flussi Alternativi

##### 4a – API-Key Invalida
- Il server non trova corrispondenza della API-Key nel database
- Risponde con **401 Unauthorized**
- Messaggio: *"Invalid API Key"*
- Il dispositivo logga l’errore e riprova dopo 1 minuto
- Se l’errore persiste:
   - attiva LED rosso di errore
   - segnala necessità di manutenzione

##### 5a – Dispositivo Non Attivo
- Il dispositivo risulta in stato **REGISTERED** o **DECOMMISSIONED**
- Il server risponde con **400 Bad Request**
- Messaggio: *"Device not active or not associated to a shipment"*
- Il dispositivo entra in modalità standby
- Nessun dato viene inviato

##### 6a – Spedizione Completata Durante il Trasporto
- La spedizione associata è stata completata (`active = false`)
- Il server risponde con **403 Forbidden**
- Messaggio:
   - *"The device is not associated to a shipment or the shipment is not active"*
- Il dispositivo interrompe l’invio della telemetria



#### Business Rules
- Solo dispositivi in stato **ACTIVE** possono inviare telemetria
- Ogni record di telemetria deve essere associato a:
   - un solo dispositivo
   - una sola spedizione attiva
- La validazione della **API-Key** è obbligatoria per ogni richiesta

#### UC6 – Monitoraggio Live su Mappa

- **ID:** UC6
- **Attore Primario:** User / Autorità
- **Livello:** Obiettivo utente

##### Precondizioni
- Esistono spedizioni con almeno un dato di tracking
- L'utente ha accesso a un browser moderno

#### Postcondizioni di Successo
- La mappa mostra la posizione GPS più recente
- La tabella di telemetria è popolata con gli ultimi **10** record
- Il polling automatico aggiorna i dati ogni **10 secondi**

#### Flusso Principale
1. L'utente accede alla pagina `/web/map`
2. Il sistema carica la vista mappa basata su **Leaflet.js**:
   - Centro: Italia (`45.46°N`, `9.19°E`)
   - Zoom iniziale: `6`
3. La sidebar sinistra mostra la lista delle spedizioni attive
4. L'utente clicca su una spedizione
5. Il browser richiede i dati di tracking relativi alla spedizione selezionata
6. l server restituisce un array JSON contenente i record di telemetria
7. Il client elabora i dati ricevuti
8. Il marker sulla mappa mostra un popup informativo
9. La tabella evidenzia le temperature anomale
10. Dopo 10 secondi, il sistema ripete automaticamente la richiesta dati (polling)

#### Flussi Alternativi

##### 6a – Nessun Dato Disponibile
- Il server restituisce un array vuoto []
- La mappa resta centrata sull’Italia senza marker
- La tabella mostra il messaggio: "Nessuna rilevazione disponibile"
- Il polling automatico continua


#### Business Rules
- Solo i dati relativi a spedizioni attive possono essere visualizzati
- Il sistema deve mostrare sempre l’ultimo punto GPS disponibile
- Il polling non deve bloccare l’interazione utente
- Le anomalie di temperatura devono essere immediatamente visibili

### UC7 - Verifica Pubblica Dati Storici

**ID**: UC7  
**Attore Primario**: Autorità / Consumatore  
**Livello**: Obiettivo utente  
**Precondizioni**:
- La spedizione è stata completata (o è ancora attiva)
- L'ID spedizione è noto (es. da QR-code su confezione)

**Postcondizioni di Successo**:
- L'autorità ottiene l'intero storico telemetrico
- I dati sono forniti in formato machine-readable (JSON)
- L'accesso è tracciato nei log per audit

#### Flusso Principale
1. L'autorità riceve il prodotto con etichetta QR-code
2. Scansiona il QR sul dispositivo
3. Il QR reindirizza a una landing page pubblica con tasto "Verifica Dati"
4. Alternativamente, l'autorità può fare richiesta diretta:
```bash
   curl https://smarttrack.example.com/api/tracking/shipment/SH-2026-PHARMA-MI
```
5. Il server esegue query:
```java
   List<TrackData> history = trackingDataRepository
       .findByShipment_ShipmentId("SH-2026-PHARMA-MI");
```
6. Risponde con JSON completo:
```json
   [
     {
       "id": 1,
       "temperature": 4.2,
       "humidity": 65.0,
       "latitude": 45.4642,
       "longitude": 9.1900,
       "timestamp": "2026-01-26T08:00:00",
       "device": {
         "uuid": "SN-2026-XF8",
         "status": "ACTIVE",
         "samplingIntervalSeconds": 60
       }
     },
     {
       "id": 2,
       "temperature": 4.5,
       "humidity": 63.0,
       "latitude": 45.5000,
       "longitude": 9.2500,
       "timestamp": "2026-01-26T08:01:00",
       "device": {
         "uuid": "SN-2026-XF8",
         "status": "ACTIVE",
         "samplingIntervalSeconds": 60
       }
     }
   ]
```
7. L'autorità analizza i dati con tool automatici:
   - **Verifica Catena del Freddo**: Tutti i valori 2-8°C ✓
   - **Continuità Tracking**: Nessun gap >2 minuti ✓
   - **Percorso GPS**: Coerente con route dichiarato ✓
8. Emette certificazione: "Trasporto conforme a normativa EU 2023/1574"

#### Flussi Alternativi
- **5a - Spedizione Non Trovata**:
   1. Il repository restituisce lista vuota
   2. Risponde: `200 OK` con `[]`
   3. L'autorità interpreta come "spedizione mai partita" o ID errato

- **4a - Richiesta Malformata**:
   1. L'ID contiene caratteri speciali non escapati
   2. Spring valida il path variable
   3. Risponde: `400 Bad Request`
### UC8 - Decommissioning Dispositivo
- **ID:** UC8
- **Attore Primario:** Admin
- **Livello:** Sottofunzione

##### Precondizioni
- Il dispositivo esiste nel sistema

#### Postcondizioni di Successo
- Il dispositivo è in stato **DECOMMISSIONED**
- Il dispositivo è dissociato da eventuali spedizioni

#### Flusso Principale
1. L'admin visualizza la lista dei dispositivi
2. Clicca sull'icona **"Dismetti"**
3. Conferma l'operazione tramite popup JavaScript
4. Il sistema aggiorna il dispositivo:
   - dissocia eventuali spedizioni
   - imposta `device.status = DECOMMISSIONED`
   - salva il dispositivo nel database
5. Il sistema effettua un redirect mostrando un messaggio di conferma

#### Flussi Alternativi

##### 4a – Device in uso attivo
- Il dispositivo è attualmente associato a una spedizione attiva
- Il sistema forza la dissociazione come scelta di design
- L'operazione prosegue normalmente, il dispositivo viene dismesso

#### UC9 – Completamento Spedizione

- **ID:** UC9
- **Attore Primario:** User
- **Livello:** Sottofunzione

##### Precondizioni
- La spedizione è attiva
- La spedizione ha dispositivi associati

#### Postcondizioni di Successo
- La spedizione è archiviata (`active = false`)
- I dispositivi associati tornano disponibili (`status = REGISTERED`)

#### Flusso Principale
1. L'utente clicca **"COMPLETA"** per la spedizione arrivata
2. Conferma l'operazione tramite popup
3. Il sistema esegue una transazione:
   - Imposta `shipment.active = false`
   - Per ogni dispositivo associato:
      - Imposta `device.status = REGISTERED`
      - Dissocia il dispositivo dalla spedizione (`device.shipment = null`)
   - Salva la spedizione tramite `shipmentRepository.save(shipment)`
4. I dispositivi tornano disponibili per nuove spedizioni

#### Business Logic
- Permette il riutilizzo dei sensori (low-cost)
- Mantiene lo storico dei dati (audit trail)
- Previene modifiche dopo il completamento della spedizione

### Diagrammi di Stato
```text
┌────────────┐
│ REGISTERED │ ◄──────────────┐
└─────┬──────┘                │
      │ associa               │ completa
      │ device                │ spedizione
      ▼                       │
┌────────────┐                │
│   ACTIVE   │ ───────────────┘
└─────┬──────┘
      │ decommission
      ▼
┌────────────────┐
│ DECOMMISSIONED │
└────────────────┘
Stati Spedizione
┌─────────────────┐
│ CREATED         │
│ active = true   │
└────────┬────────┘
         │ completa
         ▼
┌─────────────────┐
│ COMPLETED       │
│ active = false  │
└─────────────────┘
```
# SmartTracking: Sistema di Monitoraggio Logistico per Beni Fragili e Sensibili

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Coverage](https://img.shields.io/badge/coverage-70%25-yellow)]()
[![License](https://img.shields.io/badge/license-MIT-blue)]()

> Sistema di tracciamento real-time per spedizioni contenenti beni sensibili tramite dispositivi IoT a basso costo

**Università degli Studi di Verona**  
**Corso**: Progettazione e Validazione di Sistemi Software  
**Docente**: Prof. Mariano Ceccato  
**Anno Accademico**: 2025/2026  
**Data**: Febbraio 2026

---

## Indice

1. [Panoramica del Progetto](#1-panoramica-del-progetto)
2. [Requisiti e Analisi](#2-requisiti-e-analisi)
3. [Architettura e Design](#3-architettura-e-design)
4. [Installazione e Setup](#4-installazione-e-setup)
5. [Utilizzo](#5-utilizzo)
6. [Scenari e Casi d'Uso](#6-scenari-e-casi-duso)
8. [Testing](#8-testing)


---

## 1. Panoramica del Progetto

### 1.1 Problema Affrontato

Le filiere logistiche tradizionali per beni sensibili (farmaci, alimenti, opere d'arte) soffrono di:
- **Mancanza di trasparenza**: impossibilità di verificare le condizioni di trasporto
- **Assenza di certificabilità**: dati ambientali non tracciabili e verificabili
- **Costi elevati**: sistemi di monitoraggio tradizionali poco scalabili
- **Rischio di manomissione**: nessun controllo su integrità hardware/software

### 1.2 Obiettivi del Progetto

SmartTracking fornisce una soluzione end-to-end che garantisce:

- ✅ **Trasparenza nella filiera logistica** tramite tracking GPS real-time
- ✅ **Certificabilità dei dati ambientali** con API pubblica per audit
- ✅ **Scalabilità** per gestire flotte di sensori IoT a basso costo
- ✅ **Sicurezza** contro manomissioni hardware/software tramite autenticazione API-Key

### 1.3 Contesto Operativo

Il sistema simula l'ambiente di una società logistica in cui:

- **Amministratori** gestiscono l'infrastruttura IoT (provisioning, configurazione, decommissioning)
- **Operatori logistici** creano e monitorano le spedizioni
- **Dispositivi IoT** raccolgono automaticamente dati ambientali (temperatura, umidità, GPS)
- **Autorità esterne** possono verificare lo storico delle condizioni di trasporto

---

## 2. Requisiti e Analisi

### 2.1 Stakeholder

| Stakeholder | Ruolo | Esigenze Principali |
|-------------|-------|---------------------|
| **Amministratore IT** | Gestione infrastruttura IoT | Provisioning dispositivi, configurazione parametri, monitoring |
| **Operatore Logistico** | Gestione spedizioni | Creazione spedizioni, associazione sensori, tracking real-time |
| **Autorità di Controllo** | Audit e certificazione | Accesso storico dati, verifica conformità normative |
| **Cliente Finale** | Destinatario merce | Trasparenza sulle condizioni di trasporto |

### 2.2 Requisiti Funzionali

| ID | Descrizione | Priorità | Stakeholder |
|----|-------------|----------|-------------|
| **RF1** | Registrazione e gestione dispositivi IoT (provisioning/decommissioning) | Alta | Admin |
| **RF2** | Configurazione parametri di campionamento per ogni sensore | Alta | Admin |
| **RF3** | Creazione e gestione spedizioni con associazione sensori | Alta | User |
| **RF4** | Ricezione telemetria automatica da dispositivi (temp, umidità, GPS) | Alta | Sistema |
| **RF5** | Visualizzazione real-time su mappa interattiva | Media | User |
| **RF6** | API pubblica per verifica storico dati | Alta | Autorità |
| **RF7** | Gestione utenti con controllo accessi basato su ruoli | Media | Admin |
| **RF8** | Riutilizzo sensori per multiple spedizioni (ciclo di vita) | Alta | User |

### 2.3 Requisiti Non Funzionali

#### Sicurezza (RNF-SEC)
- **RNF-SEC-1**: Autenticazione API-Key univoca (UUID v4) per ogni dispositivo IoT
- **RNF-SEC-2**: Password utenti cifrate con algoritmo BCrypt
- **RNF-SEC-3**: Controllo accessi basato su ruoli (RBAC): ADMIN, USER
- **RNF-SEC-4**: Protezione CSRF per form web

#### Usabilità (RNF-UX)
- **RNF-UX-1**: Tempo di apprendimento sistema < 30 minuti per operatore esperto
- **RNF-UX-2**: Feedback visivo immediato per ogni operazione (success/error messages)

#### Performance (RNF-PERF)
- **RNF-PERF-1**: Refresh mappa real-time ogni 10 secondi (polling)


#### Manutenibilità (RNF-MAINT)
- **RNF-MAINT-1**: Architettura MVC + Service Layer per separazione responsabilità
- **RNF-MAINT-2**: Logging estensivo di operazioni critiche (SLF4J)
- **RNF-MAINT-3**: Configurazione esternalizzata (application.properties)

#### Testabilità (RNF-TEST)
- **RNF-TEST-1**: Code coverage minimo 70% (unit test + integration test)
- **RNF-TEST-2**: Acceptance test automatizzati con Selenium
- **RNF-TEST-3**: Mock dei dispositivi IoT per testing end-to-end

### 2.4 Attori del Sistema

#### 2.4.1 Admin (Amministratore)
**Descrizione**: Responsabile tecnico dell'infrastruttura IoT e della gestione utenti.

**Responsabilità**:
- Eseguire il provisioning di nuovi dispositivi fisici
- Configurare parametri di campionamento (intervalli, soglie)
- Dismettere dispositivi obsoleti o malfunzionanti
- Gestire anagrafica utenti (visualizzazione, eliminazione)

**Competenze richieste**: Conoscenza base di sistemi IoT, gestione infrastrutture IT

---

#### 2.4.2 User (Operatore Logistico)
**Descrizione**: Utente standard che gestisce le operazioni quotidiane di spedizione.

**Responsabilità**:
- Creare nuove spedizioni specificando destinazione e contenuto
- Associare sensori disponibili alle spedizioni
- Monitorare lo stato real-time delle spedizioni attive
- Completare spedizioni e liberare i sensori per riutilizzo

**Competenze richieste**: Conoscenza processi logistici, uso interfacce web

---

#### 2.4.3 Device IoT (Sensore Hardware)
**Descrizione**: Dispositivo fisico embedded con capacità di rilevazione ambientale e connettività wireless.

**Funzioni Tecniche**:
- Acquisizione ciclica di:
    - Temperatura 
    - Umidità relativa 
    - Coordinate GPS
- Trasmissione dati via HTTP POST ogni N secondi (configurabile)
- Autenticazione tramite API-Key univoca (UUID v4)

**Lifecycle Stati**:
1. **REGISTERED**: Dispositivo registrato ma non in uso
2. **ACTIVE**: Associato a spedizione attiva, invia telemetria
3. **DECOMMISSIONED**: Dismesso, non più utilizzabile

---

#### 2.4.4 Autorità Esterna
**Descrizione**: Ente di controllo (es. ASL, dogane) che verifica conformità normativa.

**Responsabilità**:
- Accedere allo storico telemetrico via API pubblica
- Verificare catena del freddo per prodotti farmaceutici
- Certificare conformità a normative (es. EU 2023/1574)

**Interazioni**: Solo lettura tramite endpoint pubblico `/api/tracking/shipment/{id}`

---

## 3. Architettura e Design

### 3.1 Pattern Architetturale

Il sistema adotta un'architettura **MVC + Service Layer** basata su Spring Boot:

```
┌─────────────────────────────────────────────────────┐
│              PRESENTATION LAYER                     │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │
│  │ Thymeleaf    │  │  REST API    │  │  Static    │ │
│  │  Templates   │  │ Controllers  │  │   Assets   │ │
│  └──────────────┘  └──────────────┘  └────────────┘ │
└─────────────────────────────────────────────────────┘
                        ▼
┌─────────────────────────────────────────────────────┐
│               BUSINESS LAYER                        │
│  ┌──────────────────────────────────────────────┐   │
│  │          Service Layer                       │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────────┐  │   │
│  │  │ Device   │ │Shipment  │ │  Tracking    │  │   │
│  │  │ Service  │ │ Service  │ │   Service    │  │   │
│  │  └──────────┘ └──────────┘ └──────────────┘  │   │
│  └──────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
                        ▼
┌─────────────────────────────────────────────────────┐
│              PERSISTENCE LAYER                      │
│  ┌──────────────────────────────────────────────┐   │
│  │   Spring Data JPA Repositories               │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────────┐  │   │
│  │  │ Device   │ │Shipment  │ │ TrackData    │  │   │
│  │  │   Repo   │ │   Repo   │ │     Repo     │  │   │
│  │  └──────────┘ └──────────┘ └──────────────┘  │   │
│  └──────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
                        ▼
┌─────────────────────────────────────────────────────┐
│                  DATA LAYER                         │
│              H2 Database (in-memory)                │
└─────────────────────────────────────────────────────┘
```

### 3.2 Modello di Dominio (Entity-Relationship)

```
┌─────────────────┐         ┌─────────────────┐
│     Device      │         │    Shipment     │
├─────────────────┤         ├─────────────────┤
│ id: Long (PK)   │         │ id: Long (PK)   │
│ uuid: String    │         │ shipmentId: Str │
│ apiKey: String  │◄───────┐│ destination: Str│
│ status: Enum    │ 1    * ││ description: Str│
│ samplingInt: Int│        ││ active: Boolean │
│ shipment: FK    │        │└─────────────────┘
└─────────────────┘        │         △
        △                  │         │
        │                  │         │
        │                  │         │ 1
        │ 1                │         │
        │                  └─────────┤
        │                          * │
┌─────────────────┐         ┌─────────────────┐
│   TrackData     │         │      User       │
├─────────────────┤         ├─────────────────┤
│ id: Long (PK)   │         │ id: Long (PK)   │
│ temperature: Dbl│         │ username: String│
│ humidity: Double│         │ password: String│
│ latitude: Double│         │ role: Enum      │
│ longitude: Dbl  │         └─────────────────┘
│ timestamp: DT   │
│ device: FK      │
│ shipment: FK    │
└─────────────────┘
```

**Relazioni**:
- `Device` (1) ↔ (0..1) `Shipment`: Un dispositivo può essere associato a max 1 spedizione attiva
- `Shipment` (1) ↔ (*) `Device`: Una spedizione può avere N dispositivi associati
- `Device` (1) ↔ (*) `TrackData`: Un dispositivo genera N record di telemetria
- `Shipment` (1) ↔ (*) `TrackData`: Una spedizione accumula N rilevazioni

### 3.3 Diagrammi di Stato

#### Stati Dispositivo IoT
```
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
```

**Transizioni**:
- `REGISTERED → ACTIVE`: Dispositivo associato a spedizione attiva
- `ACTIVE → REGISTERED`: Spedizione completata, dispositivo disponibile
- `ACTIVE → DECOMMISSIONED`: Dispositivo guasto/obsoleto
- `REGISTERED → DECOMMISSIONED`: Dismissione diretta

---

#### Stati Spedizione
```
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

**Invarianti**:
- Una spedizione completata (`active = false`) non può essere riattivata
- I dispositivi associati a spedizioni completate tornano in `REGISTERED`

### 3.4 Scelte Tecnologiche

| Componente | Tecnologia         | Versione | Motivazione                                                |
|------------|--------------------|----------|------------------------------------------------------------|
| **Backend** | Spring Boot        | 3.x | Framework enterprise standard, dependency injection nativa |
| **ORM** | Spring Data JPA    | 3.x | Astrazione database, repository pattern built-in           |
| **Database** | H2 (in-memory)     | 2.x | Prototipazione rapida, zero configurazione                 |
| **Template Engine** | Thymeleaf          | 3.x | Integrazione nativa Spring, syntax HTML-like               |
| **CSS Framework** | Tailwind CSS       | 3.x | Utility-first, customizzazione rapida                      |
| **Mappa Interattiva** | Leaflet.js         | 1.9 | Leggero, open-source, mobile-friendly                      |
| **Testing** | JUnit 5 + Selenium | - | Standard Java acceptance test                              |
| **Security** | Spring Security    | 3.x | Autenticazione/autorizzazione enterprise-grade             |
| **Logging** | SLF4J + Logback    | - | Standard de-facto Java, configurazione flessibile          |

### 3.5 Decisioni di Design Chiave

#### 3.5.1 Autenticazione Dispositivi IoT
**Decisione**: Utilizzare API-Key statiche invece di OAuth2/JWT.

**Motivazione**:
- Dispositivi embedded con risorse limitate 
- Nessuna necessità di refresh token
- Semplicità di provisioning (scan QR-code → copia API-Key)
---

#### 3.5.2 Database In-Memory
**Decisione**: H2 invece di PostgreSQL/MySQL per ambiente di sviluppo.

**Motivazione**:
- Zero configurazione per demo/testing
- Facile reset dello stato tra test
- Script SQL di init versionati

**Migrazione Produzione**: Cambio dialetto in `application.properties` per DB persistente.

---

#### 3.5.3 Polling invece di WebSocket
**Decisione**: Aggiornamento mappa tramite polling HTTP ogni 10s invece di WebSocket.

**Motivazione**:
- Infrastruttura più semplice 
- Carico server accettabile per PoC

---

## 4. Installazione e Setup

### 4.1 Prerequisiti

#### Software Richiesto
- **Java Development Kit (JDK)**: versione 17 o superiore
- **Gradle**: versione 8.1+ (build automation)
- **Git**: per clonare il repository
- **Browser Moderno**: Chrome 90+, Firefox 88+, Safari 14+ (per interfaccia web)


### 4.2 Installazione

#### 4.2.1 Clonare il Repository
```bash
git clone https://github.com/SimonePavanello/SmartTracking
```

#### 4.2.2 Configurare il Database
Il sistema utilizza H2 in-memory di default. Nessuna configurazione aggiuntiva richiesta.

#### 4.2.3 Compilare il Progetto
```bash
gradle build
```

**Output atteso**:
```
[INFO] BUILD SUCCESS
[INFO] Total time: 45.231 s
```



**Server avviato su**: `http://localhost:8080`

### 4.4 Verifica Installazione

#### Test : Accesso Interfaccia Web
1. Aprire browser su `http://localhost:8080`
2. Verificare redirect automatico a `/user/signin`
3. Login con credenziali di default:
    - **Username**: `admin`
    - **Password**: `123456789`



### 4.5 Configurazioni Avanzate

#### 4.5.1 Modificare Porta Server
Editare `application.properties`:
```properties
server.port=9090
```

#### 4.5.2 Abilitare SQL Logging
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

#### 4.5.3 Configurare Intervallo Polling Mappa
Modificare `map.html` (linea ~120):
```javascript
const POLLING_INTERVAL_MS = 5000; // da 10000 a 5000 per refresh ogni 5s
```

---

## 5. Utilizzo

### 5.1 Quick Start (Scenario Completo)

Questo esempio mostra il workflow completo: provisioning dispositivo → creazione spedizione → invio telemetria → monitoraggio.

#### Step 1: Login come Admin
```
URL: http://localhost:8080/user/signin
Username: admin
Password: 123456789
```

#### Step 2: Provisioning Dispositivo
```
1. Navigare su: "Nuovo Provisionig"
2. Inserire UID: SN-2026-TEST001
3. Cliccare: "COMPLETA PROVISIONING"
4. Copiare API-Key generata (es. "abc-123-def-456")
```

#### Step 3: Creare Spedizione
```
1. Profilo → "Gestione Spedizioni" → "+ Nuova Spedizione"
2. Compilare:
   - Codice: SH-2026-DEMO
   - Destinazione: Milano, Via Dante 15
   - Descrizione: Test farmaci catena freddo
3. Cliccare: "REGISTRA SPEDIZIONE"
```

#### Step 4: Associare Dispositivo
```
1. Nella lista spedizioni, cliccare "Associa Sensore" su SH-2026-DEMO
2. Selezionare: SN-2026-TEST001
3. Confermare Allocazione
```

#### Step 5: Simulare Invio Telemetria
```bash
curl -X POST http://localhost:8080/api/tracking/data \
  -H "Content-Type: application/json" \
  -H "X-API-Key: abc-123-def-456" \
  -d '{
    "temperature": 4.2,
    "humidity": 65.0,
    "latitude": 45.4642,
    "longitude": 9.1900
  }'
```

**Risposta attesa**: `200 OK` con messaggio `"Data Received"`

#### Step 6: Visualizzare su Mappa
```
1. Navigare su: Profilo → "Mappa Live"
2. Selezionare spedizione: SH-2026-DEMO
3. Verificare marker su Milano con popup dati
```

### 5.2 Gestione Dispositivi (Admin)

#### 5.2.1 Configurare Intervallo Campionamento
```
1. Dashboard Admin → "Dispositivi"
2. Cliccare icona "Configura" su dispositivo target
3. Modificare "Sampling Interval": 30 (secondi)
4. Cliccare: "SALVA CONFIGURAZIONE"
```

#### 5.2.2 Decommissioning Dispositivo
```
1. Dashboard Admin → "Dispositivi"
2. Cliccare icona "Dismetti" su dispositivo
3. Confermare popup JavaScript
```

**Effetto**:
- Dispositivo passa a stato `DECOMMISSIONED`
- Eventuale spedizione associata viene dissociata
- API-Key diventa invalida  `Error: The device is not associated to a shipment or the shipment is not active`

### 5.3 Gestione Spedizioni (User)

#### 5.3.1 Monitoraggio Real-Time
```
1. Login come User (username: user, password: user123)
2. Dashboard → "Mappa Live"
3. Selezionare spedizione dalla sidebar
4. Monitorare:
   - Posizione GPS aggiornata ogni 10s
   - Tabella ultimi 10 record di telemetria
   - Evidenziazione temperature anomale 
```

#### 5.3.2 Completare Spedizione
```
1. Dashboard → "Spedizioni"
2. Identificare spedizione arrivata
3. Cliccare: "COMPLETA"
4. Confermare popup
```

**Effetto**:
- Spedizione archiviata (`active = false`)
- Dispositivi associati tornano `REGISTERED` e disponibili

### 5.4 API Pubblica per Autorità

#### 5.4.1 Recuperare Storico Spedizione
```bash
curl http://localhost:8080/api/tracking/shipment/SH-2026-DEMO
```

**Risposta JSON**:
```json
[
  {
    "id": 1,
    "temperature": 4.2,
    "humidity": 65.0,
    "latitude": 45.4642,
    "longitude": 9.1900,
    "timestamp": "2026-02-04T10:30:00",
    "device": {
      "uuid": "SN-2026-TEST001",
      "status": "ACTIVE",
      "samplingIntervalSeconds": 60
    }
  }
]
```
---

## 6. Scenari e Casi d'Uso

### 6.1 Diagramma Overview Casi d'Uso
```
                    ┌─────────────────┐
                    │   SmartTrack    │
                    │     System      │
                    └─────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
    ┌───▼───┐          ┌────▼────┐        ┌────▼────┐
    │ Admin │          │  User   │        │Authority│
    └───┬───┘          └────┬────┘        └────┬────┘
        │                   │                  │
        ├─ UC1: Provision   ├─ UC3: New Ship   ├─ UC7: Verify
        ├─ UC2: Configure   ├─ UC4: Associate  │      History
        ├─ UC8: Decommission├─ UC6: Monitor Map│
        │                   ├─ UC9: Complete   │
        │                   │                  │
   ┌────▼────┐         ┌────▼────┐             │
   │ Device  │◄────────│ UC5:    │             │
   │  IoT    │         │ Send    │             │
   └─────────┘         │Telemetry│             │
                       └─────────┘             │
```

### 6.2 UC1 – Provisioning di un Nuovo Dispositivo

**ID**: UC1  
**Attore Primario**: Admin  
**Livello**: Funzione di sistema  
**Frequenza**: 1-10 volte al giorno (nuovi dispositivi)

#### Precondizioni
- L'admin è autenticato con ruolo **ADMIN**
- Il dispositivo fisico è disponibile e operativo
- L'UID del dispositivo è leggibile tramite QR-code

#### Postcondizioni di Successo
- Il dispositivo è registrato nel database con stato **REGISTERED**
- Una **API-Key** univoca è generata e associata
- I parametri di default sono impostati:
    - `samplingIntervalSeconds = 60`

#### Flusso Principale
1. L'admin naviga su `/web/provision`
2. Scansiona il QR-code presente sul dispositivo fisico
    - Ottiene un UID univoco (es. `SN-2026-XF8`)
3. Inserisce l'UID nel campo di testo del form
4. Clicca **"COMPLETA PROVISIONING"**
5. Il sistema verifica l'univocità dell'UID nel database
6. Il sistema genera:
   ```java
   UUID apiKey = UUID.randomUUID().toString();
   DeviceStatus status = DeviceStatus.REGISTERED;
   int samplingIntervalSeconds = 60;
   ```
7. Il dispositivo viene persistito tramite `DeviceRepository.save()`
8. L'admin visualizza un messaggio di conferma con i dettagli della API-Key

#### Flussi Alternativi

**5a – UID Duplicato**
- Il sistema rileva: `deviceRepository.existsDeviceByUuid(uid) == true`
- Lancia: `RuntimeException("Device already registered with uuid: " + uuid)`
- Mostra errore: *"Il dispositivo è già registrato nel sistema"*
- Il caso d'uso termina in fallimento

#### Business Rules
- Ogni dispositivo deve avere UID univoco a livello globale
- L'API-Key generata deve essere comunicata in modo sicuro al dispositivo
- Solo utenti con ruolo ADMIN possono eseguire provisioning

---

### 6.3 UC2 – Configurazione Parametri Dispositivo

**ID**: UC2  
**Attore Primario**: Admin  
**Livello**: Funzione di sistema

#### Precondizioni
- Il dispositivo esiste nel sistema (qualsiasi stato)
- L'admin ha accesso alla pagina di configurazione

#### Postcondizioni di Successo
- I nuovi parametri sono salvati nel database
- *(Opzionale)* La configurazione è inviata al dispositivo hardware via protocollo IoT

#### Flusso Principale – Aggiornamento Database
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
10. Mostra banner verde: **"Configurazione salvata nel database"**

#### Flusso Secondario – Push Configurazione a Hardware
1. Dopo il salvataggio, l'admin clicca **"INVIA ORA"**
2. Il sistema invoca `deviceService.pushConfigToHardware(uuid)`
3. *[MOCK]* Simula l'invio tramite protocollo **MQTT/HTTP** al dispositivo
4. Il dispositivo conferma la ricezione
5. Mostra banner blu: **"Configurazione inviata con successo al dispositivo hardware"**

#### Flussi Alternativi

**6a – Valore Fuori Range**
- L'admin inserisce: `< 10` oppure `> 3600` secondi
- La validazione **HTML5** previene il submit (`min="10"` `max="3600"`)
- Il browser mostra tooltip di errore

**3a – Dispositivo Eliminato Durante Modifica**
- Il sistema lancia `NoSuchElementException` durante il caricamento
- Mostra pagina **404 – "Dispositivo non trovato"**

---

### 6.4 UC3 – Creazione Nuova Spedizione

**ID**: UC3  
**Attore Primario**: User (Operatore Logistico)  
**Livello**: Obiettivo utente

#### Precondizioni
- L'utente è autenticato (ruolo USER o ADMIN)

#### Postcondizioni di Successo
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
6. Il sistema valida:
    - `shipmentId` non esiste già nel database
    - Tutti i campi obbligatori sono compilati
7. Crea entità `Shipment` con `active = true`
8. Salva tramite `shipmentRepository.save()`
9. Reindirizza a `/web/shipments` con la nuova entry visualizzata

#### Flussi Alternativi

**6a - Codice Spedizione Duplicato**
1. Il sistema rileva entry esistente tramite `findShipmentByShipmentId()`
2. Lancia `RuntimeException("Shipment already exists with shipmentId: " + id)`
3. Mostra errore: *"Una spedizione con questo codice esiste già"*
4. L'utente modifica il codice e riprova

**6b - Campi Obbligatori Mancanti**
1. La validazione lato client (HTML5 `required`) previene il submit
2. Il browser evidenzia i campi vuoti
3. L'utente completa i dati e riprova

---

### 6.5 UC4 – Associazione Dispositivo a Spedizione

**ID**: UC4  
**Attore Primario**: User  
**Livello**: Sottofunzione

#### Precondizioni
- La spedizione esiste ed è attiva (`active = true`)
- Esistono dispositivi in stato `REGISTERED`

#### Postcondizioni di Successo
- Il dispositivo passa a stato `ACTIVE`
- Il dispositivo è associato alla spedizione
- Il dispositivo può iniziare a inviare telemetria

#### Flusso Principale
1. L'utente visualizza la lista spedizioni
2. Clicca "Associa Device" sulla spedizione target
3. Il sistema mostra dropdown con dispositivi `REGISTERED`
4. L'utente seleziona un dispositivo dal menu
5. Clicca "CONFERMA ASSOCIAZIONE"
6. Il sistema aggiorna:
   ```java
   device.setStatus(DeviceStatus.ACTIVE);
   device.setShipment(shipment);
   deviceRepository.save(device);
   ```
7. Mostra messaggio: *"Dispositivo {uuid} associato con successo"*
8. Il dispositivo appare nella lista dispositivi della spedizione

#### Flussi Alternativi

**3a – Nessun Dispositivo Disponibile**
- Il repository restituisce lista vuota
- Il sistema mostra: *"Nessun dispositivo disponibile. Eseguire provisioning."*
- L'utente deve creare/liberare dispositivi prima di procedere

**6a – Dispositivo Già Associato** (race condition)
- Un altro utente ha associato il dispositivo nel frattempo
- Il sistema rileva `device.status != REGISTERED`
- Mostra errore: *"Dispositivo non più disponibile"*
- L'utente seleziona un altro dispositivo

---

### 6.6 UC5 – Invio Telemetria da Dispositivo IoT

**ID**: UC5  
**Attore Primario**: Device IoT  
**Livello**: Sottofunzione  
**Frequenza**: Ogni N secondi (configurabile, default 60s)

#### Precondizioni
- Il dispositivo è in stato `ACTIVE`
- Il dispositivo è associato a una spedizione attiva
- Il dispositivo possiede API-Key valida

#### Postcondizioni di Successo
- Un nuovo record `TrackData` è salvato nel database
- Il record è associato sia al dispositivo che alla spedizione
- Il timestamp è quello di ricezione server

#### Flusso Principale
1. Il dispositivo acquisisce dati dai sensori:
   ```
   temperature = 4.2°C
   humidity = 65.0%
   gpsLat = 45.4642
   gpsLon = 9.1900
   ```
2. Costruisce payload JSON:
   ```json
   {
     "temperature": 4.2,
     "humidity": 65.0,
     "latitude": 45.4642,
     "longitude": 9.1900
   }
   ```
3. Invia HTTP POST a `/api/tracking/data`:
   ```
   Headers:
     Content-Type: application/json
     X-API-Key: abc-123-def-456
   ```
4. Il server valida:
    - Header `X-API-Key` presente
    - API-Key corrisponde a dispositivo registrato
5. Recupera dispositivo e spedizione associata
6. Crea entità `TrackData`:
   ```java
   TrackData data = new TrackData();
   data.setTemperature(payload.getTemperature());
   data.setHumidity(payload.getHumidity());
   data.setLatitude(payload.getLatitude());
   data.setLongitude(payload.getLongitude());
   data.setTimestamp(LocalDateTime.now());
   data.setDevice(device);
   data.setShipment(device.getShipment());
   ```
7. Salva tramite `trackingDataRepository.save(data)`
8. Risponde `200 OK` con messaggio: `"Tracking data saved successfully"`

#### Flussi Alternativi

**4a – API-Key Invalida**
- Nessun dispositivo trovato con quella API-Key
- Risponde `401 Unauthorized`
- Messaggio: *"Invalid API Key"*
- Il dispositivo logga errore e riprova dopo intervallo configurato

**4b – API-Key Mancante**
- Header `X-API-Key` non presente nella request
- Risponde `400 Bad Request`
- Messaggio: *"Missing API Key header"*
- Il dispositivo entra in error mode (LED rosso lampeggiante)

**5a – Dispositivo Non Attivo**
- Il dispositivo risulta in stato `REGISTERED` o `DECOMMISSIONED`
- Risponde `400 Bad Request`
- Messaggio: *"Device not active or not associated to a shipment"*
- Il dispositivo entra in standby mode

**6a – Spedizione Completata Durante il Trasporto**
- La spedizione associata è stata completata (`active = false`)
- Risponde `403 Forbidden`
- Messaggio: *"The device is not associated to a shipment or the shipment is not active"*
- Il dispositivo interrompe invio telemetria

#### Business Rules
- Solo dispositivi in stato `ACTIVE` possono inviare telemetria
- Ogni record di telemetria deve essere associato a:
    - un solo dispositivo
    - una sola spedizione attiva
- La validazione della API-Key è obbligatoria per ogni richiesta
- Il timestamp è gestito lato server per evitare clock skew

---

### 6.7 UC6 – Monitoraggio Live su Mappa

**ID**: UC6  
**Attore Primario**: User / Autorità  
**Livello**: Obiettivo utente

#### Precondizioni
- Esistono spedizioni con almeno un dato di tracking
- L'utente ha accesso a un browser moderno (JavaScript abilitato)

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
4. L'utente clicca su una spedizione (es. "SH-2026-PHARMA-MI")
5. Il browser richiede: `GET /api/tracking/shipment/SH-2026-PHARMA-MI`
6. Il server restituisce array JSON con ultimi 10 record
7. Il client elabora i dati:
    - Posiziona marker sulla mappa all'ultima coordinata GPS
    - Popola tabella con i record ordinati per timestamp DESC
8. Il marker mostra popup informativo:
   ```
   Spedizione: SH-2026-PHARMA-MI
   Temperatura: 4.2°C
   Umidità: 65%
   Ultimo aggiornamento: 10:35:22
   ```
9. La tabella evidenzia temperature anomale (>10°C o <0°C) in rosso
10. Dopo 10 secondi, il sistema ripete automaticamente la richiesta (polling)

#### Flussi Alternativi

**6a – Nessun Dato Disponibile**
- Il server restituisce array vuoto `[]`
- La mappa resta centrata sull'Italia senza marker
- La tabella mostra: *"Nessuna rilevazione disponibile"*
- Il polling automatico continua a interrogare

**10a – Perdita Connessione Durante Polling**
- La richiesta AJAX fallisce
- Il client mostra banner: *"Connessione persa, tentativo di riconnessione..."*
- Riprova dopo 5 secondi
- Dopo 3 tentativi falliti, mostra errore permanente

#### Business Rules
- Solo i dati relativi a spedizioni attive possono essere visualizzati
- Il sistema deve mostrare sempre l'ultimo punto GPS disponibile
- Il polling non deve bloccare l'interazione utente (asincrono)

---

### 6.8 UC7 – Verifica Pubblica Dati Storici

**ID**: UC7  
**Attore Primario**: Autorità / Consumatore  
**Livello**: Obiettivo utente

#### Precondizioni
- La spedizione è stata completata (o è ancora attiva)
- L'ID spedizione è noto (es. da QR-code su confezione)

#### Postcondizioni di Successo
- L'autorità ottiene l'intero storico telemetrico
- I dati sono forniti in formato machine-readable (JSON)
- L'accesso è tracciato nei log per audit

#### Flusso Principale
1. L'autorità riceve il prodotto con etichetta QR-code
2. Scansiona il QR sul packaging
3. Il QR reindirizza a landing page pubblica con tasto "Verifica Dati"
4. Alternativamente, l'autorità può fare richiesta diretta:
   ```bash
   curl https://smarttrack.example.com/api/tracking/shipment/SH-2026-PHARMA-MI
   ```
5. Il server esegue query:
   ```java
   List<TrackData> history = trackingDataRepository
       .findByShipment_ShipmentId("SH-2026-PHARMA-MI");
   ```
6. Risponde con JSON completo (tutti i record storici)
7. L'autorità analizza i dati con tool automatici:
    - **Verifica Catena del Freddo**: Tutti i valori 2-8°C ✓
    - **Continuità Tracking**: Nessun gap >2 minuti ✓
    - **Percorso GPS**: Coerente con route dichiarato ✓
8. Emette certificazione: *"Trasporto conforme"*

#### Flussi Alternativi

**5a - Spedizione Non Trovata**
1. Il repository restituisce lista vuota
2. Risponde: `200 OK` con `[]`
3. L'autorità interpreta come "spedizione mai partita" o ID errato

**4a - Richiesta Malformata**
1. L'ID contiene caratteri speciali non escapati
2. Spring valida il path variable
3. Risponde: `400 Bad Request`

#### Business Rules
- L'endpoint è **pubblico** (no autenticazione) per trasparenza
- I dati storici sono **immutabili** (audit trail)
- Il sistema logga ogni accesso con timestamp e IP sorgente

---

### 6.9 UC8 – Decommissioning Dispositivo

**ID**: UC8  
**Attore Primario**: Admin  
**Livello**: Sottofunzione

#### Precondizioni
- Il dispositivo esiste nel sistema

#### Postcondizioni di Successo
- Il dispositivo è in stato `DECOMMISSIONED`
- Il dispositivo è dissociato da eventuali spedizioni
- L'API-Key diventa invalida

#### Flusso Principale
1. L'admin visualizza la lista dei dispositivi su `/web/devices`
2. Clicca sull'icona **"Dismetti"** accanto al dispositivo target
3. Conferma l'operazione tramite popup JavaScript
4. Il sistema esegue:
   ```java
   device.setShipment(null); // Dissociazione
   device.setStatus(DeviceStatus.DECOMMISSIONED);
   deviceRepository.save(device);
   ```
5. Reindirizza con messaggio: *"Dispositivo {uuid} dismesso con successo"*

#### Flussi Alternativi

**4a – Device in Uso Attivo**
- Il dispositivo è attualmente associato a una spedizione attiva
- Il sistema **forza la dissociazione** 
- Logga warning: *"Device decommissioned while associated to active shipment"*
- L'operazione prosegue normalmente

#### Business Rules
- Un dispositivo `DECOMMISSIONED` non può essere riattivato (one-way transition)
- L'API-Key resta nel database per audit, ma rifiuta nuove richieste
- Storico telemetrico del dispositivo viene preservato

---

### 6.10 UC9 – Completamento Spedizione

**ID**: UC9  
**Attore Primario**: User  
**Livello**: Sottofunzione

#### Precondizioni
- La spedizione è attiva (`active = true`)
- La spedizione ha almeno un dispositivo associato

#### Postcondizioni di Successo
- La spedizione è archiviata (`active = false`)
- I dispositivi associati tornano disponibili (`status = REGISTERED`)
- Lo storico telemetrico è preservato

#### Flusso Principale
1. L'utente clicca **"COMPLETA"** sulla spedizione arrivata
2. Conferma l'operazione tramite popup
3. Il sistema esegue transazione:
   ```java
   @Transactional
   public void completeShipment(String shipmentId) {
       Shipment shipment = findShipment(shipmentId);
       shipment.setActive(false);
       
       for (Device device : shipment.getDevices()) {
           device.setStatus(DeviceStatus.REGISTERED);
           device.setShipment(null);
       }
       
       shipmentRepository.save(shipment);
   }
   ```
4. I dispositivi tornano disponibili per nuove spedizioni

#### Flussi Alternativi

**3a – Spedizione Senza Dispositivi**
- Nessun dispositivo da liberare
- Il sistema completa comunque la spedizione
- Logga info: *"Shipment completed with no devices attached"*

#### Business Logic
- Permette il **riutilizzo dei sensori** (approccio low-cost)
- Mantiene lo **storico dei dati** (immutable audit trail)
- Previene modifiche dopo completamento (spedizioni archiviate sono read-only)

---


## 8. Testing

### 8.1 Strategia di Test

Il progetto adotta una **piramide di test** classica:

```
        ▲
       /│\
      / │ \
     /  │  \     E2E Tests (Selenium)
    /───┼───\    
   /    │    \  
  /─────┼─────\
 /      │      \ Integration Tests (Spring Boot Test)
/───────┼───────\ 
        │         - Coverage: API + Service + Repository
────────┼──────── Unit Tests (JUnit 5 + Mockito)
        │         
```

### 8.2 Metriche di Qualità

| Metrica | Target | Attuale | Tool |
|---------|--------|---------|------|
| **Code Coverage** | ≥70%   | 78%     | JaCoCo |
| **Branch Coverage** | ≥50%   | 59%     | JaCoCo |

### 8.3 Eseguire i Test

#### 8.3.1 Unit Test
```bash
# Eseguire tutti i test
# Eseguire tutti i test
./gradlew test

# Eseguire test di una classe specifica
./gradlew test --tests DeviceServiceTest

# Eseguire con coverage report
./gradlew clean test jacocoTestReport
```






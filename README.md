# Congresso Medicale 2025 - Analytics Dashboard

Progetto sviluppato per la valutazione tecnica Full Stack Developer (data-oriented) per Factory Studios (YEG!).
La soluzione fornisce un'architettura completa per l'ingestione, la normalizzazione e l'analisi dei dati di ingaggio dei partecipanti al "Congresso Medicale 2025".

---

## 1. Architettura & Scelte Tecniche

### Stack Tecnologico

* **Backend:** Java 21, Spring Boot 3.2.3, Spring Data JPA
* **Database:** H2 Database (configurato su file locale con persistenza `./data/congresso_db`)
* **Frontend:** Vanilla JavaScript (ES6+), HTML5, Chart.js, Bootstrap 5
* **Build Tool:** Apache Maven

### Motivazione delle scelte

* **Spring Boot + JPA:** Scelto per garantire un'architettura a layer robusta (Controller-Service-Repository-Model) e una gestione delle transazioni conforme ai principi ACID.
* **H2 Database:** Utilizzato per semplificare l'avvio su qualsiasi macchina senza dipendenze da database esterni. Grazie all'astrazione di Spring Data JPA, la transizione verso un SGBD enterprise (es. PostgreSQL) richiede esclusivamente la modifica del file `application.properties`.
* **Vanilla JS + Chart.js:** Sviluppo frontend privo di framework pesanti per minimizzare l'overhead di build, garantendo reattività, caricamento istantaneo ed elevata leggibilità della logica di consumo delle API REST.

---

## 2. Modello Dati e Normalizzazione

L'dataset di partenza (foglio Excel a 24 colonne) è stato desumilato e normalizzato per evitare una struttura a tabella "piatta" rigida e non scalabile.

```
[Participant] 1 --- * [ParticipantTouchpoint] * --- 1 [TouchpointCatalog]

```

1. **`Participant`**: Custodisce le informazioni anagrafiche del partecipante (Nome, Email, Tipologia Stakeholder, Regione, Canale di Ingaggio).
2. **`TouchpointCatalog`**: Mappa i vari punti di contatto disponibili, catalogandoli in base alla fase del percorso del congresso (`PRE_EVENTO`, `ON_SITE`, `SESSIONE`, `POST_EVENTO`) definita nel dizionario dati.
3. **`ParticipantTouchpoint`**: Tabella relazionale di giunzione che traccia le interazioni effettive (es. presenza in sala, minuti di permanenza, attenzione rilevata, quiz completati).

> **Vantaggio dell'architettura:** Se domani viene aggiunto un nuovo touchpoint, non è necessario alterare lo schema del database (`ALTER TABLE`), ma è sufficiente aggiungere una riga nel catalogo.

---

## 3. Gestione delle Anomalie nei Dati

Durante la fase di ingestione automatica (Seeding/Importazione Excel), sono state gestite le seguenti difformità presenti nel dataset:

* **Celle vuote vs Zero (`NULL` vs `0`):** Le celle vuote nelle metriche di presenza (es. minuti di permanenza o punteggio quiz) non sono state forzate a `0`, ma trattate come `NULL`. Impostarle a zero avrebbe alterato le medie reali per chi era effettivamente presente in sala.
* **Incoerenza nelle intestazioni LinkedIn:** Mappatura dinamica delle colonne durante il parsing per garantire l'uniformità dei dati.
* **Formattazione Date e Booleani:** Normalizzazione delle date dal formato italiano al formato ISO standard e conversione dei valori di ingaggio in tipi booleani coerenti sul DB.

---

## 4. Osservazioni dai Dati & Limiti

### Tre Osservazioni Chiave

1. **Conversion Rate Pre-Evento / On-Site:** Chi risponde agli invii e-mail diretti mostra una percentuale di presenza al simposio e visita allo stand significativamente più alta rispetto a chi arriva da canali social generici.
2. **Fruizione delle Sessioni Riservate:** L'accesso alla sala riservata è fortemente correlato a un tempo di permanenza medio e a un indice di attenzione superiori rispetto alla media generale.
3. **Distribuzione Territoriale:** La risposta dei partecipanti varia sensibilmente in base alla regione, identificando aree geografiche a più alto tasso di ingaggio.

### Limiti dell'Analisi

* **Assenza di Temporalità Dinamica:** I dati rappresentano una fotografia aggregata post-evento; non è possibile analizzare il comportamento degli utenti secondo un flusso temporale in tempo reale (secondo per secondo).
* **Dataset Sintetico:** Le anagrafiche generate limitano la possibilità di fare profilazione avanzata su dati socio-demografici reali.

---

## 5. Guida all'Avvio

### Prerequisiti

* Java JDK 17 o superiore
* Maven 3.x (o wrapper `./mvnw` incluso)

### Esecuzione Passo-Passo

1. **Clona il repository:**
```bash
git clone https://github.com/Johnpeinetti/EventoCongresso2025.git
cd EventoCongresso2025

```


2. **Avvia l'applicazione Spring Boot:**
```bash
./mvnw spring-boot:run

```


*(In alternativa, esegui la classe `CongressoBackendApplication.java` dal tuo IDE)*.
3. **Accedi all'applicazione:**
- **Dashboard Web:** Apri il file `congresso-frontend/index.html` nel browser
- **Backend API:** `http://localhost:8000`
- **Console DB H2:** `http://localhost:8000/h2-console`
* *JDBC URL:* `jdbc:h2:file:./data/congresso_db`
* *Username:* `sa`
* *Password:* *(vuota)*





---

## 6. Sviluppi Futuri (Cosa farei con più tempo)

* **Containerizzazione Docker:** Creazione di un file `docker-compose.yml` multimodulo per il deployment automatizzato del backend e di un database PostgreSQL in container separati.
* **Autenticazione e Sicurezza:** Implementazione di Spring Security con JWT per proteggere gli endpoint REST e la console H2.
* **Test di Copertura:** Aggiunta di test d'integrazione con `@SpringBootTest` e test unitari per i Service con JUnit 5 e Mockito.
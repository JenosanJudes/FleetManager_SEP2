# Fleet Manager – SEP2

Klient/server system til håndtering af firmabiler. Lavet som 2. semesterprojekt på Softwareingeniør, VIA University College.

---

## Krav

Sørg for at have følgende installeret:

- **IntelliJ IDEA** (Community eller Ultimate)
- **Liberica JDK Full 24** – hent fra [bell-sw.com/pages/downloads](https://bell-sw.com/pages/downloads/) (vælg "Full JDK" – den har JavaFX med)
- **PostgreSQL** – hent fra [postgresql.org/download](https://www.postgresql.org/download/) – husk dit password når du installerer!

---

## Trin 1 – Hent projektet fra GitHub

Åbn terminalen og kør:

```
git clone https://github.com/JenosanJudes/FleetManager_SEP2.git
```

Eller hent det som ZIP på GitHub (grøn "Code" knap → "Download ZIP") og pak det ud.

---

## Trin 2 – Opret databasen

Åbn terminalen og kør (skift `postgres` ud med dit eget PostgreSQL brugernavn hvis det er anderledes):

```
psql -U postgres -c "CREATE DATABASE fleet_manager;"
```

Kør derefter schema og testdata (skift stien til hvor du gemte projektet):

```
psql -U postgres -d fleet_manager -f "sti/til/FleetManager_SEP2/database/schema.sql"
psql -U postgres -d fleet_manager -f "sti/til/FleetManager_SEP2/database/seed.sql"
```

**På Mac med PostgreSQL 18:**
```
/Library/PostgreSQL/18/bin/psql -U postgres -d fleet_manager -f "sti/til/FleetManager_SEP2/database/schema.sql"
/Library/PostgreSQL/18/bin/psql -U postgres -d fleet_manager -f "sti/til/FleetManager_SEP2/database/seed.sql"
```

---

## Trin 3 – Opdater databasepassword

Åbn filen `src/server/persistence/DatabaseHelper.java` og skift password til dit eget:

```java
private static final String PASSWORD = "ditPassword"; // skift dette
```

---

## Trin 4 – Åbn projektet i IntelliJ

1. Åbn IntelliJ → **Open** → vælg mappen `FleetManager_SEP2`
2. Sæt SDK til **Liberica JDK Full 24**:
   - **File → Project Structure → Project → SDK**

---

## Trin 5 – Tilføj PostgreSQL driveren

Tjek at `lib/postgresql-42.7.11.jar` er med i projektet.

Hvis den **ikke** er markeret som library:
1. **File → Project Structure → Modules → Dependencies**
2. Klik **+** → **JARs or Directories**
3. Vælg `lib/postgresql-42.7.11.jar`

---

## Trin 6 – Opret Run Configurations

Øverst til højre: klik **"Current File"** dropdown → **Edit Configurations** → **+** → **Application**

**ServerApp:**
- Name: `ServerApp`
- Main class: `server.ServerApp`

**ClientApp:**
- Name: `ClientApp`
- Main class: `client.ClientApp`

---

## Trin 7 – Kør programmet

1. Vælg **ServerApp** i dropdown → klik ▶️ Run
2. Vent til konsollen viser: `Starter Fleet Manager server...`
3. Vælg **ClientApp** → klik ▶️ Run
4. Login-vinduet åbner

---

## Login

| Brugernavn | Password   | Rolle         |
|------------|------------|---------------|
| `admin`    | `admin123` | Administrator |
| `fleet`    | `fleet123` | Fleet Manager |

---

## Projektstruktur

```
src/
├── client/
│   ├── network/       – forbindelse til serveren
│   ├── view/          – FXML controllers
│   ├── viewmodel/     – logik og data til UI
│   └── ClientApp.java – start klienten her
├── server/
│   ├── network/       – server og ClientHandler
│   ├── persistence/   – SQL/database kode
│   ├── service/       – forretningslogik
│   └── ServerApp.java – start serveren her
└── shared/
    ├── domain/        – Vehicle, Employee osv.
    └── protocol/      – Request og Response
database/
├── schema.sql         – opretter tabeller
└── seed.sql           – testdata
```

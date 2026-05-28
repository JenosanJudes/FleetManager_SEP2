# Systemsekvensdiagram – UC01 Log ind

Kopiér koden nedenfor ind på https://mermaid.live

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#3B5998', 'primaryTextColor': '#333', 'primaryBorderColor': '#2C4170', 'lineColor': '#555', 'secondaryColor': '#E8EEF7', 'actorBkg': '#3B5998', 'actorTextColor': '#fff', 'actorBorderColor': '#2C4170', 'signalColor': '#555', 'signalTextColor': '#333', 'noteBkgColor': '#E8EEF7', 'noteTextColor': '#333'}}}%%
sequenceDiagram
    actor FA as Flådeansvarlig
    participant K as Klient (JavaFX)
    participant S as Server
    participant DB as PostgreSQL

    FA->>K: Åbner applikationen
    K-->>FA: Viser login-skærm

    FA->>K: Indtaster brugernavn + password
    FA->>K: Trykker "Log ind"

    K->>S: LOGIN Request {brugernavn, password}

    S->>DB: SELECT * FROM users WHERE username = ?
    DB-->>S: User-række (eller ingen)

    alt Gyldig bruger og korrekt password
        S-->>K: Response.ok(User)
        Note over K: AppContext gemmer User\nstartReaderThread() starter
        K-->>FA: Åbner hovedskærmen (biler/medarbejdere)
    else Forkert brugernavn eller password
        S-->>K: Response.error("Forkert brugernavn eller password")
        K-->>FA: Viser fejlbesked i rødt
    else Server ikke tilgængelig
        K-->>FA: Viser "Kunne ikke forbinde til serveren"
    end
```

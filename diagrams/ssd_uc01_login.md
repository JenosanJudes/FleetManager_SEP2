# Systemsekvensdiagram – UC01 Log ind

Kopiér koden nedenfor ind på https://mermaid.live

```mermaid
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
        K-->>FA: Åbner hovedskærmen (biler/medarbejdere)
    else Forkert brugernavn eller password
        S-->>K: Response.error("Forkert brugernavn eller password")
        K-->>FA: Viser fejlbesked i rødt
    else Server ikke tilgængelig
        K-->>FA: Viser "Kunne ikke forbinde til serveren"
    end
```

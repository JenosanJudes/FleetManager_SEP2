# Systemsekvensdiagram – UC09 Tildel bil til medarbejder

Kopiér koden nedenfor ind på https://mermaid.live

```mermaid
sequenceDiagram
    actor FA as Flådeansvarlig
    participant K as Klient (JavaFX)
    participant S as Server
    participant DB as PostgreSQL

    FA->>K: Vælger en bil i listen
    K->>S: GET_ASSIGNMENT_FOR_VEHICLE Request {vehicleId}
    S->>DB: SELECT ... WHERE vehicle_id = ? AND unassigned_at IS NULL
    DB-->>S: Aktiv tildeling (eller null)
    S-->>K: Response.ok(VehicleAssignment eller null)
    K-->>FA: Viser nuværende tildeling (eller "Ingen tildeling")

    FA->>K: Vælger medarbejder og trykker "Tildel"
    K->>S: ASSIGN_VEHICLE Request {vehicleId, employeeId}

    S->>DB: UPDATE vehicle_assignments\nSET unassigned_at = NOW()\nWHERE vehicle_id = ? AND unassigned_at IS NULL
    Note over DB: Afslutter eventuel eksisterende tildeling

    S->>DB: INSERT INTO vehicle_assignments\n(vehicle_id, employee_id) VALUES (?, ?)
    Note over DB: Partial unique index sikrer\nmaks. én aktiv tildeling pr. bil

    DB-->>S: Ny VehicleAssignment
    S-->>K: Response.ok(VehicleAssignment)
    K-->>FA: Opdaterer visning med ny bilfører
```

package shared.protocol;

// Alle de handlinger klienten kan bede serveren om at udføre
public enum RequestType {
    LOGIN,
    LOGOUT,

    // Biler
    GET_ALL_VEHICLES,
    CREATE_VEHICLE,
    UPDATE_VEHICLE,
    SEARCH_VEHICLES,

    // Medarbejdere
    GET_ALL_EMPLOYEES,
    CREATE_EMPLOYEE,
    UPDATE_EMPLOYEE,

    // Afdelinger
    GET_ALL_DEPARTMENTS,

    // Tildelinger (bil <-> bilfører)
    ASSIGN_VEHICLE,
    UNASSIGN_VEHICLE,
    GET_ASSIGNMENT_FOR_VEHICLE
}

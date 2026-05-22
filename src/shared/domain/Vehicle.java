package shared.domain;

import java.io.Serializable;
import java.time.LocalDate;

// Bil i flåden - kernedomænet i systemet
public record Vehicle(
        int id,
        String licensePlate,       // reg.nr. f.eks. "DT73085"
        String brand,              // mærke f.eks. "Tesla"
        String model,              // model f.eks. "Model Y"
        VehicleType vehicleType,   // Personbil, Varebil, Lastbil
        PlateColor plateColor,     // Hvid eller Gul
        String ownership,          // leasingselskab f.eks. "Nordania"
        LeasingType leasingType,   // Operationel, Finansiel eller Ejet
        String agreementNo,        // aftalenummer
        Integer maxKm,             // max km ifølge aftale
        LocalDate deliveryDate,    // leveringsdato
        LocalDate leasingStart,    // leasingperiode start
        LocalDate leasingEnd,      // leasingperiode slut
        LocalDate expiryDate,      // udløbsdato
        Integer departmentId,      // hvilken afdeling bilen hører til
        String departmentName,     // afdelingens navn (til visning)
        VehicleStatus status       // Aktiv eller Arkiveret
) implements Serializable {

    @Override
    public String toString() {
        return licensePlate + " - " + brand + " " + model;
    }
}

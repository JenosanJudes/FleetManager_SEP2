package shared.domain;

import java.io.Serializable;

// Afdeling - bruges til at gruppere biler og medarbejdere
public record Department(
        int id,
        String name,
        String departmentNo
) implements Serializable {

    @Override
    public String toString() {
        return name;
    }
}

package shared.domain;

import java.io.Serializable;

// Bruger der kan logge ind i systemet
public record User(
        int id,
        String username,
        String password,
        String fullName,
        String role   // "ADMIN" eller "FLEET_MANAGER"
) implements Serializable {
}

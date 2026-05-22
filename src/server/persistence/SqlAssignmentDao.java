package server.persistence;

import shared.domain.VehicleAssignment;

import java.sql.*;
import java.time.LocalDateTime;

public class SqlAssignmentDao implements AssignmentDao {

    // Henter den aktive tildeling for en given bil (unassigned_at er null = stadig aktiv)
    @Override
    public VehicleAssignment getActiveForVehicle(int vehicleId) {
        String sql = """
                SELECT va.*, v.license_plate, e.full_name, e.email, e.phone, d.name AS dept_name
                FROM vehicle_assignments va
                JOIN vehicles v ON va.vehicle_id = v.id
                JOIN employees e ON va.employee_id = e.id
                LEFT JOIN departments d ON e.department_id = d.id
                WHERE va.vehicle_id = ? AND va.unassigned_at IS NULL
                """;

        try (Connection con = DatabaseHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved hentning af tildeling", e);
        }
        return null;
    }

    // Tildeler en bil til en medarbejder
    @Override
    public VehicleAssignment assign(int vehicleId, int employeeId) {
        // Afslut eventuel eksisterende tildeling for bilen
        unassign(vehicleId);

        String sql = """
                INSERT INTO vehicle_assignments (vehicle_id, employee_id)
                VALUES (?, ?)
                RETURNING id
                """;

        try (Connection con = DatabaseHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);
            ps.setInt(2, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return getActiveForVehicle(vehicleId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved tildeling af bil", e);
        }
        return null;
    }

    // Afslutter den aktive tildeling for en bil
    @Override
    public void unassign(int vehicleId) {
        String sql = """
                UPDATE vehicle_assignments
                SET unassigned_at = NOW()
                WHERE vehicle_id = ? AND unassigned_at IS NULL
                """;

        try (Connection con = DatabaseHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved fjernelse af tildeling", e);
        }
    }

    private VehicleAssignment mapRow(ResultSet rs) throws SQLException {
        Timestamp assigned = rs.getTimestamp("assigned_at");
        Timestamp unassigned = rs.getTimestamp("unassigned_at");

        return new VehicleAssignment(
                rs.getInt("id"),
                rs.getInt("vehicle_id"),
                rs.getString("license_plate"),
                rs.getInt("employee_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("dept_name"),
                assigned != null ? assigned.toLocalDateTime() : null,
                unassigned != null ? unassigned.toLocalDateTime() : null
        );
    }
}

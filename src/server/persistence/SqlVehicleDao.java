package server.persistence;

import shared.domain.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Henter og gemmer biler i PostgreSQL via JDBC
public class SqlVehicleDao implements VehicleDao {

    @Override
    public List<Vehicle> getAll() {
        List<Vehicle> list = new ArrayList<>();
        String sql = """
                SELECT v.*, d.name AS dept_name
                FROM vehicles v
                LEFT JOIN departments d ON v.department_id = d.id
                ORDER BY v.license_plate
                """;

        try (Connection con = DatabaseHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved hentning af biler", e);
        }
        return list;
    }

    @Override
    public Vehicle getById(int id) {
        String sql = """
                SELECT v.*, d.name AS dept_name
                FROM vehicles v
                LEFT JOIN departments d ON v.department_id = d.id
                WHERE v.id = ?
                """;

        try (Connection con = DatabaseHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved hentning af bil", e);
        }
        return null;
    }

    @Override
    public List<Vehicle> search(String licensePlate) {
        List<Vehicle> list = new ArrayList<>();
        String sql = """
                SELECT v.*, d.name AS dept_name
                FROM vehicles v
                LEFT JOIN departments d ON v.department_id = d.id
                WHERE LOWER(v.license_plate) LIKE LOWER(?)
                ORDER BY v.license_plate
                """;

        try (Connection con = DatabaseHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + licensePlate + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved søgning", e);
        }
        return list;
    }

    @Override
    public Vehicle create(Vehicle v) {
        String sql = """
                INSERT INTO vehicles
                    (license_plate, brand, model, vehicle_type, plate_color,
                     ownership, leasing_type, agreement_no, max_km,
                     delivery_date, leasing_start, leasing_end, expiry_date,
                     department_id, status)
                VALUES (?,?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)
                RETURNING id
                """;

        try (Connection con = DatabaseHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            setVehicleParams(ps, v);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return getById(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved oprettelse af bil", e);
        }
        return null;
    }

    @Override
    public Vehicle update(Vehicle v) {
        String sql = """
                UPDATE vehicles SET
                    license_plate = ?, brand = ?, model = ?, vehicle_type = ?,
                    plate_color = ?, ownership = ?, leasing_type = ?,
                    agreement_no = ?, max_km = ?, delivery_date = ?,
                    leasing_start = ?, leasing_end = ?, expiry_date = ?,
                    department_id = ?, status = ?
                WHERE id = ?
                """;

        try (Connection con = DatabaseHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            setVehicleParams(ps, v);
            ps.setInt(16, v.id());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved opdatering af bil", e);
        }
        return getById(v.id());
    }

    // Hjælpemetode - sætter alle bil-parametre på et PreparedStatement
    private void setVehicleParams(PreparedStatement ps, Vehicle v) throws SQLException {
        ps.setString(1, v.licensePlate());
        ps.setString(2, v.brand());
        ps.setString(3, v.model());
        ps.setString(4, v.vehicleType().name());
        ps.setString(5, v.plateColor().name());
        ps.setString(6, v.ownership());
        ps.setString(7, v.leasingType() != null ? v.leasingType().name() : null);
        ps.setString(8, v.agreementNo());
        if (v.maxKm() != null) ps.setInt(9, v.maxKm()); else ps.setNull(9, Types.INTEGER);
        if (v.deliveryDate() != null) ps.setDate(10, Date.valueOf(v.deliveryDate())); else ps.setNull(10, Types.DATE);
        if (v.leasingStart() != null) ps.setDate(11, Date.valueOf(v.leasingStart())); else ps.setNull(11, Types.DATE);
        if (v.leasingEnd() != null) ps.setDate(12, Date.valueOf(v.leasingEnd())); else ps.setNull(12, Types.DATE);
        if (v.expiryDate() != null) ps.setDate(13, Date.valueOf(v.expiryDate())); else ps.setNull(13, Types.DATE);
        if (v.departmentId() != null) ps.setInt(14, v.departmentId()); else ps.setNull(14, Types.INTEGER);
        ps.setString(15, v.status().name());
    }

    // Læser én række fra ResultSet og laver den om til et Vehicle-objekt
    private Vehicle mapRow(ResultSet rs) throws SQLException {
        Date deliveryDate = rs.getDate("delivery_date");
        Date leasingStart = rs.getDate("leasing_start");
        Date leasingEnd   = rs.getDate("leasing_end");
        Date expiryDate   = rs.getDate("expiry_date");

        String leasingTypeStr = rs.getString("leasing_type");
        LeasingType leasingType = leasingTypeStr != null ? LeasingType.valueOf(leasingTypeStr) : null;

        int deptId = rs.getInt("department_id");
        Integer departmentId = rs.wasNull() ? null : deptId;

        int maxKmVal = rs.getInt("max_km");
        Integer maxKm = rs.wasNull() ? null : maxKmVal;

        return new Vehicle(
                rs.getInt("id"),
                rs.getString("license_plate"),
                rs.getString("brand"),
                rs.getString("model"),
                VehicleType.valueOf(rs.getString("vehicle_type")),
                PlateColor.valueOf(rs.getString("plate_color")),
                rs.getString("ownership"),
                leasingType,
                rs.getString("agreement_no"),
                maxKm,
                deliveryDate != null ? deliveryDate.toLocalDate() : null,
                leasingStart != null ? leasingStart.toLocalDate() : null,
                leasingEnd   != null ? leasingEnd.toLocalDate()   : null,
                expiryDate   != null ? expiryDate.toLocalDate()   : null,
                departmentId,
                rs.getString("dept_name"),
                VehicleStatus.valueOf(rs.getString("status"))
        );
    }
}

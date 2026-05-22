package server.persistence;

import shared.domain.Employee;
import shared.domain.EmployeeRole;
import shared.domain.EmployeeStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlEmployeeDao implements EmployeeDao {

    @Override
    public List<Employee> getAll() {
        List<Employee> list = new ArrayList<>();
        String sql = """
                SELECT e.*, d.name AS dept_name
                FROM employees e
                LEFT JOIN departments d ON e.department_id = d.id
                ORDER BY e.full_name
                """;

        try (Connection con = DatabaseHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved hentning af medarbejdere", e);
        }
        return list;
    }

    @Override
    public Employee getById(int id) {
        String sql = """
                SELECT e.*, d.name AS dept_name
                FROM employees e
                LEFT JOIN departments d ON e.department_id = d.id
                WHERE e.id = ?
                """;

        try (Connection con = DatabaseHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved hentning af medarbejder", e);
        }
        return null;
    }

    @Override
    public Employee create(Employee e) {
        String sql = """
                INSERT INTO employees
                    (employee_no, full_name, email, phone, department_id, role, status)
                VALUES (?,?,?,?,?,?,?)
                RETURNING id
                """;

        try (Connection con = DatabaseHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.employeeNo());
            ps.setString(2, e.fullName());
            ps.setString(3, e.email());
            ps.setString(4, e.phone());
            ps.setInt(5, e.departmentId());
            ps.setString(6, e.role().name());
            ps.setString(7, e.status().name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return getById(rs.getInt("id"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Fejl ved oprettelse af medarbejder", ex);
        }
        return null;
    }

    @Override
    public Employee update(Employee e) {
        String sql = """
                UPDATE employees SET
                    employee_no = ?, full_name = ?, email = ?, phone = ?,
                    department_id = ?, role = ?, status = ?
                WHERE id = ?
                """;

        try (Connection con = DatabaseHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.employeeNo());
            ps.setString(2, e.fullName());
            ps.setString(3, e.email());
            ps.setString(4, e.phone());
            ps.setInt(5, e.departmentId());
            ps.setString(6, e.role().name());
            ps.setString(7, e.status().name());
            ps.setInt(8, e.id());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Fejl ved opdatering af medarbejder", ex);
        }
        return getById(e.id());
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("id"),
                rs.getString("employee_no"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getInt("department_id"),
                rs.getString("dept_name"),
                EmployeeRole.valueOf(rs.getString("role")),
                EmployeeStatus.valueOf(rs.getString("status"))
        );
    }
}

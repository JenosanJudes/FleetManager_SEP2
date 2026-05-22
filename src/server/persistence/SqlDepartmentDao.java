package server.persistence;

import shared.domain.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlDepartmentDao implements DepartmentDao {

    @Override
    public List<Department> getAll() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY name";

        try (Connection con = DatabaseHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Department(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("department_no")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved hentning af afdelinger", e);
        }
        return list;
    }
}

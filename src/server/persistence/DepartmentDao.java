package server.persistence;

import shared.domain.Department;

import java.util.List;

public interface DepartmentDao {
    List<Department> getAll();
}

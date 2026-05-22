package server.persistence;

import shared.domain.Employee;

import java.util.List;

public interface EmployeeDao {

    List<Employee> getAll();

    Employee getById(int id);

    Employee create(Employee employee);

    Employee update(Employee employee);
}

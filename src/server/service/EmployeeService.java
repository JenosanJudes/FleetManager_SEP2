package server.service;

import server.persistence.EmployeeDao;
import shared.domain.Employee;

import java.util.List;

public class EmployeeService {

    private final EmployeeDao employeeDao;

    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public List<Employee> getAll() {
        return employeeDao.getAll();
    }

    public Employee create(Employee employee) {
        if (employee.fullName() == null || employee.fullName().isBlank()) {
            throw new IllegalArgumentException("Navn må ikke være tomt");
        }
        return employeeDao.create(employee);
    }

    public Employee update(Employee employee) {
        return employeeDao.update(employee);
    }
}

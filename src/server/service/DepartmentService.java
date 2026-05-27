package server.service;

import server.persistence.DepartmentDao;
import shared.domain.Department;

import java.util.List;

// Forretningslogik for afdelinger - sidder imellem netværk og database
public class DepartmentService {

    private final DepartmentDao departmentDao;

    public DepartmentService(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    public List<Department> getAll() {
        return departmentDao.getAll();
    }
}

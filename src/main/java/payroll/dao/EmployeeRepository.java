package payroll.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import payroll.model.domain.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>{

}

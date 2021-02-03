package payroll.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import payroll.dao.EmployeeRepository;
import payroll.exception.EmployeeNotFoundException;
import payroll.model.domain.Employee;
import payroll.assembler.EmployeeModelAssembler;


// tag::constructor[]
@RestController
public class EmployeeController {
	
	@Autowired
	private  EmployeeRepository repository;
	
	@Autowired
	private EmployeeModelAssembler assembler;

	EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {

		this.repository = repository;
		this.assembler = assembler;
	}
	// end::constructor[]

	// Aggregate root

	// tag::get-aggregate-root[]
	//@ResponseBody
	@GetMapping("/employees")
	public CollectionModel<EntityModel<Employee>> all() {

	  List<EntityModel<Employee>> employees = repository.findAll().stream()
	      .map(employee -> EntityModel.of(employee,
	          linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
	          linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
	      .collect(Collectors.toList());

	  return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	}
	// end::get-aggregate-root[]

	// tag::post[]
	@PostMapping("/employees")
	public ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {

		EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));

		return ResponseEntity //
				.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
				.body(entityModel);
	}
	// end::post[]

	// Single item

	// tag::get-single-item[]
//	@CrossOrigin(origins = "http://127.0.0.1/")
	@GetMapping("/employees/{id}")
	public EntityModel<Employee> one(@PathVariable Long id) {

	  Employee employee = repository.findById(id) //
	      .orElseThrow(() -> new EmployeeNotFoundException(id));

	  return EntityModel.of(employee, //
	      linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
	      linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
	}
	// end::get-single-item[]

	// tag::put[]
	@PutMapping("/employees/{id}")
	public ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

		Employee updatedEmployee = repository.findById(id) //
				.map(employee -> {
					employee.setFirstName(newEmployee.getFirstName());
					employee.setLastName(newEmployee.getLastName());
					employee.setRole(newEmployee.getRole());
					return repository.save(employee);
				}) //
				.orElseGet(() -> {
					newEmployee.setId(id);
					return repository.save(newEmployee);
				});

		EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);

		return ResponseEntity //
				.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
				.body(entityModel);
	}
	// end::put[]

	// tag::delete[]
	@DeleteMapping("/employees/{id}")
	public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {

		repository.deleteById(id);

		return ResponseEntity.noContent().build();
	}
	// end::delete[]
}
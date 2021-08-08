package app.cacheservice.controller;

import app.cacheservice.exception.RecordNotFoundException;
import app.cacheservice.model.Employee;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {

    @GetMapping("/employees/{id}")
    public Employee getEmpById(@PathVariable int id) {
        if (id < 100) {
            return new Employee(id, "darsan");
        } else {
            throw new RecordNotFoundException(String.format("Employee %s is not found", id));
        }
    }
}

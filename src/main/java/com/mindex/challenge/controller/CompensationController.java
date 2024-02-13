package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/compensation")
    public Compensation create(@RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for [{}]", compensation.getEmployee());

        // Assuming employee record must exist to have compensation data
        try {
            Employee employee = employeeService.read(compensation.getEmployee().getEmployeeId());
        } catch (RuntimeException e) {
            throw new RuntimeException("Employee with employeeId: " + compensation.getEmployee().getEmployeeId() + " does not exist. Compensation data cannot be created");
        }
        // Assiming salary may not be negative
        if (compensation.getSalary() < 0.0) {
            throw new RuntimeException("Employee with employeeId: " + compensation.getEmployee().getEmployeeId() + " cannot have negative salary");
        }

        return compensationService.create(compensation);
    }

    @GetMapping("/compensation/{id}")
    public Compensation read(@PathVariable String id) {
        LOG.debug("Received compensation read request for employee id [{}]", id);

        Employee employee = employeeService.read(id);
        return compensationService.read(employee);
    }
}

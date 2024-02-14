package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;
    private String compensationIdUrl;
    private String employeeIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{id}";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
    }

    @Test
    public void testCreateRead() {
        // Create checks
        Employee readEmployeeJohn = restTemplate.getForEntity(employeeIdUrl, Employee.class, "16a596ae-edd3-4847-99fe-c4518e82c86f").getBody();
        Compensation testEmployeeCompensation = new Compensation();
        testEmployeeCompensation.setEmployee(readEmployeeJohn);
        testEmployeeCompensation.setSalary(1000000.01);
        testEmployeeCompensation.setEffectiveDate(LocalDateTime.of(2020, 2, 1, 0, 0, 0));

        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testEmployeeCompensation, Compensation.class).getBody();

        assertNotNull(createdCompensation.getEmployee());
        assertCompensationEquivalence(testEmployeeCompensation, createdCompensation);

        // Read checks
        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, createdCompensation.getEmployee().getEmployeeId()).getBody();
        assertNotNull(readCompensation.getEmployee());
        assertCompensationEquivalence(testEmployeeCompensation, readCompensation);

        // Does not create compensation if salary is negative
        testEmployeeCompensation.setSalary(-1.0);
        Compensation createdCompensationNegativeSalary = restTemplate.postForEntity(compensationUrl, testEmployeeCompensation, Compensation.class).getBody();
        assertNull(createdCompensationNegativeSalary.getEmployee());
        testEmployeeCompensation.setSalary(100000.01);

        // Does not create compensation if employee does not exist
        readEmployeeJohn.setEmployeeId("does-not-exist");
        testEmployeeCompensation.setEmployee(readEmployeeJohn);
        Compensation createdCompensationBadId = restTemplate.postForEntity(compensationUrl, testEmployeeCompensation, Compensation.class).getBody();
        assertNull(createdCompensationBadId.getEmployee());
      

    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEmployeeEquivalence(expected.getEmployee(), actual.getEmployee());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }

}

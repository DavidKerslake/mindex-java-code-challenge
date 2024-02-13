package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String employeeIdReportingStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        employeeIdReportingStructureUrl = "http://localhost:" + port + "/employee/reportingstructure/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testCreateReportingStructure() {
        Employee testEmployee0 = new Employee();
        Employee testEmployee01 = new Employee();
        Employee testEmployee02 = new Employee();
        Employee testEmployee021 = new Employee();
        Employee testEmployee022 = new Employee();

        testEmployee0.setFirstName("John");
        testEmployee0.setLastName("Lennon");
        testEmployee0.setDepartment("Engineering");
        testEmployee0.setPosition("Development Manager");
        testEmployee0.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        testEmployee01.setFirstName("Paul");
        testEmployee01.setLastName("McCartney");
        testEmployee01.setDepartment("Engineering");
        testEmployee01.setPosition("Developer I");
        testEmployee01.setEmployeeId("b7839309-3348-463b-a7e3-5de1c168beb3");
        testEmployee02.setFirstName("Ringo");
        testEmployee02.setLastName("Starr");
        testEmployee02.setDepartment("Engineering");
        testEmployee02.setPosition("Developer V");
        testEmployee02.setEmployeeId("03aa1462-ffa9-4978-901b-7c001562cf6f");
        testEmployee021.setFirstName("Pete");
        testEmployee021.setLastName("Best");
        testEmployee021.setDepartment("Engineering");
        testEmployee021.setPosition("Developer II");
        testEmployee021.setEmployeeId("62c1084e-6e34-4630-93fd-9153afb65309");
        testEmployee022.setFirstName("George");
        testEmployee022.setLastName("Harrison");
        testEmployee022.setDepartment("Engineering");
        testEmployee022.setPosition("Developer III");
        testEmployee022.setEmployeeId("c0c2293d-16bd-4603-8e08-638a9d18b22c");

        List<Employee> testEmployee02DirectReports = new ArrayList<>();
        testEmployee02DirectReports.add(testEmployee021);
        testEmployee02DirectReports.add(testEmployee022);
        testEmployee02.setDirectReports(testEmployee02DirectReports);

        List<Employee> testEmployee0DirectReports = new ArrayList<>();
        testEmployee0DirectReports.add(testEmployee01);
        testEmployee0DirectReports.add(testEmployee02);
        testEmployee0.setDirectReports(testEmployee0DirectReports);

        ReportingStructure testEmployee0ReportingStructure = new ReportingStructure(testEmployee0);
        ReportingStructure createdTestEmployee0ReportingStructure = restTemplate.getForEntity(employeeIdReportingStructureUrl, ReportingStructure.class, testEmployee0.getEmployeeId()).getBody();

        assertReportingStructureEquivalence(testEmployee0ReportingStructure, createdTestEmployee0ReportingStructure);

    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private static void assertReportingStructureEquivalence(ReportingStructure expected, ReportingStructure actual) {
        assertEmployeeEquivalence(expected.getEmployee(), actual.getEmployee());
        if (expected.getEmployee().getDirectReports() == null) {
            assertNull(actual.getEmployee().getDirectReports());
        } else {
            assertEquals(expected.getNumberOfReports(), actual.getNumberOfReports());
            assertEquals(expected.getEmployee().getDirectReports().size(), actual.getEmployee().getDirectReports().size());
            expected.getEmployee().getDirectReports().sort(Comparator.comparing(Employee::getEmployeeId));
            actual.getEmployee().getDirectReports().sort(Comparator.comparing(Employee::getEmployeeId));

            for (int i = 0; i < expected.getEmployee().getDirectReports().size(); i++) {
                assertReportingStructureEquivalence(new ReportingStructure(expected.getEmployee().getDirectReports().get(i)), new ReportingStructure(actual.getEmployee().getDirectReports().get(i)));
            }
        }
    }
}

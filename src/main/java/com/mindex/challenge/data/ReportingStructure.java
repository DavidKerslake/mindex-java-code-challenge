package com.mindex.challenge.data;

import java.util.List;

public class ReportingStructure {
    private Employee employee;
    private int numberOfReports;

    public ReportingStructure(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return this.employee;
    }

    // Set number of reports here as it gets calculated from Employee
    public void setEmployee(Employee employee) {
        this.employee = employee;
        List<Employee> directReports = this.employee.getDirectReports();

    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    // Probably don't need this
    public void setNumberOfReports(int numberOfReports) {
        this.numberOfReports = numberOfReports;
    }
}

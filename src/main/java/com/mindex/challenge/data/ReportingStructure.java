package com.mindex.challenge.data;

public class ReportingStructure {
    private Employee employee;
    private int numberOfReports;

    public ReportingStructure() {
    }

    public Employee getEmployee() {
        return this.employee;
    }

    // Set number of reports here as it gets calculated from Employee tree
    public void setEmployee(Employee employee) {
        this.employee = employee;
        this.numberOfReports = this.calculateNumberOfReports(this.employee);
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    // Recursively add direct reports
    private int calculateNumberOfReports(Employee employee) {
        if (employee.getDirectReports() == null) {
            return 0;
        }
        int totalReports = 0;
        for (Employee e : employee.getDirectReports()) {
            totalReports += 1 + this.calculateNumberOfReports(e);
        }
        return totalReports;
    }
}

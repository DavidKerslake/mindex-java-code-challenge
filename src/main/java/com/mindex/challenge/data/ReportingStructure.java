package com.mindex.challenge.data;

public class ReportingStructure {
    private Employee employee;
    private int numberOfReports;

    public ReportingStructure() {
    }

    public ReportingStructure(Employee employee) {
        this.employee = employee;
        this.numberOfReports = this.calculateNumberOfReports();
    }

    public Employee getEmployee() {
        return this.employee;
    }

    // Set number of reports here as it gets calculated from Employee
    public void setEmployee(Employee employee) {
        this.employee = employee;
        this.calculateNumberOfReports();
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    // Probably don't need this
    private int calculateNumberOfReports() {
        if (this.employee.getDirectReports() == null) {
            return 0;
        }
        int totalReports = 0;
        for (Employee e : this.employee.getDirectReports()) {
            ReportingStructure directReport = new ReportingStructure(e);
            totalReports += 1 + directReport.getNumberOfReports();
        }
        return totalReports;
    }
}

package com.example.pulkit.employeeapp.model;

/**
 * Created by RajK on 07-08-2017.
 */

public class CompletedJob {
    private String empId;
    private String dateassigned;
    private String datecompleted;
    private String assignedByUsername,assignedByName;
    private String coordinatorNote,empployeeNote;
    private String empName,empDesignation;

    public CompletedJob(String assignedByName, String assignedByUsername, String coordinatorNote, String dateassigned, String datecompleted, String empDesignation, String empId, String empName, String empployeeNote) {
        this.assignedByName = assignedByName;
        this.assignedByUsername = assignedByUsername;
        this.coordinatorNote = coordinatorNote;
        this.dateassigned = dateassigned;
        this.datecompleted = datecompleted;
        this.empDesignation = empDesignation;
        this.empId = empId;
        this.empName = empName;
        this.empployeeNote = empployeeNote;
    }

    public String getEmpDesignation() {
        return empDesignation;
    }

    public void setEmpDesignation(String empDesignation) {
        this.empDesignation = empDesignation;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getAssignedByUsername() {
        return assignedByUsername;
    }

    public void setAssignedByUsername(String assignedByUsername) {
        this.assignedByUsername = assignedByUsername;
    }

    public String getAssignedByName() {
        return assignedByName;
    }

    public void setAssignedByName(String assignedByName) {
        this.assignedByName = assignedByName;
    }


    public CompletedJob() {
    }

    public String getCoordinatorNote() {
        return coordinatorNote;
    }

    public void setCoordinatorNote(String coordinatorNote) {
        this.coordinatorNote = coordinatorNote;
    }

    public String getEmpployeeNote() {
        return empployeeNote;
    }

    public void setEmpployeeNote(String empployeeNote) {
        this.empployeeNote = empployeeNote;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getDateassigned() {
        return dateassigned;
    }

    public void setDateassigned(String dateassigned) {
        this.dateassigned = dateassigned;
    }

    public String getDatecompleted() {
        return datecompleted;
    }

    public void setDatecompleted(String datecompleted) {
        this.datecompleted = datecompleted;
    }

}

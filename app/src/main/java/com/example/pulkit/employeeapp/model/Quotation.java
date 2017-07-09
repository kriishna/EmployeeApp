package com.example.pulkit.employeeapp.model;

/**
 * Created by RajK on 03-06-2017.
 */

public class Quotation {
    private String approvedByCust;

    public Quotation() {

    }

    public Quotation(String approvedByCust) {
        this.approvedByCust = approvedByCust;
    }

    public String getApprovedByCust() {
        return approvedByCust;
    }

    public void setApprovedByCust(String approvedByCust) {
        this.approvedByCust = approvedByCust;
    }


}

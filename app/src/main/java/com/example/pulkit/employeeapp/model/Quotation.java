package com.example.pulkit.employeeapp.model;

public class Quotation {
    private String approvedByCust;
    private String url;

    public Quotation() {

    }

    public Quotation(String approvedByCust, String url) {
        this.approvedByCust = approvedByCust;
        this.url = url;
    }

    public String getApprovedByCust() {
        return approvedByCust;
    }

    public void setApprovedByCust(String approvedByCust) {
        this.approvedByCust = approvedByCust;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

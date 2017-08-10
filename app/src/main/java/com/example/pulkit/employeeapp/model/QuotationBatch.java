package com.example.pulkit.employeeapp.model;

public class QuotationBatch {

    String endDate;
    String startDate;
    String coordnote;
    String id;
    String custName;
    private int color = -1;

    public QuotationBatch() {
    }

    public QuotationBatch(String id, String coordnote, String startDate, String endDate, String custName, int color) {
        this.endDate = endDate;
        this.id = id;
        this.coordnote = coordnote;
        this.startDate = startDate;
        this.custName = custName;
        this.color = color;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return coordnote;
    }

    public void setNote(String coordnote) {
        this.coordnote = coordnote;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

package com.example.pulkit.employeeapp.model;

public class QuotationBatch {

    String endDate, startDate, note, id;
    private int color = -1;

    public QuotationBatch() {
    }

    public QuotationBatch(String id, String note, String startDate, String endDate, int color) {
        this.endDate = endDate;
        this.id = id;
        this.note = note;
        this.startDate = startDate;
        this.color = color;
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
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

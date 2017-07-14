package com.example.pulkit.employeeapp.model;

/**
 * Created by RajK on 10-07-2017.
 */

public class NameAndStatus {
    private String id;
    private String name;
    private String online;

    public NameAndStatus(String id, String name, String online) {
        this.id = id;
        this.name = name;
        this.online = online;
    }

    public NameAndStatus() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}

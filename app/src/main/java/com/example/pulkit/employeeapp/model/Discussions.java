package com.example.pulkit.employeeapp.model;

/**
 * Created by SoumyaAgarwal on 5/21/2017.
 */

public class Discussions {
    private String place_id,name;
    private int color = -1;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Discussions() {
    }

    public Discussions(String place_id, String name) {
        this.place_id = place_id;
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }
}

package com.example.pulkit.employeeapp.model;


public class Employee {

    public Employee(){

    }

    private int color=-1;
    private String name;
    private String phone_num;
    private String address;
    private String designation;
    private String username,password;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Employee(int Color, String name, String phone_num, String address, String designation, String username, String password) {
        this.color = Color;
        this.name = name;
        this.phone_num = phone_num;
        this.address = address;
        this.designation = designation;
        this.username = username;
        this.password = password;
        }
}

package com.example.pulkit.employeeapp.model;

/**
 * Created by SoumyaAgarwal on 7/13/2017.
 */

public class ChatListModel {
    private String name,userkey,dbTableKey;
    private int color=-1;
    private Long lastMsg;

    public ChatListModel() {
    }

    public ChatListModel(String name, String userkey, String dbTableKey, int color, Long lastMsg) {
        this.name = name;
        this.userkey = userkey;
        this.dbTableKey = dbTableKey;
        this.color = color;
        this.lastMsg = lastMsg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public String getDbTableKey() {
        return dbTableKey;
    }

    public void setDbTableKey(String dbTableKey) {
        this.dbTableKey = dbTableKey;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Long getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(Long lastMsg) {
        this.lastMsg = lastMsg;
    }
}

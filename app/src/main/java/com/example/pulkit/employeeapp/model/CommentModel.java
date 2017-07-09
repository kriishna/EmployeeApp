package com.example.pulkit.employeeapp.model;

/**
 * Created by RajK on 16-05-2017.
 */

public class CommentModel {
    private String commentString,sender,timestamp,type,id,status,imgurl,videourl;

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getCommentString() {
        return commentString;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public CommentModel() {

    }

    public CommentModel(String commentString, String sender, String timestamp, String type, String id, String status) {
        this.commentString = commentString;
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
        this.id = id;
        this.status = status;
    }

    public CommentModel(String commentString, String sender, String timestamp, String type, String id, String status, String imgurl) {
        this.commentString = commentString;
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
        this.id = id;
        this.status = status;
        this.imgurl = imgurl;
    }

    public CommentModel(String commentString, String sender, String timestamp, String type, String id, String status, String imgurl, String videourl) {
        this.commentString = commentString;
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
        this.id = id;
        this.status = status;
        this.imgurl = imgurl;
        this.videourl = videourl;
    }

    public CommentModel(String commentString, String sender, String timestamp) {
        this.commentString = commentString;
        this.sender = sender;
        this.timestamp = timestamp;
    }
}

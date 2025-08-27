package com.reday.collabportal;

import java.util.Date;
import java.util.List;

public class Post {
    private String postId;
    private String userId;
    private String userName;
    private String content;
    private Date timestamp;
    private int loves;
    private List<String> lovedBy;

    // Required empty constructor for Firestore
    public Post() {
    }

    public Post(String userId, String userName, String content, Date timestamp, int loves, List<String> lovedBy) {
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.timestamp = timestamp;
        this.loves = loves;
        this.lovedBy = lovedBy;
    }

    // Getters and setters
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getLoves() {
        return loves;
    }

    public void setLoves(int loves) {
        this.loves = loves;
    }

    public List<String> getLovedBy() {
        return lovedBy;
    }

    public void setLovedBy(List<String> lovedBy) {
        this.lovedBy = lovedBy;
    }
}
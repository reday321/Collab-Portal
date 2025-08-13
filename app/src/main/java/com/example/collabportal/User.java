package com.example.collabportal.models;

import com.google.firebase.Timestamp;

public class User {
    private String uid, name, studentId, phone, email, department;
    private Timestamp createdAt;

    public User() {}

    public User(String uid, String name, String studentId, String phone,
                String email, String department, Timestamp createdAt) {
        this.uid = uid; this.name = name; this.studentId = studentId;
        this.phone = phone; this.email = email; this.department = department;
        this.createdAt = createdAt;
    }

    // getters & setters for all fields (generate in IDE)
}

package com.reday.collabportal;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class CollabPortalApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
    }
}
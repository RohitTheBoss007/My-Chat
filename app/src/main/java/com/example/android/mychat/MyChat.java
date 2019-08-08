package com.example.android.mychat;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyChat extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

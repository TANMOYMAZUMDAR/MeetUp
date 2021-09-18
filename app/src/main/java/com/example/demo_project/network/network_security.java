package com.example.demo_project.network;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class network_security extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

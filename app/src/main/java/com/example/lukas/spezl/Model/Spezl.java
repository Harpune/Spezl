package com.example.lukas.spezl.Model;

import android.app.Application;
import com.firebase.client.Firebase;

public class Spezl extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}

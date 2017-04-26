package com.example.lukas.spezl.Model;

import android.app.Application;
import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;

public class Spezl extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

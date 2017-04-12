package com.example.lukas.spezl.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.lukas.spezl.R;

public class DecisionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision);
    }

    public void createEvent(View view) {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    public void joinEvent(View view) {
        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }
}

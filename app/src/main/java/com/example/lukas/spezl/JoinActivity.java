package com.example.lukas.spezl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class JoinActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
    }

    public void chooseDate(View view) {
        Intent intent = new Intent(this, DateActivity.class);
        startActivity(intent);
    }
}

package com.example.lukas.spezl.view;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lukas.spezl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {
    private final String TAG = "1";

    private EditText oldPassword, firstPassword, newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Setup toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Passwort ändern");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        oldPassword = (EditText) findViewById(R.id.input_old_password);
        firstPassword = (EditText) findViewById(R.id.input_password);
        newPassword = (EditText) findViewById(R.id.input_check_password);
    }

    public void changePassword(View view) {
        String oldString = oldPassword.getText().toString().trim();
        String firstString = firstPassword.getText().toString().trim();
        final String newString = newPassword.getText().toString().trim();

        if(!firstString.equals(newString)){
            Toast.makeText(this, "Deine Passwörter stimmen nicht überein", Toast.LENGTH_SHORT).show();
        } else if (oldString.equals(newString)){
            Toast.makeText(this, "Dein neues Passwort ist gleich wie dein altes", Toast.LENGTH_SHORT).show();
        } else {
            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldString);

            firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        firebaseUser.updatePassword(newString).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Passwort wurde geändert", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Password updated");
                                    Intent intent = new Intent(ResetPasswordActivity.this, DecisionActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Neues Passwort ist zu kurz (min. 6 Zeichen)", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Error password not updated");
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Dein altes Passwort ist falsch", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error auth failed");
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

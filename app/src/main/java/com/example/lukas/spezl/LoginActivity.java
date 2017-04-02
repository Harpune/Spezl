package com.example.lukas.spezl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends Activity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmailText, mPasswordText;
    private TextInputLayout mEmailLayout, mPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailText = (EditText) findViewById(R.id.input_email);
        mPasswordText = (EditText) findViewById(R.id.input_password);

        mEmailLayout = (TextInputLayout) findViewById(R.id.input_layout_email);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.input_layout_password);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("AUTH", "Signed in... UserID: + " + user.getUid());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("AUTH", "Signed out...");
                }
            }
        };
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        String email = mEmailText.getText().toString().trim();
        String password = mPasswordText.getText().toString().trim();

        mEmailLayout.setErrorEnabled(false);
        mPasswordLayout.setErrorEnabled(false);

        if (email.matches("")) {
            mEmailLayout.setError("Bitte geb deine E-Mail an!");
            mEmailText.requestFocus();
            return;
        }

        if (password.matches("")) {
            mPasswordLayout.setError("Geb ein Passwort ein!");
            mPasswordText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            mPasswordLayout.setError("Das Passwort ist zu kurz.");
            mPasswordText.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Anmeldung ist fehlgeschlagen!", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, DecisionActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}

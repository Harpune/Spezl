package com.example.lukas.spezl.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.lukas.spezl.R;
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

        //Check if a user is logged in
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("AUTH", "Signed in... UserID: + " + user.getUid());
                    Intent intent = new Intent(getApplicationContext(), DecisionActivity.class);
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
            mEmailLayout.setError("Bitte gib deine E-Mail an!");
            mEmailText.requestFocus();
            return;
        }

        if (password.matches("")) {
            mPasswordLayout.setError("Gib ein Passwort ein!");
            mPasswordText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            mPasswordLayout.setError("Das Passwort ist zu kurz.");
            mPasswordText.requestFocus();
            return;
        }

        final RelativeLayout loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        loadingPanel.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            loadingPanel.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "E-Mail und Passwort stimmen nicht überein!", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingPanel.setVisibility(View.GONE);

                            if (!isEmailVerified()) {
                                //restart this activity
                                Toast.makeText(getApplicationContext(), "Bestätige zuerst deine Email!", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }
                    }
                });
    }

    private boolean isEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d("IS_EMAIL_VERIFIED", "User is null");
            return false;
        }
        Log.d("IS_EMAIL_VERIFIED", "" + user.isEmailVerified());
        if (user.isEmailVerified()) {
            // user is verified, so you can finish this activity or send user to activity which you want.
            Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Please verify your Account", Toast.LENGTH_SHORT).show();
            return false;
        }
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

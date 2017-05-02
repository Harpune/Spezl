package com.example.lukas.spezl.view;

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
    // Firebase Instance
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Views globally needed.
    private EditText mEmailText, mPasswordText;
    private TextInputLayout mEmailLayout, mPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find Views in Layout
        mEmailText = (EditText) findViewById(R.id.input_email);
        mPasswordText = (EditText) findViewById(R.id.input_password);

        mEmailLayout = (TextInputLayout) findViewById(R.id.input_layout_email);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.input_layout_password);

        // get Firebase Instance.
        mAuth = FirebaseAuth.getInstance();

        //Check if a user is logged in
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) { // is logged in
                        Log.d("AUTH", "Signed in... UserID: + " + user.getUid());
                        Intent intent = new Intent(getApplicationContext(), DecisionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d("AUTH", "User has to verify its email");
                    }

                } else {
                    Log.d("AUTH", "Signed out...");
                }
            }
        };

    }

    /**
     * Triggered when view new registration should be done.
     * @param view clicked TextView
     */
    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Check all inputs for validation.
     * @param view clicked Button
     */
    public void login(View view) {
        // Get typed email and password.
        String email = mEmailText.getText().toString().trim();
        String password = mPasswordText.getText().toString().trim();

        // Remove error-note.
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

        // Start loading panel.
        final RelativeLayout loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        loadingPanel.setVisibility(View.VISIBLE);

        // Sign in with Firebase Instance.
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            loadingPanel.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "E-Mail und Passwort stimmen nicht überein!", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingPanel.setVisibility(View.GONE);

                            //Check if user verified his email.
                            if (isEmailVerified()) {
                                Intent intent = new Intent(LoginActivity.this, DecisionActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }

                        }
                    }
                });
    }

    /**
     * Email-Verification.
     * @return true if user verified his email.
     */
    private boolean isEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d("IS_EMAIL_VERIFIED", "User is null");
            return false;
        }

        if (user.isEmailVerified()) {
            // user is verified, so you can finish this activity or send user to activity which you want.
            Toast.makeText(LoginActivity.this, "Auf die Spezl! Fertig! Los!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Bestätige bitte zuerst deine E-Mail Adresse", Toast.LENGTH_SHORT).show();
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

    /**
     * Reset the password. Email is send to given address.
     * Firebase generates link to reset the password.
     * @param view clicked TextView
     */
    public void resetPassword(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = mEmailText.getText().toString().trim();

        if (email.matches("")) {
            mEmailLayout.setError("Bitte gib zuerst hier deine E-Mail an");
            mEmailText.requestFocus();
            return;
        }

        // Send reset-password mail.
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("RESET_PASSWORD", "Email sent.");
                            Toast.makeText(LoginActivity.this, "Überpfüfe deinen Email-Eingang", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

package com.example.lukas.spezl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends Activity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mNameText, mTownText, mEmailText, mAgeText, mPasswordText, mPasswordCheckText;
    private TextInputLayout mNameLayout, mTownLayout, mEmailLayout, mAgeLayout, mPasswordLayout,
            mPasswordCheckLayout;

    private String name, town, email, age, password, passwordCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mNameText = (EditText) findViewById(R.id.input_name);
        mTownText = (EditText) findViewById(R.id.input_town);
        mEmailText = (EditText) findViewById(R.id.input_email);
        mAgeText = (EditText) findViewById(R.id.input_age);
        mPasswordText = (EditText) findViewById(R.id.input_password);
        mPasswordCheckText = (EditText) findViewById(R.id.input_check_password);

        mNameLayout = (TextInputLayout) findViewById(R.id.input_layout_name);
        mTownLayout = (TextInputLayout) findViewById(R.id.input_layout_town);
        mEmailLayout = (TextInputLayout) findViewById(R.id.input_layout_email);
        mAgeLayout = (TextInputLayout) findViewById(R.id.input_layout_age);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.input_layout_password);
        mPasswordCheckLayout = (TextInputLayout) findViewById(R.id.input_layout_check_password);
    }

    public void register(View view) {
        name = mNameText.getText().toString().trim();
        town = mTownText.getText().toString().trim();
        email = mEmailText.getText().toString().trim();
        age = mAgeText.getText().toString().trim();
        password = mPasswordText.getText().toString().trim();
        passwordCheck = mPasswordCheckText.getText().toString().trim();

        mNameLayout.setErrorEnabled(false);
        mTownLayout.setErrorEnabled(false);
        mEmailLayout.setErrorEnabled(false);
        mAgeLayout.setErrorEnabled(false);
        mPasswordLayout.setErrorEnabled(false);
        mPasswordCheckLayout.setErrorEnabled(false);

        if (name.matches("")) {
            mNameLayout.setError("Gib bitte deinen Namen an!");
            mNameText.requestFocus();
            return;
        }

        if (town.matches("")) {
            mTownLayout.setError("Wo wohnst du denn?");
            mTownText.requestFocus();
            return;
        }

        if (email.matches("")) {
            mEmailLayout.setError("Wir wollen deine Daten!");
            mEmailText.requestFocus();
            return;
        }

        if (age.matches("")) {
            mAgeLayout.setError("So jung siehst du nicht aus...");
            mAgeText.requestFocus();
            return;
        }

        if (password.matches("")) {
            mPasswordLayout.setError("Bitte gib wenigstens ein Passwort ein!");
            mPasswordText.requestFocus();
            return;
        }

        if(password.length() < 6){
            mPasswordLayout.setError("Das Passwort muss länger als 6 Buchstaben sein");
            mPasswordText.requestFocus();
            return;
        }

        if (!password.matches(passwordCheck)) {
            mPasswordLayout.setError("Deine Passwörter stimmen nicht überein!");
            mPasswordCheckLayout.setError("Der über mir hat recht.");
            mPasswordText.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                mEmailLayout.setError(getString(R.string.auth_user_exists));
                                mEmailText.requestFocus();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                mPasswordLayout.setError(((FirebaseAuthWeakPasswordException) task.getException()).getReason());
                                mPasswordText.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                mEmailLayout.setError(getString(R.string.auth_invalid_email));
                                mEmailText.requestFocus();
                            } catch (FirebaseAuthInvalidUserException e) {
                                Toast.makeText(getApplicationContext(), getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                            } catch (Exception e){
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Intent intent = new Intent(getApplicationContext(), DecisionActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}

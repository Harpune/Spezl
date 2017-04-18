package com.example.lukas.spezl.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.lukas.spezl.Model.User;
import com.example.lukas.spezl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends Activity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseUser fireUser;

    private EditText mNameText, mTownText, mEmailText, mAgeText, mPasswordText, mPasswordCheckText;
    private TextInputLayout mNameLayout, mTownLayout, mEmailLayout, mAgeLayout, mPasswordLayout,
            mPasswordCheckLayout;
    private RadioGroup mRadioGroup;

    private String name, town, email, age, password, passwordCheck;
    private Boolean sex = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mNameText = (EditText) findViewById(R.id.input_name);
        mTownText = (EditText) findViewById(R.id.input_town);
        mEmailText = (EditText) findViewById(R.id.input_email);
        mAgeText = (EditText) findViewById(R.id.input_age);
        mPasswordText = (EditText) findViewById(R.id.input_password);
        mPasswordCheckText = (EditText) findViewById(R.id.input_check_password);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_sex);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                switch (id) {
                    case R.id.radio_sex_female:
                        sex = false;
                        break;
                    case R.id.radio_sex_male:
                        sex = true;
                        break;
                }
            }
        });

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

        if (sex == null) {
            Toast.makeText(this, "Männlein oder Weiblein?", Toast.LENGTH_SHORT).show();
            mRadioGroup.requestFocus();
            return;
        }

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

        if (password.length() < 6) {
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

        final RelativeLayout loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        loadingPanel.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            loadingPanel.setVisibility(View.GONE);

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
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            loadingPanel.setVisibility(View.GONE);
                            sendVerificationEmail();
                            fireUser = task.getResult().getUser();

                            User user = new User(fireUser.getUid(), name, sex, town, email, Double.parseDouble(age), "");
                            mDatabaseRef = mDatabase.getReference();
                            mDatabaseRef.child("users").child(fireUser.getUid()).setValue(user);

                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            FirebaseAuth.getInstance().signOut();
                            startActivity(intent);
                            finish();

                        }
                    }
                });
    }

    private void sendVerificationEmail() {
        FirebaseUser userFire = FirebaseAuth.getInstance().getCurrentUser();

        userFire.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "E-Mail send", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(getApplicationContext(), "E-Mail DIDNOT send", Toast.LENGTH_SHORT).show();
                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });


    }
}

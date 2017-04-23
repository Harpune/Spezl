package com.example.lukas.spezl.View;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends Activity {
    private FirebaseAuth mAuth;

    private Calendar mCalendar = Calendar.getInstance();

    private EditText mFirstNameText, mLastNameText, mEmailText, mAgeText, mPasswordText, mPasswordCheckText;
    private TextInputLayout mFirstNameLayout, mLastNameLayout, mEmailLayout, mAgeLayout, mPasswordLayout,
            mPasswordCheckLayout;
    private RadioGroup mRadioGroup;

    private String firstName, lastName, email, age, password, passwordCheck;
    private Boolean sex = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO name zu firebase user hinzufügen

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mFirstNameText = (EditText) findViewById(R.id.input_first_name);
        mLastNameText = (EditText) findViewById(R.id.input_last_name);
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

        mFirstNameLayout = (TextInputLayout) findViewById(R.id.input_layout_first_name);
        mLastNameLayout = (TextInputLayout) findViewById(R.id.input_layout_last_name);
        mEmailLayout = (TextInputLayout) findViewById(R.id.input_layout_email);
        mAgeLayout = (TextInputLayout) findViewById(R.id.input_layout_age);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.input_layout_password);
        mPasswordCheckLayout = (TextInputLayout) findViewById(R.id.input_layout_check_password);

        getDateFromUser();
    }

    public void register(View view) {
        firstName = mFirstNameText.getText().toString().trim();
        lastName = mLastNameText.getText().toString().trim();
        email = mEmailText.getText().toString().trim();
        age = mAgeText.getText().toString().trim();
        password = mPasswordText.getText().toString().trim();
        passwordCheck = mPasswordCheckText.getText().toString().trim();

        mFirstNameLayout.setErrorEnabled(false);
        mLastNameLayout.setErrorEnabled(false);
        mEmailLayout.setErrorEnabled(false);
        mAgeLayout.setErrorEnabled(false);
        mPasswordLayout.setErrorEnabled(false);
        mPasswordCheckLayout.setErrorEnabled(false);



        if (firstName.matches("")) {
            mFirstNameLayout.setError("Gib bitte deinen Vornamen an!");
            mFirstNameText.requestFocus();
            return;
        }

        if (lastName.matches("")) {
            mLastNameLayout.setError("Wie lautet dein Nachname?");
            mLastNameText.requestFocus();
            return;
        }

        if (sex == null) {
            Toast.makeText(this, "Männlein oder Weiblein?", Toast.LENGTH_SHORT).show();
            mRadioGroup.requestFocus();
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

                            FirebaseUser fireUser = task.getResult().getUser();

                            // Build the user-object.
                            User user = new User();
                            user.setUserId(fireUser.getUid());
                            user.setUsername(firstName+ " " + lastName);
                            user.setSex(sex);
                            user.setEmail(email);
                            user.setAge(mCalendar.getTime());

                            Log.d("NEW_USER", user.toString());
                            Log.d("NEW_USER", fireUser.getEmail());

                            // Create database connection and reference.
                            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference mDatabaseRef = mDatabase.getReference("users");

                            // Create user.
                            String userId = mDatabaseRef.push().getKey();

                            mDatabaseRef.child(userId).setValue(user);

                            //start intent and sign out.
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

        assert userFire != null;
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

    /**
     * Start Date Dialog to get the Date of the event.
     */
    private void getDateFromUser() {
        //Create the DatePickerDialog
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };

        // Show the DatePickerDialog
        mAgeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, date,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });
    }

    /**
     * Update the editTextField with chosen date.
     */
    private void updateDate() {
        String dateFormat = "dd.MM.YYYY";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

        mAgeText.setText(sdf.format(mCalendar.getTime()));
    }
}

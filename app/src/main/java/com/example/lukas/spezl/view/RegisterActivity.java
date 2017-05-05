package com.example.lukas.spezl.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.lukas.spezl.R;
import com.example.lukas.spezl.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    // TAG for AGB
    private final int TAG_AGB = 1;

    // Firebase Authentication.
    private FirebaseAuth mAuth;

    // Calender for the Date.
    private Calendar mCalendar = Calendar.getInstance();

    // Views of the Layout.
    private EditText mFirstNameText, mLastNameText, mEmailText, mAgeText, mPasswordText, mPasswordCheckText;
    private TextInputLayout mFirstNameLayout, mLastNameLayout, mEmailLayout, mAgeLayout, mPasswordLayout,
            mPasswordCheckLayout;
    private RadioGroup mRadioGroup;
    private RelativeLayout loadingPanel;
    private CheckBox checkBox;

    // Input of the user.
    private String firstName, lastName, email, age, password, passwordCheck;
    private Boolean sex = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Instanciate the FirebaseAuthentication.
        mAuth = FirebaseAuth.getInstance();

        // Implement toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.text_register);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        // Find the views.
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

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        checkBox = (CheckBox) findViewById(R.id.agb_checkBox);

        // Get date from user.
        getDateFromUser();
    }

    /**
     * Check every input of the user.
     *
     * @param view register-button clicked.
     */
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

        if (!checkBox.isChecked()) {
            Toast.makeText(RegisterActivity.this, "Bitte bestätige die AGB", Toast.LENGTH_LONG).show();
            return;
        }

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
            mEmailLayout.setError("Wie wär’s mit einer Mailadresse?");
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

        loadingPanel.setVisibility(View.VISIBLE);

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
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            loadingPanel.setVisibility(View.GONE);

                        } else {
                            FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (fireUser == null) {
                                Toast.makeText(getApplicationContext(), "Benutzer konnte nicht angelegt werden", Toast.LENGTH_SHORT).show();
                            } else {
                                // Send verification mail
                                sendVerificationEmail(fireUser);

                                // Create a new User!
                                createNewUser(fireUser);
                            }
                            loadingPanel.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void createNewUser(FirebaseUser fireUser) {
        // Build the user-object.
        User user = new User();
        user.setUserId(fireUser.getUid());
        user.setUsername(firstName + " " + lastName);
        user.setSex(sex);
        user.setEmail(email);
        user.setAge(mCalendar.getTime());
        user.setImageUri("");

        Log.d("NEW_USER", user.toString());
        Log.d("NEW_USER", fireUser.getEmail());

        // Set DisplayName to FirebaseUser for Display.
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(firstName + " " + lastName).build();
        fireUser.updateProfile(profileUpdates);

        // Create database connection and reference.
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.child("users")
                .child(fireUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            FirebaseAuth.getInstance().signOut();
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setIcon(R.drawable.pic_owl_active)
                                    .setTitle("Geschafft!")
                                    .setMessage("Bitte bestätige deine E-Mail")
                                    .setPositiveButton("Los Spezln", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                                            startActivity(intent);
                                            finish();
                                        }
                                    }).show();

                        } else {
                            loadingPanel.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Der Benutzer konnte nicht angelegt werden", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    /**
     * Send verification E-Mail to the given user.
     */
    private void sendVerificationEmail(FirebaseUser fireUser) {
        assert fireUser != null;
        fireUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("EMAIL_VERIFICATION", "E-Mail send");
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
        final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
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
                //Check for different build, because it crashes.
                int mYear = mCalendar.get(Calendar.YEAR);
                int mMonth = mCalendar.get(Calendar.MONTH);
                int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(RegisterActivity.this, R.style.TimePicker, mDateSetListener, mYear, mMonth, mDay).show();
            }
        });
    }

    /**
     * Update the editTextField with chosen date.
     */
    private void updateDate() {
        DateFormat df = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        mAgeText.setText(df.format(mCalendar.getTime()));
    }

    public void showAGB(View view) {
        Intent intent = new Intent(RegisterActivity.this, AGBActivity.class);
        intent.putExtra("SETUP_TOOLBAR", false);
        startActivityForResult(intent, TAG_AGB);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAG_AGB) {
            if (resultCode == RESULT_OK) {
                checkBox.setChecked(true);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

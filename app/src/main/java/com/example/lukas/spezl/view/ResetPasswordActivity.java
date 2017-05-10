package com.example.lukas.spezl.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lukas.spezl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResetPasswordActivity extends AppCompatActivity {
    private final String TAG = "1";

    private EditText mOldPasswordText, mFirstPasswordText, mNewPasswordText, mFirstNameText, mLastNameText;

    private TextInputLayout mOldPasswordLayout, mFirstPasswordLayout, mNewPasswordLayout, mFirstNameLayout, mLastNameLayout;

    private Button changeProfileButton;

    private TextView mUserEmail;

    private boolean profileChangeable = false;

    private RelativeLayout loadingPanel;

    private Snackbar snackbar;

    private DatabaseReference mRefUser;
    private FirebaseUser fireUser;

    private String password = "";
    private String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Setup toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profil");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);

        mUserEmail = (TextView) findViewById(R.id.user_email);

        mOldPasswordText = (EditText) findViewById(R.id.input_old_password);
        mFirstPasswordText = (EditText) findViewById(R.id.input_password);
        mNewPasswordText = (EditText) findViewById(R.id.input_check_password);

        mOldPasswordLayout = (TextInputLayout) findViewById(R.id.input_layout_old_password);
        mFirstPasswordLayout = (TextInputLayout) findViewById(R.id.input_layout_password);
        mNewPasswordLayout = (TextInputLayout) findViewById(R.id.input_layout_check_password);

        mFirstNameText = (EditText) findViewById(R.id.input_first_name);
        mLastNameText = (EditText) findViewById(R.id.input_last_name);

        mFirstNameLayout = (TextInputLayout) findViewById(R.id.input_layout_first_name);
        mLastNameLayout = (TextInputLayout) findViewById(R.id.input_layout_last_name);

        changeProfileButton = (Button) findViewById(R.id.change_profile_button);
        changeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!profileChangeable) {
                    snackbar = Snackbar.make(view, "Profil kann bearbeitet werden", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Rückgängig", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            enableProfileViews(false);
                            getUserData();
                            changeProfileButton.setText(getResources().getString(R.string.text_change_profile));
                        }
                    });
                    snackbar.show();

                    enableProfileViews(true);
                    changeProfileButton.setText(getResources().getString(R.string.text_accept));

                } else {

                    final EditText editText = new EditText(ResetPasswordActivity.this);
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


                    new AlertDialog.Builder(ResetPasswordActivity.this)
                            .setIcon(R.drawable.pic_owl_icon)
                            .setTitle("Bist du dir sicher?")
                            .setView(editText)
                            .setMessage("Danach musst du dich erneut einloggen.")
                            .setCancelable(false)
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    snackbar.dismiss();
                                    changeProfile(editText.getText().toString());
                                }
                            })
                            .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    enableProfileViews(false);
                                    getUserData();
                                    changeProfileButton.setText(getResources().getString(R.string.text_change_profile));
                                }
                            })
                            .show();
                }

            }
        });

        getUserData();
    }


    public void changeProfile(String password) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        final String[] firstLast = firebaseUser.getDisplayName().split("\\s+");

        final String firstName = mFirstNameText.getText().toString().trim();
        final String lastName = mLastNameText.getText().toString().trim();

        if (password.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Gib bitte dein Passwort ein", Toast.LENGTH_SHORT).show();
            return;
        }

        if (firstName.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Gib bitte deinen Vornamen ein", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lastName.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Gib bitte deinen Vornamen ein", Toast.LENGTH_SHORT).show();
            return;
        }

        if (firstName.equals(firstLast[0]) && lastName.equals(firstLast[1])) {
            Toast.makeText(ResetPasswordActivity.this, "Du hast keine Angabe geändert.", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingPanel.setVisibility(View.VISIBLE);

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Log.d("UPDATE_PROFILE", "reauthenticate: success");

                    // Update DisplayName to FirebaseUser for Display.
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(firstName + " " + lastName).build();
                    firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("UPDATE_PROFILE", "updateProfile: success");

                                DatabaseReference mDataNameRef = FirebaseDatabase.getInstance().getReference("users");
                                mDataNameRef.child(firebaseUser.getUid()).child("username").setValue(firstName + " " + lastName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("UPDATE_PROFILE", "setValue: success");

                                            loadingPanel.setVisibility(View.GONE);

                                            FirebaseAuth.getInstance().signOut();

                                            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.d("UPDATE_PROFILE", "setValue: failed");

                                            loadingPanel.setVisibility(View.GONE);
                                        }
                                    }
                                });

                            } else {
                                Log.d("UPDATE_PROFILE", "updateProfile: failed");

                                Toast.makeText(ResetPasswordActivity.this, "Der Name konnte nicht geändert werden.", Toast.LENGTH_SHORT).show();
                                loadingPanel.setVisibility(View.GONE);
                            }

                        }
                    });
                } else {
                    Log.d("UPDATE_PROFILE", "reauthenticate: failed");

                    Toast.makeText(ResetPasswordActivity.this, "Dein Passwort ist falsch.", Toast.LENGTH_SHORT).show();
                    loadingPanel.setVisibility(View.GONE);
                    snackbar.dismiss();
                }
            }
        });
    }


    public void changePassword(View view) {
        String oldString = mOldPasswordText.getText().toString().trim();
        String firstString = mFirstPasswordText.getText().toString().trim();
        final String newString = mNewPasswordText.getText().toString().trim();

        if (firstString.equals("") || oldString.equals("") || newString.equals("")) {
            Toast.makeText(this, "Gib bitte ein Passwort an, wenn du es ändern willst.", Toast.LENGTH_SHORT).show();
        }

        if (!firstString.equals(newString)) {
            Toast.makeText(this, "Deine Passwörter stimmen nicht überein", Toast.LENGTH_SHORT).show();
        } else if (oldString.equals(newString)) {
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

    public void getUserData() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        String[] firstLast = firebaseUser.getDisplayName().split("\\s+");
        mFirstNameText.setText(firstLast[0]);
        mLastNameText.setText(firstLast[1]);
        mUserEmail.setText(firebaseUser.getEmail());
    }

    public void enableProfileViews(boolean enable) {
        mFirstNameText.setFocusable(enable);
        mFirstNameText.setClickable(enable);
        mFirstNameText.setCursorVisible(enable);
        mFirstNameText.setFocusableInTouchMode(enable);

        mLastNameText.setFocusable(enable);
        mLastNameText.setClickable(enable);
        mLastNameText.setCursorVisible(enable);
        mLastNameText.setFocusableInTouchMode(enable);

        profileChangeable = enable;
    }

    /**
     * Inflates the menu in the toolbar.
     *
     * @param menu The mnu layout clicked.
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    /**
     * Handle clicks on the item of the toolbar.
     *
     * @param item Clicked menu-item.
     * @return boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete:
                // Ask user if he is sure.
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setIcon(R.drawable.pic_owl_icon)
                        .setTitle("Acount löschen")
                        .setMessage("Möchtest du wirklich deinen Account löschen? Das kann nicht rückgängig gemacht werden.")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Show dialog for user input.
                                initializeDialog();
                            }
                        })
                        .setNegativeButton("Nein", null) // nothing when canceled.
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Show the AlertDialog for user input.
     * Get the password of the user.
     */
    public void initializeDialog() {
        fireUser = FirebaseAuth.getInstance().getCurrentUser();// current user.
        mRefUser = FirebaseDatabase.getInstance().getReference("users");// reference to the user node.
        //DatabaseReference mRefEvent = FirebaseDatabase.getInstance().getReference("events"); //reference to the event node.

        // Setup dialog.
        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);
        alert.setTitle("Benutzer löschen");
        alert.setMessage("Geben Sie ihr Passort ein, um Ihren Account zu löschen.");
        alert.setIcon(R.drawable.pic_owl_icon);

        // Set an EditText view to get user password.
        final EditText passwordView = new EditText(this);
        passwordView.setHint(R.string.text_password);
        passwordView.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        alert.setView(passwordView); // Add EditText to alertView.

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                password = passwordView.getText().toString().trim(); // get user input.

                if (password.equals("") || password.length() < 6) { // check the input
                    Toast.makeText(ResetPasswordActivity.this, "Gib bitte dein Passwort ein", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("DELETE_USER", "User Email: " + fireUser.getEmail());
                    deleteUser(); // delete user.
                }
            }
        });
        alert.setNegativeButton("Cancel", null); // Do nothing on cancel.
        alert.show();
    }

    /**
     * Method to delete a user.
     */
    public void deleteUser() {
        uid = fireUser.getUid(); // Save uid, because its gone as soon as user is deleted.
        mRefUser.child(uid).removeValue(); // Remove the user-node first. Afterwards the permissions are missing.
        AuthCredential credential = EmailAuthProvider.getCredential(fireUser.getEmail(), password);

        // Reauthenticate the user.
        fireUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) { // successfull? -> delete User.
                            fireUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) { // successfill -> SignOut and go to WelcomeActivity.
                                        Log.d("DELETE_USER", "User account deleted: " + uid);
                                        Toast.makeText(ResetPasswordActivity.this, "Benutzer wurde gelöscht!", Toast.LENGTH_LONG).show();

                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(ResetPasswordActivity.this, WelcomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear every Activity.
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(ResetPasswordActivity.this, "Benutzer konnte nicht gelöscht werden... Versuchen Sie es erneut.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Das war nicht dein Passwort", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

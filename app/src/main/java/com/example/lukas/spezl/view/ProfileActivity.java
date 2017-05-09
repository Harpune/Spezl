package com.example.lukas.spezl.view;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.lukas.spezl.R;
import com.example.lukas.spezl.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;

public class ProfileActivity extends AppCompatActivity {

    private EditText mFirstNameText, mLastNameText, mEmailText, mAgeText;
    private TextInputLayout mFirstNameLayout, mLastNameLayout, mEmailLayout, mAgeLayout;
    private RelativeLayout loadingPanel;
    private RadioGroup mRadioGroup;

    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profil");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24);
        }

        mFirstNameLayout = (TextInputLayout) findViewById(R.id.input_layout_first_name);
        mLastNameLayout = (TextInputLayout) findViewById(R.id.input_layout_last_name);
        mEmailLayout = (TextInputLayout) findViewById(R.id.input_layout_email);
        mAgeLayout = (TextInputLayout) findViewById(R.id.input_layout_age);

        mFirstNameText = (EditText) findViewById(R.id.input_first_name);
        mLastNameText = (EditText) findViewById(R.id.input_last_name);
        mEmailText = (EditText) findViewById(R.id.input_email);
        mAgeText = (EditText) findViewById(R.id.input_age);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_sex);

        setupUser();
    }

    public void changeProfile(View view) {
        String firstName = mFirstNameText.getText().toString().trim();
        String lastName = mLastNameText.getText().toString().trim();
        String email = mEmailText.getText().toString().trim();
        String age = mAgeText.getText().toString().trim();

        mFirstNameLayout.setErrorEnabled(false);
        mLastNameLayout.setErrorEnabled(false);
        mEmailLayout.setErrorEnabled(false);
        mAgeLayout.setErrorEnabled(false);

        String[] firstLast = user.getUsername().split("\\s+");

        if(firstName.equals("")){
            mFirstNameLayout.setError("Gib deinen neuen Vornamen an.");
            return;
        }

        if(lastName.equals("")){
            mLastNameLayout.setError("Gib deinen neuen Nachnamen an.");
            return;
        }

        if(email.equals("")){
            mEmailLayout.setError("Gib eine E-Mailadresse an.");
            return;
        }

        if (age.matches("")) {
            mAgeLayout.setError("Wie alt bist du?");
            return;
        }

        Toast.makeText(ProfileActivity.this, "Kommt noch", Toast.LENGTH_LONG).show();

    }

    public void setupUser() {
        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();

        assert fireUser != null;
        String fireUserDisplayName = fireUser.getDisplayName();
        String[] firstLast = fireUserDisplayName.split("\\s+");
        mFirstNameText.setText(firstLast[0]);
        mLastNameText.setText(firstLast[1]);
        mEmailText.setText(fireUser.getEmail());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_password:
                Intent intent = new Intent(ProfileActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.delete:
                Toast.makeText(ProfileActivity.this, "Delte user", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

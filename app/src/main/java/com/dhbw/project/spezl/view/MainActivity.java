package com.dhbw.project.spezl.view;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dhbw.project.spezl.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    // Firebase Auth for FirebaseUser.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if FirebaseUser is logged in.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("AUTH", "Signed in... UserID: + " + user.getUid());
                    Intent intent = new Intent(MainActivity.this, DecisionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("AUTH", "Signed out...");
                }
            }
        };
        // Get Firebase Instance.
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);

        // Change font.
        TextView welcomeText = (TextView) findViewById(R.id.text_welcome);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/AmaticSC-Regular.ttf");
        welcomeText.setTypeface(typeFace);

    }

    /**
     * Start intent to Login Activity.
     *
     * @param view Login Button.
     */
    public void toLoginActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Start intent to Register Activity.
     *
     * @param view Register Button.
     */
    public void toRegisterActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Check if user is logged in.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Check if user is logged in.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}

package com.example.lukas.spezl.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lukas.spezl.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DecisionActivity extends AppCompatActivity {

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        initDrawerLayout();
    }

    public void showRelaxEvents(View view) {
        Toast.makeText(this, "Entspannt!", Toast.LENGTH_SHORT).show();
    }

    public void showPartyEvents(View view) {
        Toast.makeText(this, "Party!!", Toast.LENGTH_SHORT).show();
    }

    public void showSportEvents(View view) {
        Toast.makeText(this, "Sport!", Toast.LENGTH_SHORT).show();
    }

    public void showCookEvents(View view) {
        Toast.makeText(this, "Kochen!", Toast.LENGTH_SHORT).show();
    }

    public void showDiscussionEvents(View view) {
        Toast.makeText(this, "Diskussion!", Toast.LENGTH_SHORT).show();
    }

    public void showCultureEvents(View view) {
        Toast.makeText(this, "Kultur!", Toast.LENGTH_SHORT).show();
    }

    private void initDrawerLayout() {
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profile:
                        Toast.makeText(getApplicationContext(), "Profil", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.createEvent:
                        Intent createIntent = new Intent(getApplicationContext(), CreateActivity.class);
                        startActivity(createIntent);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(), "Einstellungen", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        FirebaseAuth.getInstance().signOut();
                        Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(logoutIntent);
                        finish();
                        break;
                    default:
                }
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);
        TextView mUsernameTextField = (TextView) header.findViewById(R.id.user_name);
        mUsernameTextField.setText(user.getEmail());//TODO name verwenden
    }


}

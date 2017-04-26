package com.example.lukas.spezl.View;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
    private final String TAG_CATEGORY = "TAG_CATEGORY";

    private FirebaseUser user;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision);

        user = FirebaseAuth.getInstance().getCurrentUser();

        initDrawerLayout();
    }

    public void showRelaxEvents(View view) {
        Intent intent = new Intent(DecisionActivity.this, RelaxActivity.class);
        intent.putExtra(TAG_CATEGORY, "Entspannt");
        startActivity(intent);
    }

    public void showPartyEvents(View view) {
        Intent intent = new Intent(DecisionActivity.this, RelaxActivity.class);
        intent.putExtra(TAG_CATEGORY, "Feiern");
        startActivity(intent);
    }

    public void showSportEvents(View view) {
        Intent intent = new Intent(DecisionActivity.this, RelaxActivity.class);
        intent.putExtra(TAG_CATEGORY, "Sport");
        startActivity(intent);
    }

    public void showCookEvents(View view) {
        Intent intent = new Intent(DecisionActivity.this, RelaxActivity.class);
        intent.putExtra(TAG_CATEGORY, "Kochen");
        startActivity(intent);
    }

    public void showDiscussionEvents(View view) {
        Intent intent = new Intent(DecisionActivity.this, RelaxActivity.class);
        intent.putExtra(TAG_CATEGORY, "Diskussion");
        startActivity(intent);
    }

    public void showCultureEvents(View view) {
        Intent intent = new Intent(DecisionActivity.this, RelaxActivity.class);
        intent.putExtra(TAG_CATEGORY, "Kultur");
        startActivity(intent);
    }

    private void initDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    supportInvalidateOptionsMenu();
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    supportInvalidateOptionsMenu();
                }
            };
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_white);
            mDrawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profile:
                        Toast.makeText(getApplicationContext(), "Profil", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.createEvent:
                        Intent createIntent = new Intent(getApplicationContext(), CreateActivity.class);
                        startActivity(createIntent);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(), "Einstellungen", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

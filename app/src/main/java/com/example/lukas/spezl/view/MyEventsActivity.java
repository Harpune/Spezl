package com.example.lukas.spezl.view;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.lukas.spezl.R;
import com.example.lukas.spezl.controller.EventAdapter;
import com.example.lukas.spezl.controller.MyEventAdapter;
import com.example.lukas.spezl.controller.StorageController;
import com.example.lukas.spezl.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MyEventsActivity extends AppCompatActivity {
    private FirebaseUser fireUser;

    private MyEventAdapter eventAdapter;

    private List<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Meine Events");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        // Implement recyclerView.
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        eventAdapter = new MyEventAdapter(events, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(eventAdapter);

        fireUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get the Events.
        //getAllLocalEventsFromUser();
        events.clear();
        events.addAll(StorageController.getAllLocalEvents(this));
        //deleteAllEvents();
    }



    /**
     * Inflates the menu in the toolbar.
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
     * @param item Clicked menu-item.
     * @return boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete:
                // Ask user if he is sure.
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.pic_owl_active)
                        .setTitle("Alle Events löschen")
                        .setMessage("Möchtest du wirklich deine lokalen Events löschen? Deine aktiven Events bleiben trotzdem online.")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Show dialog for user input.
                                //deleteAllEvents();
                                StorageController.deleteAllEvents(MyEventsActivity.this);
                                events.clear();
                                eventAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Nein", null) // nothing when canceled.
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        events.clear();
        events.addAll(StorageController.getAllLocalEvents(this));
        eventAdapter.notifyDataSetChanged();
        //getAllLocalEventsFromUser();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


}

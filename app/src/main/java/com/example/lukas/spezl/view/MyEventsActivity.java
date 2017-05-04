package com.example.lukas.spezl.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.lukas.spezl.R;
import com.example.lukas.spezl.controller.EventAdapter;
import com.example.lukas.spezl.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyEventsActivity extends AppCompatActivity {
    private FirebaseUser fireUser;

    private EventAdapter eventAdapter;

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
        eventAdapter = new EventAdapter(events, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(eventAdapter);

        fireUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get the Events.
        getAllLocalEventsFromUser();
        //deleteAllEvents();
    }

    public void deleteAllEvents() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
        Log.d("EVENTS", "deleted");
    }

    public void getAllLocalEventsFromUser() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = sharedPrefs.getString(fireUser.getUid(), "");
        Type type = new TypeToken<ArrayList<Event>>() {
        }.getType();

        events.clear();
        List<Event> eventsFromJson = new Gson().fromJson(json, type);

        if (eventsFromJson != null) {
            events.addAll(eventsFromJson);
            eventAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllLocalEventsFromUser();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


}

package com.example.lukas.spezl.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.lukas.spezl.R;
import com.example.lukas.spezl.controller.EventAdapter;
import com.example.lukas.spezl.model.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private final String TAG_CATEGORY = "TAG_CATEGORY";

    private EventAdapter eventAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Event> events = new ArrayList<>();

    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Get the intent.
        Intent intent = getIntent();
        category = intent.getStringExtra(TAG_CATEGORY);

        // Refresh the Events when swiped.
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRecyclerViewData();
            }
        });

        // Implement toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(category);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24);
        }

        // Implement recyclerView.
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        eventAdapter = new EventAdapter(events, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(eventAdapter);

        // Get the Events.
        getRecyclerViewData();
    }

    /**
     * Read the Events from the category events and delete events in the past.
     */
    public void getRecyclerViewData() {
        mSwipeRefreshLayout.setRefreshing(true);
        Log.d("CATEGORY", category);
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("events");
        Query query = mDatabaseRef.orderByChild("category").equalTo(category);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Get the event from database with its key (uid).
                    String key = postSnapshot.getKey();
                    Log.d("KEY", key);
                    Event event = postSnapshot.getValue(Event.class);

                    Calendar date = Calendar.getInstance();
                    date.setTime(event.getDate()); // your date

                    Calendar tooLate = Calendar.getInstance(); // today
                    tooLate.add(Calendar.DAY_OF_YEAR, -1); // too late

                    Log.d("DELETE_EVENT", "Date: " + date.get(Calendar.YEAR) + " " + date.get(Calendar.DAY_OF_YEAR));
                    Log.d("DELETE_EVENT", "Yesterday: " + tooLate.get(Calendar.YEAR) + " " + tooLate.get(Calendar.DAY_OF_YEAR));

                    if(date.get(Calendar.YEAR) <= tooLate.get(Calendar.YEAR)
                            && date.get(Calendar.DAY_OF_YEAR) <= tooLate.get(Calendar.DAY_OF_YEAR)){
                        deleteExpiredEvents(key);
                    } else {
                        event.setuId(key);
                        events.add(event);
                    }
                }

                // Sort by date.
                Collections.sort(events, new Comparator<Event>() {
                    public int compare(Event e1, Event e2) {
                        if (e1.getDate() == null || e2.getDate() == null)
                            return 0;
                        return e1.getDate().compareTo(e2.getDate());
                    }
                });

                // Notify adapter and stop refreshing
                eventAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void deleteExpiredEvents(String eventId) {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("events").child(eventId);
        mRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("DELETE_EVENT", "Event deleted: success");
                } else {
                    Log.d("DELETE_EVENT", "Event deleted: failed");
                }
            }
        });
    }

    public void createEvent(View view) {
        Intent intent = new Intent(CategoryActivity.this, CreateActivity.class);
        intent.putExtra(TAG_CATEGORY, category);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRecyclerViewData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

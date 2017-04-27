package com.example.lukas.spezl.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.lukas.spezl.controller.EventAdapter;
import com.example.lukas.spezl.model.Event;
import com.example.lukas.spezl.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends Activity {
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
        toolbar.setTitleTextColor(Color.WHITE);

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
     * Read the Events from the category events.
     */
    public void getRecyclerViewData() {
        mSwipeRefreshLayout.setRefreshing(true);
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("events").child(category);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Get the event from database with its key (uid).
                    String key = postSnapshot.getKey();
                    Log.d("KEY", key);
                    Event event = postSnapshot.getValue(Event.class);
                    event.setuId(key);
                    events.add(event);
                }

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

    public void createEvent(View view) {
        Intent intent = new Intent(CategoryActivity.this, CreateActivity.class);
        intent.putExtra(TAG_CATEGORY, category);
        startActivity(intent);
    }
}

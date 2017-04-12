package com.example.lukas.spezl.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.lukas.spezl.Controller.EventAdapter;
import com.example.lukas.spezl.Model.Event;
import com.example.lukas.spezl.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private List<Event> eventList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EventAdapter mEventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mEventAdapter = new EventAdapter(eventList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mEventAdapter);

        //getRecyclerViewDataTest();
        getRecyclerViewData();


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRecyclerViewData();
            }
        });
    }

    public void getRecyclerViewData() {
        mSwipeRefreshLayout.setRefreshing(true);
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("events");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Get the event from database with its key (uid).
                    String key = postSnapshot.getKey();
                    Log.d("KEY", key);
                    Event event = postSnapshot.getValue(Event.class);
                    event.setuId(key);
                    eventList.add(event);
                }

                // Notify adapter and stop refreshing
                mEventAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });

    }
/*
    public void getRecyclerViewDataTest() {
        Event event;
        for (int i = 0; i < 14; i++) {
            event = new Event("" + i, "Eintragnr.: " + i, "" + 5d, 4d, 6d, new Date(), "" + i, "" + i, "" + i);
            eventList.add(event);
        }
        mEventAdapter.notifyDataSetChanged();
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return false;
        }
    }

    public void getEvent(View view) {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }
}

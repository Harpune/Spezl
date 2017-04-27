package com.example.lukas.spezl.View;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lukas.spezl.Controller.UserAdapter;
import com.example.lukas.spezl.Model.Event;
import com.example.lukas.spezl.Model.User;
import com.example.lukas.spezl.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EventActivity extends AppCompatActivity {

    //TAGS.
    private final String TAG_EVENT_ID = "TAG_EVENT_ID";
    private final String TAG_EVENT_NAME = "TAG_EVENT_NAME";
    private final String TAG_DESCRIPTION = "TAG_DESCRIPTION";
    private final String TAG_MAX_PARTICIPANTS = "TAG_PARTICIPANTS";
    private final String TAG_AMOUNT_PARTICIPANTS = "TAG_AMOUNT_PARTICIPANTS";
    private final String TAG_EVENT_TOWN = "TAG_EVENT_TOWN";
    private final String TAG_EVENT_ADDRESS = "TAG_EVENT_ADDRESS";
    private final String TAG_EVENT_CATEGORY = "TAG_EVENT_CATEGORY";

    private final String TAG_OWNER_ID = "TAG_OWNER_ID";

    //The EventData.
    private String eventId, ownerId, eventCategory;
    private Double eventMaxParticipants;
    private int eventAmountPaticipants;

    // Views.
    private TextView mDescriptionText, mUsernameText;
    private RecyclerView mRecyclerView;
    private FloatingActionButton fab;
    // The Adapter of the RecyclerView responsible for the Users.
    private UserAdapter mUserAdapter;

    // List of the Users.
    private List<User> users = new ArrayList<>();

    // Global Database.
    private FirebaseDatabase mDatabase;

    private FirebaseUser fireUser;

    // Current event.
    private Event event = new Event();

    // Owner of the Event.
    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // Get the Intent information from the MainActivity
        Intent intent = getIntent();
        eventId = intent.getStringExtra(TAG_EVENT_ID);
        String eventDescription = intent.getStringExtra(TAG_DESCRIPTION);
        eventMaxParticipants = intent.getDoubleExtra(TAG_MAX_PARTICIPANTS, 0);
        eventAmountPaticipants = intent.getIntExtra(TAG_AMOUNT_PARTICIPANTS, 0);
        String eventTown = intent.getStringExtra(TAG_EVENT_TOWN);
        String eventAddress = intent.getStringExtra(TAG_EVENT_ADDRESS);
        String eventName = intent.getStringExtra(TAG_EVENT_NAME);
        eventCategory = intent.getStringExtra(TAG_EVENT_CATEGORY);
        ownerId = intent.getStringExtra(TAG_OWNER_ID);

        fireUser = FirebaseAuth.getInstance().getCurrentUser();
        readOwner();

        // Read the Event
        readEvent();

        // Initialize the Views.
        mDescriptionText = (TextView) findViewById(R.id.text_event_description);
        mUsernameText = (TextView) findViewById(R.id.text_user_name);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Set the title and the Views with intent-information.
        setTitle(eventName);
        mDescriptionText.setText("Beschreibung: \n\n" + eventDescription);

        //Setup the recyclerView and its adapter.
        mUserAdapter = new UserAdapter(users);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mUserAdapter);


    }

    /**
     * Triggered when fab is clicked.
     *
     * @param view The fab-view.
     */
    public void joinEvent(View view) {
        if (userAlreadyParticipates()) {
            String key = getKeyByValue(event.getParticipantIds(), fireUser.getUid());
            if(key != null){
                DatabaseReference mDatabaseRef = mDatabase.getReference("events")
                        .child(eventCategory)
                        .child(eventId)
                        .child("participantIds")
                        .child(key);
                mDatabaseRef.removeValue();
                onBackPressed();
            }


        } else {
            DatabaseReference mDatabaseRef = mDatabase.getReference("events")
                    .child(eventCategory)
                    .child(eventId)
                    .child("participantIds")
                    .push();
            mDatabaseRef.setValue(fireUser.getUid());
            onBackPressed();
        }

    }

    public static <T, E> T getKeyByValue(HashMap<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Check if user should be able to join the event.
     *
     * @return true if current user is already taking part.
     */
    private boolean userAlreadyParticipates() {
        return event.getParticipantIds() != null && event.getParticipantIds().containsValue(fireUser.getUid());
    }

    private void readOwner() {
        //TODO? Read user
    }

    /**
     * Read the participant ids deposited in the event object.
     */
    public void readEvent() {
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = mDatabase.getReference("events").child(eventCategory).child(eventId);
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
                event.setuId(dataSnapshot.getKey());
                Log.d("EVENT_FROM_SERVER", event.toString());

                //Check if user should be able to participate. TODO Benachrichtinung über zustand des users zum event
                if (fireUser.getUid().equals(ownerId)) {
                    Toast.makeText(EventActivity.this, "Das ist dein Event!", Toast.LENGTH_SHORT).show();
                    fab.setVisibility(View.GONE);
                } else if (userAlreadyParticipates()) {
                    Toast.makeText(EventActivity.this, "Du nimmst schon teil.", Toast.LENGTH_SHORT).show();
                    fab.setImageResource(R.drawable.ic_cancel);
                } else if (eventMaxParticipants == 0) {
                    // unendlich Viele Teilnhemer zulassen
                } else if (eventMaxParticipants <= eventAmountPaticipants) {
                    Toast.makeText(EventActivity.this, "Dieses Event ist leider voll.", Toast.LENGTH_SHORT).show();
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Da ist etwas falsch gelaufen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Read the User information from the given ids.
     *
     * @param participants The ids of the participants in a list.
     */
    public void readParticipants(List<String> participants) {
        for (int i = 0; i < participants.size(); i++) {
            DatabaseReference mDatabaseRef = mDatabase.getReference("users").child(participants.get(i));
            final int finalI = i; //for final sake.
            mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class); // read the user.
                    if (user != null) {
                        user.setUserId(dataSnapshot.getKey()); // add the userId to the user itself.
                        Log.d("GET_USERS-user", user.toString());
                        if (finalI == 0) { // the owner of the event.
                            //TODO wohin mit dem besitzer?
                        } else {
                            users.add(user); // add user to the list.
                            mUserAdapter.notifyItemChanged(finalI); // notify the adapter the new user.
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Da ist etwas falsch gelaufen", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

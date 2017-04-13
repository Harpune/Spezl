package com.example.lukas.spezl.View;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.IntegerRes;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventActivity extends AppCompatActivity {

    //TAGS.
    private final String TAG_EVENT_ID = "TAG_EVENT_ID";
    private final String TAG_EVENT_NAME = "TAG_EVENT_NAME";
    private final String TAG_OWNER_ID = "TAG_OWNER_ID";
    private final String TAG_DESCRIPTION = "TAG_DESCRIPTION";
    private final String TAG_OWNER_NAME = "TAG_OWNER_NAME";
    private final String TAG_MAX_PARTICIPANTS = "TAG_PARTICIPANTS";
    private final String TAG_EVENT_TOWN = "TAG_EVENT_TOWN";

    //The EventId.
    private String eventId, ownerName;

    // Views.
    private TextView mDescriptionText, mUsernameText;
    private RecyclerView mRecyclerView;

    // The Adapter of the RecyclerView responsible for the Users.
    private UserAdapter mUserAdapter;

    // List of the Users.
    private List<User> users = new ArrayList<>();

    // Global Database.
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        // Get the Intent information from the MainActivity
        Intent intent = getIntent();
        eventId = intent.getStringExtra(TAG_EVENT_ID);
        String ownerId = intent.getStringExtra(TAG_OWNER_ID);
        ownerName = intent.getStringExtra(TAG_OWNER_NAME);
        String eventDescription = intent.getStringExtra(TAG_DESCRIPTION);
        Double eventMaxParticipants = intent.getDoubleExtra(TAG_MAX_PARTICIPANTS, 0);
        String eventTown = intent.getStringExtra(TAG_EVENT_TOWN);
        String eventName = intent.getStringExtra(TAG_EVENT_NAME);


        // Initialize the Views.
        mDescriptionText = (TextView) findViewById(R.id.text_event_description);
        mUsernameText = (TextView) findViewById(R.id.text_user_name);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // Set the Titel and the Views with intent-information.
        setTitle(eventName);
        mDescriptionText.setText("<b>Beschreibung:</b> \n\n" + eventDescription);
        mUsernameText.setText(ownerName);

        //Setup the recyclerView and its adapter.
        mUserAdapter = new UserAdapter(users);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mUserAdapter);

        //Read the ids deposited in the event object.
        readParticipantsIds();

    }

    /**
     * Read the participant ids deposited in the event object.
     */
    public void readParticipantsIds(){
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = mDatabase.getReference("events").child(eventId);
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                Log.d("GET_PARTICIPANTS", event.toString());
                List<String> participants = event.getParticipantIds();
                readParticipants(participants);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Da ist etwas falsch gelaufen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Read the User information from the given ids.
     * @param participants The ids of the participants in a list.
     */
    public void readParticipants(List<String> participants){
        for(int i = 0; i < participants.size(); i++){
            DatabaseReference mDatabaseRef = mDatabase.getReference("users").child(participants.get(i));
            final int finalI = i; //for final sake.
            mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class); // read the user.
                    user.setUserId(dataSnapshot.getKey()); // add the userId to the user itself.
                    Log.d("GET_USERS-user", user.toString());

                    if(finalI == 0){ // the owner of the event.
                        //mUsernameText.setText(ownerName + " (" + user.getAge().intValue() + ")");
                    } else {
                        users.add(user); // add user to the list.
                        mUserAdapter.notifyItemChanged(finalI); // notify the adapter the new user.
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Da ist etwas falsch gelaufen", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Triggered when fab is clicked.
     * @param view The fab-view.
     */
    public void joinEvent(View view) {
        Toast.makeText(this, "Join Event", Toast.LENGTH_SHORT).show();


    }
}

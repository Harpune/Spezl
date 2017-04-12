package com.example.lukas.spezl.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lukas.spezl.Model.Event;
import com.example.lukas.spezl.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventActivity extends AppCompatActivity {

    private final String TAG_EVENT_ID = "TAG_EVENT_ID";
    private final String TAG_EVENT_NAME = "TAG_EVENT_NAME";
    private final String TAG_OWNER_ID = "TAG_OWNER_ID";
    private final String TAG_DESCRIPTION = "TAG_DESCRIPTION";
    private final String TAG_OWNER_NAME = "TAG_OWNER_NAME";
    private final String TAG_MAX_PARTICIPANTS = "TAG_PARTICIPANTS";
    private final String TAG_EVENT_TOWN = "TAG_EVENT_TOWN";

    private String eventId;

    private TextView mDescriptionText, mUsernameText, mParticipantsText;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        eventId = intent.getStringExtra(TAG_EVENT_ID);
        String ownerId = intent.getStringExtra(TAG_OWNER_ID);
        String ownerName = intent.getStringExtra(TAG_OWNER_NAME);
        String eventDescription = intent.getStringExtra(TAG_DESCRIPTION);
        Double eventMaxParticipants = intent.getDoubleExtra(TAG_MAX_PARTICIPANTS, 0);
        String eventTown = intent.getStringExtra(TAG_EVENT_TOWN);
        String eventName = intent.getStringExtra(TAG_EVENT_NAME);


        mDescriptionText = (TextView) findViewById(R.id.text_event_description);
        mUsernameText = (TextView) findViewById(R.id.text_user_name);
        mParticipantsText = (TextView) findViewById(R.id.text_event_participants);

        setTitle(eventName);
        mDescriptionText.setText(eventDescription);
        mUsernameText.setText(ownerName);
        mParticipantsText.setText(String.valueOf(eventMaxParticipants.intValue()));

        getParticipants();

    }

    public void getParticipants(){
        //TODO benutzer auslesen, die dem Event zugesagt haben!

    }

    public void joinEvent(View view) {
        Toast.makeText(this, "Join Event", Toast.LENGTH_SHORT).show();


    }
}

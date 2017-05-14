package com.dhbw.project.spezl.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dhbw.project.spezl.controller.UserAdapter;
import com.dhbw.project.spezl.model.Event;
import com.dhbw.project.spezl.model.User;
import com.dhbw.project.spezl.R;
import com.dhbw.project.spezl.controller.StorageController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

    private final int REQUEST_CODE = 1;

    private int count = 0;

    private List<User> users = new ArrayList<>();
    private UserAdapter userAdapter;

    //The EventData.
    private String eventId, ownerId, eventCategory;
    private Integer eventMaxParticipants;
    private int eventAmountParticipants;

    // Views.
    private TextView mDescriptionText, mDateText, mTownText, mPlaceText, mParticipantsText, mNotificationTextView;

    // Loading Panel.
    private RelativeLayout loadingPanel;

    //ImageButtons
    private Button joinEventButton, adminDeleteButton;

    // Global Database.
    private FirebaseDatabase mDatabase;
    private FirebaseUser fireUser;

    // Current event.
    private Event event = new Event();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        // Get the Intent information from the MainActivity
        Intent intent = getIntent();
        eventId = intent.getStringExtra(TAG_EVENT_ID);
        String eventDescription = intent.getStringExtra(TAG_DESCRIPTION);
        Double maxParticipants = intent.getDoubleExtra(TAG_MAX_PARTICIPANTS, 0);
        eventAmountParticipants = intent.getIntExtra(TAG_AMOUNT_PARTICIPANTS, 0);
        String eventTown = intent.getStringExtra(TAG_EVENT_TOWN);
        String eventAddress = intent.getStringExtra(TAG_EVENT_ADDRESS);
        String eventName = intent.getStringExtra(TAG_EVENT_NAME);
        eventCategory = intent.getStringExtra(TAG_EVENT_CATEGORY);
        ownerId = intent.getStringExtra(TAG_OWNER_ID);

        eventMaxParticipants = maxParticipants.intValue();

        fireUser = FirebaseAuth.getInstance().getCurrentUser();

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(eventName);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24);
        }

        // Initialize the Views.
        mDateText = (TextView) findViewById(R.id.text_event_date);
        mTownText = (TextView) findViewById(R.id.text_event_town);
        mPlaceText = (TextView) findViewById(R.id.text_event_place);
        mParticipantsText = (TextView) findViewById(R.id.text_event_participants);
        mDescriptionText = (TextView) findViewById(R.id.text_event_description);
        mNotificationTextView = (TextView) findViewById(R.id.notificationTextView);

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);

        joinEventButton = (Button) findViewById(R.id.join_event_button);
        adminDeleteButton = (Button) findViewById(R.id.delete_event_button_admin);
        adminDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEvent();
            }
        });

        // Set the title and the Views with intent-information.
        mDescriptionText.setText(eventDescription);
        mParticipantsText.setText(eventAmountParticipants + "/" + eventMaxParticipants + " Teilnehmer");
        if (eventAddress.equals("")) {
            mPlaceText.setText(eventTown);
        } else {
            mPlaceText.setText(eventTown + ", " + eventAddress);
        }

        // Setup RecyclerView Displaying the users participating.
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        userAdapter = new UserAdapter(users, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(userAdapter);

        // Read the Event
        readEvent();

        // Check if current user is admin.
        // checkForAdmin();
    }

    /**
     * Read the users participating from the event.getParticipantIds().
     */
    public void getRecyclerViewData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        for (final String current : event.getParticipantIds().values()) {
            databaseReference.child(current).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        Log.d("USER", user.toString());
                        users.add(user);
                        userAdapter.notifyItemChanged(count);
                        count++;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("READ_USERS", "failed");
                }
            });
        }
        loadingPanel.setVisibility(View.GONE);
    }

    /**
     * Check if current user is admin. If so make deleteButton visible.
     */
    public void checkForAdmin() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);

                if (currentUser != null && currentUser.isAdmin()) {
                    adminDeleteButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("CheckForAdmin", "Couldn't read Admin.");
            }
        });
    }

    /**
     * Delete the current event.
     */
    public void deleteEvent() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.pic_owl_icon)
                .setTitle("Event absagen")
                .setMessage("Willst du wirklich das Event löschen?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String key = getKeyByValue(event.getParticipantIds(), fireUser.getUid());
                        if (key != null) {
                            DatabaseReference mDatabaseRef = mDatabase
                                    .getReference("events")
                                    .child(eventId);
                            mDatabaseRef.removeValue();

                            //StorageController.deleteLocalEvent(event, EventActivity.this);
                            onBackPressed();
                        }
                    }
                })
                .setNegativeButton("Nein", null)
                .show();
    }

    /**
     * LEave the current event.
     */
    public void leaveEvent() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.pic_owl_icon)
                .setTitle("Event absagen")
                .setMessage("Willst du wirklich dem Event nicht mehr teilnehemen?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String key = getKeyByValue(event.getParticipantIds(), fireUser.getUid());
                        if (key != null) {
                            DatabaseReference mDatabaseRef = mDatabase.getReference("events")
                                    .child(eventId)
                                    .child("participantIds")
                                    .child(key);
                            mDatabaseRef.removeValue();

                            //StorageController.deleteLocalEvent(event, EventActivity.this);
                            onBackPressed();
                        }
                    }
                })
                .setNegativeButton("Nein", null)
                .show();
    }

    /**
     * User joins the event.
     */
    public void addUserToEvent() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.pic_owl_icon)
                .setTitle("Du nimmst teil!")
                .setMessage("Bist du dir sicher?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference mDatabaseRef = mDatabase.getReference("events")
                                .child(eventId)
                                .child("participantIds")
                                .push();
                        mDatabaseRef.setValue(fireUser.getUid());

                        //StorageController.storeLocalEvent(event, EventActivity.this);
                        onBackPressed();
                    }
                })
                .setNegativeButton("Nein", null)
                .show();
    }

    /**
     * Triggered when fab is clicked.
     *
     * @param view The fab-view.
     */
    public void joinEvent(final View view) {
        if (event.getParticipantIds().size() <= 1
                && event.getParticipantIds().values().toArray()[0].equals(fireUser.getUid())) {
            deleteEvent();
        } else if (userAlreadyParticipates()) {
            leaveEvent();
        } else {
            addUserToEvent();
        }
    }

    /**
     * Look for the key by having the value
     *
     * @param map   Key.
     * @param value Value.
     * @param <T>   String.
     * @param <E>   STring.
     * @return Key with Value.
     */
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

    /**
     * Read the participant ids deposited in the event object.
     */
    public void readEvent() {
        loadingPanel.setVisibility(View.VISIBLE);

        Log.d("EVENT_FROM_SERVER", eventId + " " + eventCategory);

        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = mDatabase.getReference("events").child(eventId);
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
                event.setuId(dataSnapshot.getKey());
                Log.d("EVENT_FROM_SERVER", event.toString());

                //Check if user should be able to participate.
                if (fireUser.getUid().equals(ownerId)) {
                    mNotificationTextView.setVisibility(View.VISIBLE);
                    mNotificationTextView.setText("Das ist mein Event! \nKlicke auf den Button um das Event in den Sand zu setzen.\n");
                    joinEventButton.setText("Löschen");
                    joinEventButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                } else if (userAlreadyParticipates()) {
                    mNotificationTextView.setVisibility(View.VISIBLE);
                    mNotificationTextView.setText("Du nimmst schon teil. Klicke auf \"Verlassen\" um auszutreten.");
                    joinEventButton.setText("Verlassen");
                    joinEventButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                } else if (eventMaxParticipants == 0) {
                    // unendlich Viele Teilnhemer zulassen
                    joinEventButton.setText("Teilnehmen");
                    joinEventButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                } else if (eventMaxParticipants <= eventAmountParticipants) {
                    mNotificationTextView.setVisibility(View.VISIBLE);
                    mNotificationTextView.setText("Dieses Event ist leider voll.");
                    joinEventButton.setVisibility(View.GONE);
                } else {
                    mNotificationTextView.setVisibility(View.GONE);
                    joinEventButton.setText("Teilnehmen");
                    joinEventButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                }

                DateFormat dfDate = android.text.format.DateFormat.getDateFormat(EventActivity.this);
                DateFormat dfTime = android.text.format.DateFormat.getTimeFormat(EventActivity.this);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd.MM.yyyy", Locale.getDefault());
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());


                // Setup the views.
                mDateText.setText(simpleDateFormat.format(event.getDate()) + " um " + simpleTimeFormat.format(event.getDate()));
                mPlaceText.setText(event.getPlace());
                mDescriptionText.setText(event.getDescription());


                if (event.getMaxParticipants() == 0) {
                    mParticipantsText.setText(event.getParticipantIds().size() + " Teilnehmer");
                } else {
                    mParticipantsText.setText(event.getParticipantIds().size() + "/" + event.getMaxParticipants().intValue() + " Teilnehmer");
                }

                if (!event.getAddress().equals("")) {
                    mTownText.setText(event.getTown() + ", " + event.getAddress());
                } else {
                    mTownText.setText(event.getTown() + ". " + event.getPlace());
                }

                getRecyclerViewData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Da ist etwas falsch gelaufen", Toast.LENGTH_SHORT).show();
                loadingPanel.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

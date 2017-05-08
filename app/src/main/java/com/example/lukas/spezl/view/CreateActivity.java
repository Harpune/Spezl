package com.example.lukas.spezl.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lukas.spezl.R;
import com.example.lukas.spezl.controller.StorageController;
import com.example.lukas.spezl.model.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CreateActivity extends AppCompatActivity {
    private final String TAG_CATEGORY = "TAG_CATEGORY";

    private Calendar mCalendar = Calendar.getInstance();

    private EditText mNameText, mDescriptionText, mDateText, mTimeText, mPlaceText, mTownText, mAddressText;

    private RelativeLayout loadingPanel;

    private ScrollView scrollView;

    private TextInputLayout mNameLayout, mDescriptionLayout, mDateLayout, mTimeLayout, mPlaceLayout, mTownLayput, mAddressLayout;

    private String category = "Entspannt";
    private Double maxParticipants = 0.0;

    private Event event = new Event();

    private FirebaseUser fireUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // Implement toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.text_create_event);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24);
        }

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        // Get all necessary views from the Layout.
        mNameText = (EditText) findViewById(R.id.input_name);
        mDescriptionText = (EditText) findViewById(R.id.input_description);
        mDateText = (EditText) findViewById(R.id.input_date);
        mTimeText = (EditText) findViewById(R.id.input_time);
        mPlaceText = (EditText) findViewById(R.id.input_place);
        mTownText = (EditText) findViewById(R.id.input_town);
        mAddressText = (EditText) findViewById(R.id.input_address);

        mNameLayout = (TextInputLayout) findViewById(R.id.input_layout_name);
        mDescriptionLayout = (TextInputLayout) findViewById(R.id.input_layout_description);
        mDateLayout = (TextInputLayout) findViewById(R.id.input_layout_date);
        mTimeLayout = (TextInputLayout) findViewById(R.id.input_layout_time);
        mPlaceLayout = (TextInputLayout) findViewById(R.id.input_layout_place);
        mTownLayput = (TextInputLayout) findViewById(R.id.input_layout_town);
        mAddressLayout = (TextInputLayout) findViewById(R.id.input_layout_address);

        // Get the intent if the fireUser comes from CategoryActivity.
        Intent intent = getIntent();
        if (intent.hasExtra(TAG_CATEGORY)) {
            category = intent.getStringExtra(TAG_CATEGORY);
        }
        //Setup spinner with categories.
        setupCategorySpinner();

        //Setup spinner with participants.
        setupParticipantSpinner();

        // Get the dates.
        getDateFromUser();
        getTimeFromUser();
    }

    public void create(View view) {
        // Read the input.
        String name = mNameText.getText().toString().trim();
        String description = mDescriptionText.getText().toString().trim();
        String date = mDateText.getText().toString().trim();
        String time = mTimeText.getText().toString().trim();
        String place = mPlaceText.getText().toString().trim();
        String town = mTownText.getText().toString().trim();
        String address = mAddressText.getText().toString().trim();

        // Disable all error notifications of the TextInputLayouts.
        mNameLayout.setErrorEnabled(false);
        mDescriptionLayout.setErrorEnabled(false);
        mDateLayout.setErrorEnabled(false);
        mTimeLayout.setErrorEnabled(false);
        mPlaceLayout.setErrorEnabled(false);
        mTownLayput.setErrorEnabled(false);
        mAddressLayout.setErrorEnabled(false);

        //Build the date.
        Date dateTime = null;
        String dateFormat = date + time;
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d MMMM yyyyHH:mm", Locale.getDefault());
        try {
            dateTime = format.parse(dateFormat);
            mCalendar.setTime(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Check the input for wrong input.
        if (name.matches("")) {
            mNameLayout.setError("Gib bitte deinen Namen für deine Veranstaltung an");
            mNameText.requestFocus();
            focusOnView(mNameLayout);
            return;
        }

        if (date.matches("")) {
            mDateLayout.setError("Man muss doch wissen, wann es statt findet");
            mDateText.requestFocus();
            focusOnView(mDateLayout);
            return;
        }

        if (time.matches("")) {
            mTimeLayout.setError("Wann genau?");
            mTimeText.requestFocus();
            focusOnView(mTimeLayout);
            return;
        }

        Calendar nowDate = Calendar.getInstance();

        long now = nowDate.getTimeInMillis();
        long than = mCalendar.getTimeInMillis();

        // Check if date is in the past.
        if (than < now) {
            mDateLayout.setError("Wähle einen Zeitpunkt weiter in der Zukunft.");
            mTimeLayout.setError("Siehe oben");
            nowDate.add(Calendar.HOUR, 1);
            updateDate(nowDate);
            updateTime(nowDate);
            mDateText.requestFocus();
            focusOnView(mTimeLayout);
            return;
        }

        if (town.matches("")) {
            mTownLayput.setError("Wo findet dein Event statt?");
            mTownText.requestFocus();
            focusOnView(mTownLayput);
            return;
        }

        if (place.matches("")) {
            mPlaceLayout.setError("Wohin geht's?");
            mPlaceText.requestFocus();
            focusOnView(mPlaceLayout);
            return;
        }

        loadingPanel.setVisibility(View.VISIBLE);

        //Create the event from the given information.
        fireUser = FirebaseAuth.getInstance().getCurrentUser();

        event.setName(name);
        event.setDescription(description);
        event.setDate(dateTime);
        event.setCreationDate(new Date());
        event.setPlace(place);
        event.setMaxParticipants(maxParticipants);
        event.setTown(town);
        event.setAddress(address);
        event.setCategory(category);

        //Add fireUser data to the event
        assert fireUser != null;
        event.setOwnerId(fireUser.getUid());

        // Create database connection and reference.
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference mDatabaseRef = mDatabase.getReference();

        // Push the event and create own uid.
        DatabaseReference newEvent = mDatabase.getReference("events").push();

        // Set Id of the event.
        final String key = newEvent.getKey();
        event.setuId(key);

        // Upload the event to firebase.
        newEvent.setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DatabaseReference addOwner = mDatabaseRef
                            .child("events")
                            .child(key)
                            .child("participantIds")
                            .push();

                    // Create HashMap with owner-id.
                    String key = addOwner.getKey();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(key, fireUser.getUid());
                    event.setParticipantIds(hashMap);

                    addOwner.setValue(fireUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Store the event locally
                                //storeEvent();
                                StorageController.storeLocalEvent(event, CreateActivity.this);

                                loadingPanel.setVisibility(View.GONE);

                                // Start MainActivity.
                                new AlertDialog.Builder(CreateActivity.this)
                                        .setIcon(R.drawable.pic_owl_icon)
                                        .setTitle("Geschafft!")
                                        .setMessage("Event wurde erstellt")
                                        .setCancelable(false)
                                        .setPositiveButton("Und los", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(CreateActivity.this, DecisionActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .show();
                            } else {
                                Toast.makeText(CreateActivity.this, "Da lief etwas schief...", Toast.LENGTH_LONG).show();
                                loadingPanel.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    Toast.makeText(CreateActivity.this, "Das Event konnte nicht erstellt werden. Versuche es erneut.", Toast.LENGTH_LONG).show();
                    loadingPanel.setVisibility(View.GONE);
                }

            }
        });
    }

    /**
     * Start Date Dialog to get the Date of the event.
     */
    private void getDateFromUser() {
        //Create the DatePickerDialog
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate(mCalendar);
            }
        };

        // Show the DatePickerDialog
        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateActivity.this, R.style.TimePicker, date,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000));
                datePickerDialog.show();
            }
        });
    }

    /**
     * Update the editTextField with chosen date.
     */
    private void updateDate(Calendar calendar) {
        String dateFormat = "EEEE, d MMMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

        mDateText.setText(sdf.format(calendar.getTime()));
    }

    /**
     * Start Time Dialog to get the time of the event.
     */
    public void getTimeFromUser() {
        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                updateTime(mCalendar);
            }
        };

        // Show the DatePickerDialog
        mTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateActivity.this, R.style.TimePicker, time,
                        mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE),
                        true);
                timePickerDialog.show();
            }
        });
    }

    /**
     * Update the editTextField with chosen time.
     */
    private void updateTime(Calendar calendar) {
        String timeFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
        String oClock = sdf.format(calendar.getTime()) + getString(R.string.text_oclock);
        mTimeText.setText(oClock);
    }

    private void setupParticipantSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_participants);
        final String[] participants = new String[]{"2", "3", "4", "5", "6", "7", "8", "Offen"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, participants);
        spinner.setAdapter(adapter);

        // Set adapterPosition to the value from the intent. Default: Entspannt.
        int spinnerPosition = adapter.getPosition("Offen");
        spinner.setSelection(spinnerPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (participants[pos].equals("Offen")) {
                    maxParticipants = 0.0;
                } else {
                    maxParticipants = Double.parseDouble(participants[pos]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "Keine Kategorie ausgewählt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategorySpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_category);
        final String[] categories = new String[]{"Entspannt", "Feiern", "Sport", "Kochen", "Diskussion", "Kultur"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(adapter);

        // Set adapterPosition to the value from the intent. Default: Entspannt.
        int spinnerPosition = adapter.getPosition(category);
        spinner.setSelection(spinnerPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                category = categories[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "Keine Kategorie ausgewählt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void focusOnView(final TextInputLayout view){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                Log.d("FOCUS_VIEW", "" + view.getTop());
                scrollView.smoothScrollTo(0, view.getTop());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

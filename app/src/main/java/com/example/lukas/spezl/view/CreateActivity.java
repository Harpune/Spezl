package com.example.lukas.spezl.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lukas.spezl.model.Event;
import com.example.lukas.spezl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CreateActivity extends Activity {
    private final String TAG_CATEGORY = "TAG_CATEGORY";

    private Calendar mCalendar = Calendar.getInstance();

    private EditText mNameText, mDescriptionText, mDateText, mTimeText, mPlaceText, mTownText, mAddressText, mMaxParticipentsText;

    private TextInputLayout mNameLayout, mDescriptionLayout, mDateLayout, mTimeLayout, mPlaceLayout, mTownLayput, mAddressLayout, mMaxParticipentsLayout;

    private String category = "Entspannt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // Get all necessary views from the Layout.
        mNameText = (EditText) findViewById(R.id.input_name);
        mDescriptionText = (EditText) findViewById(R.id.input_description);
        mDateText = (EditText) findViewById(R.id.input_date);
        mTimeText = (EditText) findViewById(R.id.input_time);
        mPlaceText = (EditText) findViewById(R.id.input_place);
        mTownText = (EditText) findViewById(R.id.input_town);
        mAddressText = (EditText) findViewById(R.id.input_address);
        mMaxParticipentsText = (EditText) findViewById(R.id.input_max_participants);

        mNameLayout = (TextInputLayout) findViewById(R.id.input_layout_name);
        mDescriptionLayout = (TextInputLayout) findViewById(R.id.input_layout_description);
        mDateLayout = (TextInputLayout) findViewById(R.id.input_layout_date);
        mTimeLayout = (TextInputLayout) findViewById(R.id.input_layout_time);
        mPlaceLayout = (TextInputLayout) findViewById(R.id.input_layout_place);
        mTownLayput = (TextInputLayout) findViewById(R.id.input_layout_town);
        mAddressLayout = (TextInputLayout) findViewById(R.id.input_layout_address);
        mMaxParticipentsLayout = (TextInputLayout) findViewById(R.id.input_layout_max_participants);

        // Get the intent if the user comes from CategoryActivity.
        Intent intent = getIntent();
        if (intent.hasExtra(TAG_CATEGORY)) {
            category = intent.getStringExtra(TAG_CATEGORY);
        }

        //Setup spinner with categories
        Spinner mSpinner = (Spinner) findViewById(R.id.spinner);
        final String[] categories = new String[]{"Entspannt", "Feiern", "Sport", "Kochen", "Diskussion", "Kultur"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        mSpinner.setAdapter(adapter);

        // Set adapterPosition to the value from the intent. Default: Entspannt.
        int spinnerPosition = adapter.getPosition(category);
        mSpinner.setSelection(spinnerPosition);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                category = categories[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "Keine Kategorie ausgewählt", Toast.LENGTH_SHORT).show();
            }
        });

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
        String maxParticipantsString = mMaxParticipentsText.getText().toString().trim();


        // Disable all error notifications of the TextInputLayouts.
        mNameLayout.setErrorEnabled(false);
        mDescriptionLayout.setErrorEnabled(false);
        mDateLayout.setErrorEnabled(false);
        mTimeLayout.setErrorEnabled(false);
        mPlaceLayout.setErrorEnabled(false);
        mTownLayput.setErrorEnabled(false);
        mAddressLayout.setErrorEnabled(false);
        mMaxParticipentsLayout.setErrorEnabled(false);


        //Check the input for wrong input.
        if (name.matches("")) {
            mNameLayout.setError("Gib bitte deinen Namen für deine Veranstaltung an");
            mNameText.requestFocus();
            return;
        }

        if (description.matches("")) {
            mDescriptionLayout.setError("Eine Beschreibung wäre bestimmt nicht schlecht");
            mDescriptionText.requestFocus();
            return;
        }

        if (date.matches("")) {
            mDateLayout.setError("Man muss doch wissen, wann es statt findet");
            mDateText.requestFocus();
            return;
        }

        if (time.matches("")) {
            mTimeLayout.setError("Wann genau?");
            mTimeText.requestFocus();
            return;
        }

        Calendar now  = Calendar.getInstance();
        Log.d("CALENDER", "Now: " + now.getTimeInMillis());
        Log.d("CALENDER", "Then: " + mCalendar.getTimeInMillis());
        if (mCalendar.getTime().getTime() < now.getTime().getTime()) {// TODO check if chosen date is in the past..
            mDateLayout.setError("Dein Event ist in der Vergangenheit");
            mTimeLayout.setError("Wähle mindestens den jetzigen Termin");
            now.add(Calendar.MINUTE, 1);
            updateDate(now);
            updateTime(now);
            mDateText.requestFocus();
            return;
        }

        if (place.matches("")) {
            mPlaceLayout.setError("Wohin geht's?");
            mPlaceText.requestFocus();
            return;
        }

        if (town.matches("")) {
            mTownLayput.setError("In Tumbuktu?");
            mTownText.requestFocus();
            return;
        }

        Double maxParticipants;
        if (maxParticipantsString.matches("")) {
            maxParticipants = 0d;
        } else {
            maxParticipants = Double.parseDouble(maxParticipantsString);
        }

        if (maxParticipants > 8) {
            mMaxParticipentsLayout.setError("Maximal 8 Teilnehmer. Wähle 0 wenn es egal ist, wie viele kommen!");
            return;
        }

        //Build the date.
        Date dateTime = null;
        String dateFormat = date + time;
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d MMMM yyyykk:mm", Locale.getDefault());
        try {
            dateTime = format.parse(dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Create the event from the given information.
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setDate(dateTime);
        event.setCreationDate(new Date());
        event.setPlace(place);
        event.setMaxParticipants(maxParticipants);
        event.setTown(town);
        event.setAddress(address);
        event.setCategory(category);

        //Add user data to the event
        assert user != null;
        event.setOwnerId(user.getUid());



        // Create database connection and reference.
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = mDatabase.getReference();

        // Push the event and create own uid.
        DatabaseReference newEvent = mDatabase.getReference("events")
                .child(category)
                .push();
        newEvent.setValue(event);
        String key = newEvent.getKey();
        DatabaseReference addOwner = mDatabaseRef
                .child("events")
                .child(category)
                .child(key)
                .child("participantIds")
                .push();
        addOwner.setValue(user.getUid());


        // Start MainActivity.
        Intent intent = new Intent(CreateActivity.this, DecisionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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
        String timeFormat = "kk:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
        String oClock = sdf.format(calendar.getTime()) + getString(R.string.text_oclock);
        mTimeText.setText(oClock);
    }
}

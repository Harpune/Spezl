package com.example.lukas.spezl.View;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lukas.spezl.Controller.TagAdapter;
import com.example.lukas.spezl.Model.Event;
import com.example.lukas.spezl.Model.User;
import com.example.lukas.spezl.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateActivity extends Activity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;

    private Calendar mCalendar = Calendar.getInstance();

    private EditText mNameText, mDescriptionText, mDateText, mTimeText, mTownText;

    private TextInputLayout mNameLayout, mDescriptionLayout, mDateLayout, mTimeLayout, mTownLayput;

    private RecyclerView mRecyclerView;

    private TagAdapter mTagAdapter;

    private List<String> tagList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mNameText = (EditText) findViewById(R.id.input_name);
        mDescriptionText = (EditText) findViewById(R.id.input_description);
        mDateText = (EditText) findViewById(R.id.input_date);
        mTimeText = (EditText) findViewById(R.id.input_time);
        mTownText = (EditText) findViewById(R.id.input_town);

        mNameLayout = (TextInputLayout) findViewById(R.id.input_layout_name);
        mDescriptionLayout = (TextInputLayout) findViewById(R.id.input_layout_description);
        mDateLayout = (TextInputLayout) findViewById(R.id.input_layout_date);
        mTimeLayout = (TextInputLayout) findViewById(R.id.input_layout_time);
        mTownLayput = (TextInputLayout) findViewById(R.id.input_layout_town);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mTagAdapter = new TagAdapter(tagList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.HORIZONTAL));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mTagAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        getDateFromUser();
        getTimeFromUser();
    }

    public void create(View view) {
        String name = mNameText.getText().toString().trim();
        String description = mDescriptionText.getText().toString().trim();
        String date = mDateText.getText().toString().trim();
        String time = mTimeText.getText().toString().trim();
        String town = mTownText.getText().toString().trim();

        mNameLayout.setErrorEnabled(false);
        mDescriptionLayout.setErrorEnabled(false);
        mDateLayout.setErrorEnabled(false);
        mTimeLayout.setErrorEnabled(false);
        mTownLayput.setErrorEnabled(false);

        Log.d("DATE", date);

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
        if (town.matches("")) {
            mTownLayput.setError("In Tumbuktu?");
            mTownText.requestFocus();
            return;
        }

        Date dateTime = null;
        String dateFormat = date + time;
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d MMMM yyyykk:mm", Locale.getDefault());
        try {
            dateTime = format.parse(dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final RelativeLayout loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        loadingPanel.setVisibility(View.VISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setDate(dateTime);//TODO Date hinzufügen
        event.setTown(town);
        assert user != null;
        event.setOwnerId(user.getUid());

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();

        mDatabaseRef.child("events").push().setValue(event);

        loadingPanel.setVisibility(View.GONE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getDateFromUser() {
        //Create the DatePickerDialog
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };

        // Show the DatePickerDialog
        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateActivity.this, date,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
    }

    private void updateDate() {
        String dateFormat = "EEEE, d MMMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

        mDateText.setText(sdf.format(mCalendar.getTime()));
    }

    public void getTimeFromUser() {
        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                updateTime();
            }
        };

        // Show the DatePickerDialog
        mTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateActivity.this, time,
                        mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE),
                        true);
                timePickerDialog.show();
            }
        });
    }

    private void updateTime() {
        String timeFormat = "kk:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
        String oClock = sdf.format(mCalendar.getTime()) + getString(R.string.text_oclock);
        mTimeText.setText(oClock);
    }

    /**
     * Triggered if new a new tag should be added
     *
     * @param view Clicked TextField.
     */
    public void addTag(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tag");

        // Set up the input
        final EditText input = new EditText(this);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTag = input.getText().toString();
                tagList.add(newTag);
                mTagAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}

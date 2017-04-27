package com.example.lukas.spezl.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.lukas.spezl.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateActivity extends Activity {
    private Calendar mCalendar = Calendar.getInstance();
    private EditText mDateText = null;
    private EditText mStartTimeText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        mDateText = (EditText) findViewById(R.id.input_date);
        mStartTimeText = (EditText) findViewById(R.id.input_start_time);

        getDateFromUser();
        getTimeFromUser();
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(DateActivity.this, date,
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
        mStartTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(DateActivity.this, time,
                        mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE),
                        true);
                timePickerDialog.show();
            }
        });
    }

    private void updateTime(){
        String timeFormat = "kk:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
        String oClock = sdf.format(mCalendar.getTime()) + getString(R.string.text_oclock);
        mStartTimeText.setText(oClock);
    }
}

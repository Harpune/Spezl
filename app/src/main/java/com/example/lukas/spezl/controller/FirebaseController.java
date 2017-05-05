package com.example.lukas.spezl.controller;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.lukas.spezl.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FirebaseController {

    private static FirebaseUser fireUser;

    public static void deleteEvent(Event event, Context context) {

    }

    public static List<Event> getAllEvents(Context context) {
        return new ArrayList<>();
    }

    public static void storeEvent(Event event, Context context) {

    }

    public static List<Event> getAllEventsFromUser(final Context context) {
        final List<Event> list = new ArrayList<>();

        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();

        assert fireUser != null;
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("events");
        Query query = mDatabaseRef.orderByChild("ownerId").equalTo(fireUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Get the event from database with its key (uid).
                    String key = postSnapshot.getKey();
                    Event event = postSnapshot.getValue(Event.class);
                    event.setuId(key);
                    list.add(event);
                }

                Log.d("KEY", list.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Daten konnten nicht gelesen werden", Toast.LENGTH_LONG).show();
            }
        });

        return sortEventsByDate(list);
    }

    public static void deleteAllEvents(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
    }

    public static List<Event> sortEventsByDate(List<Event> events) {
        Collections.sort(events, new Comparator<Event>() {
            public int compare(Event e1, Event e2) {
                if (e1.getDate() == null || e2.getDate() == null)
                    return 0;
                return e1.getDate().compareTo(e2.getDate());
            }
        });
        return events;
    }
}

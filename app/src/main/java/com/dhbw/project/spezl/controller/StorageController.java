package com.dhbw.project.spezl.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dhbw.project.spezl.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StorageController {

    private static FirebaseUser fireUser;

    public static void deleteLocalEvent(Event event, Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        List<Event> events = getAllLocalEvents(context);

        if (events == null) {
            events = new ArrayList<>();
        }

        Log.d("STORAGE_CONTROLLER", "Delete: " + event.toString());

        for (Event e : events) {
            if (event.getuId().equals(e.getuId())) {
                events.remove(e);
                break;
            }
        }

        String json = new Gson().toJson(events);

        editor.putString(fireUser.getUid(), json);
        editor.apply();
    }

    public static List<Event> getAllLocalEvents(Context context) {
        fireUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sharedPrefs.getString(fireUser.getUid(), "");

        Type type = new TypeToken<ArrayList<Event>>() {}.getType();

        Log.d("STORAGE_CONTROLLER", "All: " + json);
        List<Event> list = new Gson().fromJson(json, type);
        if(list != null){
            return sortEventsByDate(list);
        } else {
            return new ArrayList<>();
        }
    }

    public static void storeLocalEvent(Event event, Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        List<Event> events = getAllLocalEvents(context);
        if (events == null) {
            events = new ArrayList<>();
        }
        events.add(event);
        Log.d("STORAGE_CONTROLLER", "storeEvents = " + events);

        String json = new Gson().toJson(events);

        editor.putString(fireUser.getUid(), json);
        editor.apply();
    }

    public static void deleteAllLocalEvents(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
        Log.d("STORAGE_CONTROLLER", "All deleted");
    }

    public static List<Event> sortEventsByDate(List<Event> events){
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

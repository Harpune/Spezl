package com.example.lukas.spezl.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class User {
    private DatabaseReference mDatabase;

    public String userId;
    public String username;
    public String town;
    public String email;
    public Double age;

    public User(){}

    public User(String userId, String username, String town, String email, Double age) {
        this.username = username;
        this.town = town;
        this.email = email;
        this.age = age;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", userId);
        result.put("username", username);
        result.put("city", town);
        result.put("email", email);
        result.put("age", age);

        return result;
    }
}

package com.example.lukas.spezl.model;

import java.util.Date;
import java.util.HashMap;

public class Event {
    private String uId;
    private String name;
    private String description;
    private Double maxParticipants;
    private Date date;
    private Date creationDate;
    private String place;
    private String town;
    private String address;
    private String category;
    private String ownerId;
    private String imageUri;
    private HashMap<String, String> participantIds;

    public Event() {
    }

    public Event(String uId, String name, String description, Double maxParticipants, Date date, Date creationDate, String place, String town, String address, String category, String ownerId, String imageUri, HashMap<String, String> participantIds) {
        this.uId = uId;
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.date = date;
        this.creationDate = creationDate;
        this.place = place;
        this.town = town;
        this.address = address;
        this.category = category;
        this.ownerId = ownerId;
        this.imageUri = imageUri;
        this.participantIds = participantIds;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Double maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public HashMap<String, String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(HashMap<String, String> participantIds) {
        this.participantIds = participantIds;
    }

    @Override
    public String toString() {
        return "Event{" +
                "uId='" + uId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", maxParticipants=" + maxParticipants +
                ", date=" + date +
                ", creationDate=" + creationDate +
                ", place='" + place + '\'' +
                ", town='" + town + '\'' +
                ", address='" + address + '\'' +
                ", category='" + category + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", participantIds=" + participantIds +
                '}';
    }
}


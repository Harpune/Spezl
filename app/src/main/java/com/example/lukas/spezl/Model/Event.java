package com.example.lukas.spezl.Model;

import java.util.Date;
import java.util.List;

public class Event {
    private String uId;
    private String name;
    private String description;
    private Double maxParticipants;
    private Date date;
    private String town;
    private String ownerId;
    private String ownerName;
    private String imageUri;
    private List<String> participantIds;
    private List<String> tags;

    public Event() {
    }

    public Event(String uId, String name, String description, Double maxParticipants, Date date, String town, String ownerId, String ownerName, String imageUri, List<String> participantIds, List<String> tags) {
        this.uId = uId;
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.date = date;
        this.town = town;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.imageUri = imageUri;
        this.participantIds = participantIds;
        this.tags = tags;
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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}

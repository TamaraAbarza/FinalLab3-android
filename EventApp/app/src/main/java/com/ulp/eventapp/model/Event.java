package com.ulp.eventapp.model;

import java.io.Serializable;

public class Event implements Serializable {
    private int id;
    private String name;
    //private Date date;
    private String date;
    private String location;
    private String description;
    private String imageUrl;
    private boolean isParticipating = false;

    public Event(int id, String name, String date, String location, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public Event(String name, String date, String location, String description) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
    }

    public Event(String name, String date, String location, String description, String imageUrl) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isParticipating() {
        return isParticipating;
    }

    public void setParticipating(boolean participating) {
        isParticipating = participating;
    }

    public Event() {
    }
}

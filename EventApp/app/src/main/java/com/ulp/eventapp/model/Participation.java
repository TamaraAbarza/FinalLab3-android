package com.ulp.eventapp.model;

import java.io.Serializable;

public class Participation implements Serializable {
    private int id;
    private boolean isConfirmed;
    private int userId;
    private User user;
    private int eventId;
    private Event event;

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean isConfirmed() { return isConfirmed; }
    public void setConfirmed(boolean confirmed) { isConfirmed = confirmed; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
}
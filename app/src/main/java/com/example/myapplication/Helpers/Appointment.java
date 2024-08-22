package com.example.myapplication.Helpers;

import android.util.Log;

public class Appointment {

    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;

    public String clientName;
    public String barberId;
    public String barberName;

    public Appointment() {
        // Default constructor
    }

    public Appointment(String clientName, int year, int month, int day, int hour, int minute, String barberId, String barberName) {
        this.clientName = clientName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.barberId = barberId;
        this.barberName = barberName;
    }

    public String formatDateTime() {
        // Returns a string in the format "Date: YYYY-MM-DD Time: HH:MM"
        return String.format("Date: %04d-%02d-%02d\nTime: %02d:%02d", year, month, day, hour, minute);
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getBarberName() {
        return barberName;
    }

}
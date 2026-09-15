package com.wac.autocore.model;

import java.time.LocalDate;

public class Booking {

    private int id;
    private int vehicleId;
    private LocalDate date;
    private String description;
    private String status;

    public Booking(int id, int vehicleId, LocalDate date, String description) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.date = date;
        this.description = description;
        this.status = "BOOKED";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + " - Vehicle ID: " + vehicleId +
                " | Date: " + date +
                " | Description: " + description +
                " | Status: " + status;
    }
}
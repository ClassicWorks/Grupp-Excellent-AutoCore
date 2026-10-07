package com.wac.autocore.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "description")
    private String description;

    @Column(name = "description_lang", length = 5)
    private String descriptionLang;

    @Column(name = "description_translated", length = 1000)
    private String descriptionTranslated;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    /*public Booking(int id, int vehicle, LocalDate date, String description) {
        this.id = id;
        this.vehicle = vehicle;
        this.date = date;
        this.description = description;
        this.status = "BOOKED";
    }*/

    public Booking(Vehicle vehicle, LocalDate date, String description) {
        this.vehicle = vehicle;
        this.date = date;
        this.description = description;
        this.status = "BOOKED";
    }

    public Booking(Vehicle vehicle, LocalDate date, String description,
                   String descriptionLang, String descriptionTranslated) {
        this(vehicle, date, description);
        this.descriptionLang = descriptionLang;
        this.descriptionTranslated = descriptionTranslated;
    }

    protected Booking() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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

    public String getDescriptionLang() {
        return descriptionLang;
    }

    public void setDescriptionLang(String descriptionLang) {
        this.descriptionLang = descriptionLang;
    }

    public String getDescriptionTranslated() {
        return descriptionTranslated;
    }

    public void setDescriptionTranslated(String descriptionTranslated) {
        this.descriptionTranslated = descriptionTranslated;
    }

    public String getDescriptionFor(String language) {
        boolean hasTranslation = descriptionTranslated != null && !descriptionTranslated.trim().isEmpty();
        boolean otherLanguage = descriptionLang != null && language != null && !descriptionLang.equals(language);
        if (hasTranslation && otherLanguage) {
            return descriptionTranslated;
        }
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + " - Vehicle ID: " + vehicle +
                " | Date: " + date +
                " | Description: " + description +
                " | Status: " + status;
    }
}
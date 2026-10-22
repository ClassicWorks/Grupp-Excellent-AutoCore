package com.wac.autocore.service;

import com.wac.autocore.data.BookingRepo;
import com.wac.autocore.data.VehicleRepo;
import com.wac.autocore.exceptions.BookingNotFoundException;
import com.wac.autocore.exceptions.InputNotValidException;
import com.wac.autocore.exceptions.VehicleNotFoundException;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.TranslationService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BookingService {
    private final VehicleRepo vehicleRepo;
    private final BookingRepo bookingRepo;

    public BookingService(VehicleRepo vehicleRepo, BookingRepo bookingRepo) {
        this.vehicleRepo = vehicleRepo;
        this.bookingRepo = bookingRepo;
    }

    public void showBookings() {
        System.out.println();
        System.out.println("=== BOOKINGS ===");

        if (bookingRepo.getAll().isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Booking booking : bookingRepo.getAll()) {
            System.out.println(booking);
        }
    }

    public List<Booking> getBookings() {
        List<Booking> bookings = bookingRepo.getAll();
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
        }

        return bookings;
    }

    public Optional<Booking> getBooking(int id){
        return bookingRepo.get(id);
    }

    public Booking createBookingWithTranslation(int vehicleId,
                                                LocalDate date,
                                                String description,
                                                String descriptionLang) {
        if(vehicleId <= 0){
            throw new InputNotValidException("Vehicle ID cannot be zero or negative.");
        }
        if(date == null){
            throw new InputNotValidException("Booking date cannot be empty.");
        }
        if(description == null || description.trim().isEmpty() || description.length() > 255){
            throw new InputNotValidException("Booking description cannot be empty or more that 255 characters.");
        }
        if(descriptionLang == null || descriptionLang.trim().isEmpty() || descriptionLang.length() > 5){
            throw new InputNotValidException("Description language can not be empty or more than 5 characters.");
        }

        Optional<Vehicle> optionalVehicle = vehicleRepo.get(vehicleId);

        if (!optionalVehicle.isPresent()) {
            throw new VehicleNotFoundException(vehicleId);
        }

        String targetLang = "sv".equals(descriptionLang) ? "en" : "sv";
        String translated = TranslationService.translate(description, descriptionLang, targetLang);

        Booking booking = new Booking(
                optionalVehicle.get(),
                date,
                description,
                descriptionLang,
                translated
        );

        Booking savedBooking = bookingRepo.save(booking);

        System.out.println("Booking created successfully.");
        System.out.println(savedBooking);

        return savedBooking;
    }
}

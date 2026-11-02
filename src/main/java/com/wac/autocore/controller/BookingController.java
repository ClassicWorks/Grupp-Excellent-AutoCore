package com.wac.autocore.controller;

import com.wac.autocore.model.Booking;
import com.wac.autocore.service.BookingService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BookingController {
    private final BookingService bookingService;
    private final ErrorHandler errorHandler;
    public BookingController(BookingService bookingService, ErrorHandler errorHandler) {
        this.bookingService = bookingService;
        this.errorHandler = errorHandler;
    }

    public Result<List<Booking>> getBookings() {
        return errorHandler.call(bookingService::getBookings);
    }

    public Result<Optional<Booking>> getBooking(int id) {
        return  errorHandler.call(() -> bookingService.getBooking(id));
    }

    public Result<Booking> createBooking(int vehicleId,
                                         LocalDate date,
                                         String description,
                                         String descriptionLang) {
        return errorHandler.call(() ->
                bookingService.createBookingWithTranslation(vehicleId, date, description, descriptionLang));
    }
}

package com.wac.autocore.data;


import com.wac.autocore.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepo {
    Booking save(Booking b);
    Booking update(Booking b);
    List<Booking> getAll();
    Optional<Booking> get(int id);
}

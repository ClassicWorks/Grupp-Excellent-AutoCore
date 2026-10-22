package com.wac.autocore.service;

import com.wac.autocore.data.BookingRepo;
import com.wac.autocore.data.CustomerRepo;
import com.wac.autocore.data.VehicleRepo;
import com.wac.autocore.exceptions.CustomerNotFoundException;
import com.wac.autocore.exceptions.DeletionNotAllowedException;
import com.wac.autocore.exceptions.InputNotValidException;
import com.wac.autocore.exceptions.VehicleNotFoundException;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class VehicleService {
    private final VehicleRepo vehicleRepo;
    private final CustomerRepo customerRepo;
    private final BookingRepo bookingRepo;

    public VehicleService(VehicleRepo vehicleRepo, CustomerRepo customerRepo, BookingRepo bookingRepo) {
        this.vehicleRepo = vehicleRepo;
        this.customerRepo = customerRepo;
        this.bookingRepo = bookingRepo;
    }

    //For testing
    public void showVehicles() {
        System.out.println();
        System.out.println("=== VEHICLES ===");

        if (vehicleRepo.getAll().isEmpty()) {
            System.out.println("No vehicles found.");
            return;
        }

        for (Vehicle vehicle : vehicleRepo.getAll()) {
            System.out.println(vehicle);
        }
    }

    public List<Vehicle> getVehicles() {
        List<Vehicle> vehicles = vehicleRepo.getAll();
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
        }
        return vehicles;
    }

    public Optional<Vehicle> getVehicle(int id){
        return vehicleRepo.get(id);
    }

    public List<Vehicle> getCustomersVehicle(int customerId){
        return vehicleRepo.getVehicleByCustomerId(customerId);
    }

    public Vehicle createVehicle(String registrationNumber,
                                 String brand,
                                 String model,
                                 int year,
                                 int customerId) {
        if(registrationNumber == null || registrationNumber.trim().isEmpty()){
            throw new InputNotValidException("Vehicle registration number cannot be empty.");
        }
        if(brand == null || brand.trim().isEmpty()){
            throw new InputNotValidException("Vehicle brand cannot be empty.");
        }
        if(model == null || model.trim().isEmpty()){
            throw new InputNotValidException("Vehicle model cannot be empty.");
        }
        if(year <= 1649 || year > LocalDate.now().getYear()){
            throw new InputNotValidException("Vehicle year cannot be before 1649 or efter current year.");
        }
        if(customerId <= 0){
            throw new InputNotValidException("Customer ID cannot be zero or negative.");
        }

        Optional<Customer> optionalCustomer = customerRepo.get(customerId);

        if (!optionalCustomer.isPresent()) {
            throw new CustomerNotFoundException(customerId);
        }
        Customer customer = optionalCustomer.get();


        Vehicle vehicle = new Vehicle(
                registrationNumber,
                brand,
                model,
                year,
                customer
        );

        Vehicle savedVehicle = vehicleRepo.save(vehicle);

        System.out.println("Vehicle created successfully.");
        System.out.println(savedVehicle);

        return savedVehicle;
    }

    public Vehicle updateVehicle(int vehicleId, String registrationNumber, String brand,
                                String model, int year, Integer customerId) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()
                || model == null || model.trim().isEmpty()) {
            throw new InputNotValidException("Registration number and model cannot be empty.");
        }

        Optional<Vehicle> optionalVehicle = getVehicle(vehicleId);

        if (!optionalVehicle.isPresent()) {
            throw new VehicleNotFoundException(vehicleId);
        }

        Customer customer = null;
        if (customerId != null) {
            Optional<Customer> optionalCustomer = customerRepo.get(customerId);

            if (!optionalCustomer.isPresent()) {
                throw new CustomerNotFoundException(customerId);
            }
            customer = optionalCustomer.get();
        }

        Vehicle vehicle = optionalVehicle.get();
        vehicle.setRegistrationNumber(registrationNumber.trim());
        vehicle.setBrand(brand == null ? "" : brand.trim());
        vehicle.setModel(model.trim());
        vehicle.setYear(year);
        vehicle.setCustomer(customer);

        return vehicleRepo.update(vehicle);
    }

    public boolean vehicleHasBookings(int vehicleId) {
        return bookingRepo.vehicleHasBooking(vehicleId);
    }

    //TODO om alla bokningar är klar/avbrutna, koppla lös dem
    public boolean deleteVehicle(int vehicleId) {
        if (!vehicleRepo.get(vehicleId).isPresent()) {
            throw new VehicleNotFoundException(vehicleId);
        }
        if (vehicleHasBookings(vehicleId)) {
            throw new DeletionNotAllowedException("Vehicle has bookings and cannot be deleted.");
        }
        vehicleRepo.delete(vehicleId);
        return true;
    }
}

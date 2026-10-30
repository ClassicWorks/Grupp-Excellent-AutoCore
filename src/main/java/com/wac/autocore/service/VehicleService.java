package com.wac.autocore.service;

import com.wac.autocore.data.BookingRepo;
import com.wac.autocore.data.CustomerRepo;
import com.wac.autocore.data.VehicleRepo;
import com.wac.autocore.exceptions.*;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.security.AccessGuard;
import com.wac.autocore.security.Permission;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class VehicleService {
    private final VehicleRepo vehicleRepo;
    private final CustomerRepo customerRepo;
    private final BookingRepo bookingRepo;
    private final AccessGuard accessGuard;

    public VehicleService(VehicleRepo vehicleRepo, CustomerRepo customerRepo, BookingRepo bookingRepo,
                          AccessGuard accessGuard) {
        this.vehicleRepo = vehicleRepo;
        this.customerRepo = customerRepo;
        this.bookingRepo = bookingRepo;
        this.accessGuard = accessGuard;
    }

    public List<Vehicle> getVehicles() {
        accessGuard.require(Permission.READ, "getVehicles");
        List<Vehicle> vehicles = vehicleRepo.getAll();
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
        }
        return vehicles;
    }

    public Optional<Vehicle> getVehicle(int id){
        accessGuard.require(Permission.READ, "getVehicle");
        return vehicleRepo.get(id);
    }

    public List<Vehicle> getCustomersVehicle(int customerId){
        accessGuard.require(Permission.READ, "getCustomersVehicle");
        return vehicleRepo.getVehicleByCustomerId(customerId);
    }

    /**
     * Creates a new Vehicle
     *
     * @param registrationNumber the registration number of the vehicle; must not be null or empty
     * @param brand the brand of the vehicle; must not be null or empty
     * @param model the model of the vehicle; must not be null or empty
     * @param year the year of manufacture of the vehicle; must be between 1650 and the current year
     * @param customerId the ID of the associated customer; must be a positive value and correspond
     *                   to an existing customer in the repository
     * @return the newly created and saved Vehicle instance
     * @throws InputNotValidException if any input parameter is invalid (e.g., null, empty, or out of range)
     * @throws NotUniqueException if a vehicle with the same registration number already exists
     * @throws CustomerNotFoundException if a customer with the given ID does not exist
     */
    public Vehicle createVehicle(String registrationNumber,
                                 String brand,
                                 String model,
                                 int year,
                                 int customerId) {
        accessGuard.require(Permission.MANAGE_VEHICLES, "createVehicle");

        if(registrationNumber == null || registrationNumber.trim().isEmpty() || registrationNumber.trim().length() > 10){
            throw new InputNotValidException("Vehicle registration number cannot be empty or longer than 10 characters.");
        }
        if(brand == null || brand.trim().isEmpty() || brand.trim().length() > 20){
            throw new InputNotValidException("Vehicle brand cannot be empty or longer than 20 characters.");
        }
        if(model == null || model.trim().isEmpty() || model.trim().length() > 20){
            throw new InputNotValidException("Vehicle model cannot be empty or longer than 20 characters.");
        }
        if(year <= 1649 || year > LocalDate.now().getYear()){
            throw new InputNotValidException("Vehicle year cannot be before 1649 or after current year.");
        }
        if(customerId <= 0){
            throw new InputNotValidException("Customer ID cannot be zero or negative.");
        }

        if(vehicleRepo.existsByRegistrationNumber(registrationNumber)){
            throw new NotUniqueException("Vehicle registration number already exists, must be unique.");
        }

        Customer customer = customerRepo.get(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

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

    /**
     * Updates the details of an existing vehicle based on the provided parameters. All details are updated
     * in the vehicle
     *
     * @param vehicleId the unique identifier of the vehicle to update; must correspond to an existing vehicle
     * @param registrationNumber the new registration number for the vehicle; must not be null, empty, or already in use
     * @param brand the new brand of the vehicle; must not be null or empty
     * @param model the new model of the vehicle; must not be null or empty
     * @param year the new year of manufacture of the vehicle; must be between 1650 and the current year
     * @param customerId the ID of the associated customer; must be a positive value and correspond to an existing customer
     * @return the updated Vehicle instance
     * @throws InputNotValidException if any input parameter is invalid (e.g., null, empty, or out of range)
     * @throws VehicleNotFoundException if no vehicle is found with the given vehicleId
     * @throws NotUniqueException if the new registration number is already in use
     * @throws CustomerNotFoundException if no customer is found with the given customerId
     */
    public Vehicle updateVehicle(int vehicleId, String registrationNumber, String brand,
                                 String model, int year, int customerId) {
        accessGuard.require(Permission.MANAGE_VEHICLES, "updateVehicle");

        if(registrationNumber == null || registrationNumber.trim().isEmpty() || registrationNumber.trim().length() > 10){
            throw new InputNotValidException("Vehicle registration number cannot be empty or longer than 10 characters.");
        }
        if(brand == null || brand.trim().isEmpty() || brand.trim().length() > 20){
            throw new InputNotValidException("Vehicle brand cannot be empty or longer than 20 characters.");
        }
        if(model == null || model.trim().isEmpty() || model.trim().length() > 20){
            throw new InputNotValidException("Vehicle model cannot be empty or longer than 20 characters.");
        }
        if(year <= 1649 || year > LocalDate.now().getYear()){
            throw new InputNotValidException("Vehicle year cannot be before 1649 or after current year.");
        }
        if(customerId <= 0){
            throw new InputNotValidException("Customer ID cannot be zero or negative.");
        }

        Vehicle vehicle = getVehicle(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        if(!registrationNumber.equalsIgnoreCase(vehicle.getRegistrationNumber()) && vehicleRepo.existsByRegistrationNumber(registrationNumber)){
            throw new NotUniqueException("Vehicle registration number already exists, must be unique.");
        }

        Customer customer = customerRepo.get(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        vehicle.setRegistrationNumber(registrationNumber.trim());
        vehicle.setBrand(brand.trim());
        vehicle.setModel(model.trim());
        vehicle.setYear(year);
        vehicle.setCustomer(customer);

        return vehicleRepo.update(vehicle);
    }

    public boolean vehicleHasBookings(int vehicleId) {
        accessGuard.require(Permission.READ, "vehicleHasBookings");
        return bookingRepo.vehicleHasBooking(vehicleId);
    }

    /**
     * Deletes the vehicle with the specified ID if it exists and has no associated bookings.
     *
     * @param vehicleId the unique identifier of the vehicle to delete; must correspond to an existing vehicle
     * @return true if the vehicle was successfully deleted
     * @throws VehicleNotFoundException if no vehicle is found with the given vehicleId
     * @throws DeletionNotAllowedException if the vehicle has active bookings and cannot be deleted
     */
    public boolean deleteVehicle(int vehicleId) {
        accessGuard.require(Permission.MANAGE_VEHICLES, "deleteVehicle");

        if (vehicleRepo.get(vehicleId).isEmpty()) {
            throw new VehicleNotFoundException(vehicleId);
        }
        if (vehicleHasBookings(vehicleId)) {
            throw new DeletionNotAllowedException("Vehicle has bookings and cannot be deleted.");
        }
        vehicleRepo.delete(vehicleId);
        return true;
    }
}
package com.wac.autocore.service;

import com.wac.autocore.data.*;
import com.wac.autocore.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class GarageSystem {
    private final VehicleRepo vehicleRepo;
    private final CustomerRepo customerRepo;
    private final MechanicRepo mechanicRepo;
    private final ServiceItemRepo serviceItemRepo;
    private final BookingRepo bookingRepo;
    private final WorkOrderRepo workOrderRepo;
    private final ServicePackRepo servicePackRepo;

    private final CustomerService customerService;
    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    private final VehicleService vehicleService;
    private final BookingService bookingService;
    private final WorkOrderService workOrderService;
    private final MechanicService mechanicService;
    private final ServiceItemService serviceItemService;

    public GarageSystem() {
        vehicleRepo = new VehicleRepoImpl();
        customerRepo = new CustomerRepoImpl();
        mechanicRepo = new MechanicRepoImpl();
        serviceItemRepo = new ServiceItemRepoImpl();
        bookingRepo = new BookingRepoImpl();
        workOrderRepo = new WorkOrderRepoImpl();
        servicePackRepo = new ServicePackRepoImpl();

        customerService = new CustomerService(customerRepo, vehicleRepo);
        invoiceService = new InvoiceService(new InvoiceRepoImpl(), workOrderRepo);
        paymentService = new PaymentService(new PaymentRepoImpl(), invoiceService);
        vehicleService = new VehicleService(vehicleRepo, customerRepo, bookingRepo);
        workOrderService = new WorkOrderService(bookingRepo, workOrderRepo, mechanicRepo, serviceItemRepo);
        bookingService = new BookingService(vehicleRepo, bookingRepo);
        mechanicService = new MechanicService(mechanicRepo, workOrderRepo);
        serviceItemService = new ServiceItemService(serviceItemRepo, servicePackRepo);
    }

    public void showCustomers() {
        customerService.showCustomers();
    }

    public List<Customer> getCustomers() {
        return customerService.getCustomers();
    }

    public Optional<Customer> getCustomer(int id){
        return customerService.getCustomer(id);
    }


    public List<Customer> getCustomerMatching(String query) {
        return customerService.getCustomerMatching(query);
    }

    public void showVehicles() {
        vehicleService.showVehicles();
    }

    public List<Vehicle> getVehicles() {
        return vehicleService.getVehicles();
    }

    public Optional<Vehicle> getVehicle(int id){
        return vehicleService.getVehicle(id);
    }

    public List<Vehicle> getCustomersVehicle(int customerId){
        return vehicleService.getCustomersVehicle(customerId);
    }

    public void showBookings() {
        bookingService.showBookings();
    }

    public List<Booking> getBookings() {
        return bookingService.getBookings();
    }

    public Optional<Booking> getBooking(int id){
        return bookingService.getBooking(id);
    }

    public void showServiceItems() {
        serviceItemService.showServiceItems();
    }

    public List<ServiceItem> getServiceItems() {
        return serviceItemService.getServiceItems();
    }

    public List<ServiceItem> getServiceItemsByIds(List<Integer> serviceIds) {
        return serviceItemService.getServiceItemsByIds(serviceIds);
    }


    public List<Integer> getMissingIds(List<Integer> lookingForIds, List<ServiceItem> serviceList) {
        return serviceItemService.getMissingIds(lookingForIds, serviceList);
    }

    public Optional<ServiceItem> getServiceItem(int id){
        return serviceItemService.getServiceItem(id);
    }

    public ServiceItem updateServiceItemPrice(int serviceItemId, double newPrice) {
        return serviceItemService.updateServiceItemPrice(serviceItemId, newPrice);
    }

    public Customer updateCustomer(int customerId, String name, String phone, String email) {
        return customerService.updateCustomer(customerId, name, phone, email);
    }

    public Mechanic updateMechanic(int mechanicId, String name, String phone, String specialization) {
        return mechanicService.updateMechanic(mechanicId, name, phone, specialization);
    }

    public Vehicle updateVehicle(int vehicleId, String registrationNumber, String brand,
                                 String model, int year, Integer customerId) {
       return vehicleService.updateVehicle(vehicleId, registrationNumber, brand, model, year, customerId);
    }

    public boolean customerHasVehicles(int customerId) {
        return customerService.customerHasVehicles(customerId);
    }

    public boolean vehicleHasBookings(int vehicleId) {
        return vehicleService.vehicleHasBookings(vehicleId);
    }

    public boolean mechanicHasUncompletedWorkOrders(int mechanicId) {
        return mechanicService.mechanicHasUncompletedWorkOrders(mechanicId);
    }

    public boolean deleteCustomer(int customerId) {
        return customerService.deleteCustomer(customerId);
    }

    public boolean deleteVehicle(int vehicleId) {
        return vehicleService.deleteVehicle(vehicleId);
    }

    public boolean deleteMechanic(int mechanicId) {
        return mechanicService.deleteMechanic(mechanicId);
    }

    public void showServicePacks() {
        serviceItemService.showServicePacks();
    }

    public List<ServicePack> getServicePacks() {
        return serviceItemService.getServicePacks();
    }

    public Optional<ServicePack> getServicePack(int id){
        return serviceItemService.getServicePack(id);
    }

    public ServicePack updateServicePack(int servicePackId, ServicePack updatedServicePack){
        return serviceItemService.updateServicePack(servicePackId, updatedServicePack);
    }

    public void deleteServicePack(int id){
        serviceItemService.deleteServicePack(id);
    }

    public void showMechanics() {
        mechanicService.showMechanics();
    }

    public List<Mechanic> getMechanics() {
        return mechanicService.getMechanics();
    }

    public List<Mechanic> getAvailableMechanics() {
        return mechanicService.getAvailableMechanics();
    }

    public Optional<Mechanic> getMechanic(int id){
        return mechanicService.getMechanic(id);
    }

    public void showWorkOrders() {
        workOrderService.showWorkOrders();
    }

    public List<WorkOrder> getWorkOrders() {
        return workOrderService.getWorkOrders();
    }

    public List<WorkOrder> getWorkOrdersWithItems(){
        return workOrderService.getWorkOrdersWithItems();
    }

    public List<WorkOrder> getWorkOrdersWithItemsByStatus(WorkOrderStatus status){
        return workOrderService.getWorkOrdersWithItemsByStatus(status);
    }

    public List<WorkOrder> getWorkOrdersThatCanBeInvoiced() {
        return invoiceService.getWorkOrdersThatCanBeInvoiced();
    }

    public Optional<WorkOrder> getWorkOrderWithItems(int workOrderId) {
        return workOrderService.getWorkOrderWithItems(workOrderId);
    }

    public Optional<WorkOrder> getWorkOrder(int id){
        return workOrderService.getWorkOrder(id);
    }

    public List<Invoice> getInvoices() {
        return invoiceService.getInvoices();
    }

    public Optional<Invoice> getInvoice(int id) {
        return invoiceService.getInvoice(id);
    }

    public List<Payment> getPayments() {
        return paymentService.getPayments();
    }

    public List<Invoice> getUnpaidInvoices() {
        return invoiceService.getUnpaidInvoices();
    }

    public List<Invoice> getPaidInvoices() {
        return invoiceService.getPaidInvoices();
    }

    public Customer createCustomer(String name, String phone, String email) {
        return customerService.createCustomer(name, phone, email);
    }

    public Vehicle createVehicle(String registrationNumber,
                                 String brand,
                                 String model,
                                 int year,
                                 int customerId) {
        return vehicleService.createVehicle(registrationNumber, brand, model, year, customerId);
    }

    public Booking createBookingWithTranslation(int vehicleId,
                                                LocalDate date,
                                                String description,
                                                String descriptionLang) {
        return bookingService.createBookingWithTranslation(vehicleId, date, description, descriptionLang);
    }


    public ServicePack createServicePack(String name, int... serviceItemIds){
        return serviceItemService.createServicePack(name, serviceItemIds);
    }

    public WorkOrder createWorkOrder(int bookingId,
                                     int mechanicId,
                                     WorkOrderType type,
                                     int... serviceItemIds) {
        return workOrderService.createWorkOrder(bookingId, mechanicId, type, serviceItemIds);
    }

    public void confirmWorkOrder (int workOrderId){
        workOrderService.confirmWorkOrder(workOrderId);
    }

    public WorkOrder updateWorkOrderServices(int workOrderId,
                                             List<Integer> serviceItemIdsToAdd,
                                             List<Integer> itemIdsToRemove) {
        return workOrderService.updateWorkOrderServices(workOrderId, serviceItemIdsToAdd, itemIdsToRemove);
    }


    public WorkOrder updateWorkOrderType(int workOrderId, WorkOrderType type) {
        return workOrderService.updateWorkOrderType(workOrderId, type);
    }

    /**
     * Updates which mechanic is connected to work order.
     * Method throws WorkOrderNotFoundException if workOrderId is not present in database.
     * Throws StatusNotValidException if the status of the work order is not in an editable state.
     * Throws MechanicNotFoundException if mechanicId is not present in database.
     * Throws MechanicNotAvailableException if the mechanic is not available.
     * @param workOrderId id of work order to be updated.
     * @param mechanicId id of mechanic to be connected to work order. If value is 0, the mechanic connected to work order will be changed to null.
     * @return updated work order
     */
    public WorkOrder updateWorkOrderMechanic(int workOrderId, int mechanicId){
        return workOrderService.updateWorkOrderMechanic(workOrderId, mechanicId);
    }

    public void startWorkOrder(int workOrderId) {
        workOrderService.startWorkOrder(workOrderId);
    }

    public void completeWorkOrder(int workOrderId) {
        workOrderService.completeWorkOrder(workOrderId);
    }

    public void cancelWorkOrder(int workOrderId) {
        workOrderService.cancelWorkOrder(workOrderId);
    }

    public Invoice createInvoice(int workOrderId, String discountCode) {
        return invoiceService.createInvoice(workOrderId, discountCode);
    }

    public Payment processPayment(int invoiceId, String paymentType) {
        return paymentService.processPayment(invoiceId, paymentType);
    }
}
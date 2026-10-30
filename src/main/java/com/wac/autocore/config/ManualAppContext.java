package com.wac.autocore.config;

import com.wac.autocore.controller.*;
import com.wac.autocore.data.*;
import com.wac.autocore.security.*;
import com.wac.autocore.service.*;

public class ManualAppContext implements AppContext {

    private final GarageSystem garageSystem;
    private final Authenticator authenticator;
    private final AppControllers controllers;
    private final AccessGuard accessGuard;

    public ManualAppContext() {
        // Repositories
        VehicleRepo vehicleRepo = new VehicleRepoImpl();
        CustomerRepo customerRepo = new CustomerRepoImpl();
        MechanicRepo mechanicRepo = new MechanicRepoImpl();
        ServiceItemRepo serviceItemRepo = new ServiceItemRepoImpl();
        BookingRepo bookingRepo = new BookingRepoImpl();
        WorkOrderRepo workOrderRepo = new WorkOrderRepoImpl();
        ServicePackRepo servicePackRepo = new ServicePackRepoImpl();

        // Security
        accessGuard = new AccessGuard(AppSession.getInstance());
        authenticator = new DatabaseAuthenticator(new UserRepoImpl(), AppSession.getInstance(), new PasswordHasher());

        // Services
        CustomerService customerService = new CustomerService(customerRepo, vehicleRepo, accessGuard);
        VehicleService vehicleService = new VehicleService(vehicleRepo, customerRepo, bookingRepo, accessGuard);
        BookingService bookingService = new BookingService(vehicleRepo, bookingRepo);
        WorkOrderService workOrderService = new WorkOrderService(bookingRepo, workOrderRepo, mechanicRepo, serviceItemRepo);
        MechanicService mechanicService = new MechanicService(mechanicRepo, workOrderRepo, accessGuard);
        ServiceItemService serviceItemService = new ServiceItemService(serviceItemRepo, servicePackRepo, accessGuard);
        InvoiceService invoiceService = new InvoiceService(new InvoiceRepoImpl(), workOrderRepo, accessGuard);
        PaymentService paymentService = new PaymentService(new PaymentRepoImpl(), invoiceService, accessGuard);

        // Controllers
        ErrorHandler errorHandler = new ErrorHandler();
        controllers = new AppControllers(
                new WorkOrderController(workOrderService, errorHandler),
                new InvoiceController(invoiceService, accessGuard, errorHandler),
                new PaymentController(paymentService, accessGuard, errorHandler),
                new CustomerController(customerService, errorHandler),
                new CatalogController(serviceItemService, errorHandler),
                new LoginController(authenticator),
                new MechanicController(mechanicService, errorHandler),
                new VehicleController(vehicleService, errorHandler)
        );

        // GarageSystem (Kvar tills vyer använder controllers)
        garageSystem = new GarageSystem(customerService, vehicleService, bookingService, workOrderService,
                mechanicService, serviceItemService, invoiceService, paymentService);
    }

    @Override
    public GarageSystem garageSystem() {
        return garageSystem;
    }

    @Override
    public Authenticator authenticator() {
        return authenticator;
    }

    @Override
    public CurrentUserProvider currentUserProvider() {
        return AppSession.getInstance();
    }

    @Override
    public AccessGuard accessGuard() {
        return accessGuard;
    }

    @Override
    public AppControllers controllers() {
        return controllers;
    }
}
package com.wac.autocore.config;

import com.wac.autocore.controller.InvoiceController;
import com.wac.autocore.controller.PaymentController;
import com.wac.autocore.data.*;
import com.wac.autocore.security.*;
import com.wac.autocore.service.*;

public class ManualAppContext implements AppContext {

    private final GarageSystem garageSystem;
    private final Authenticator authenticator;
    private final InvoiceController invoiceController;
    private final PaymentController paymentController;

    public ManualAppContext() {
        VehicleRepo vehicleRepo = new VehicleRepoImpl();
        CustomerRepo customerRepo = new CustomerRepoImpl();
        MechanicRepo mechanicRepo = new MechanicRepoImpl();
        ServiceItemRepo serviceItemRepo = new ServiceItemRepoImpl();
        BookingRepo bookingRepo = new BookingRepoImpl();
        WorkOrderRepo workOrderRepo = new WorkOrderRepoImpl();
        ServicePackRepo servicePackRepo = new ServicePackRepoImpl();

        InvoiceService invoiceService = new InvoiceService(new InvoiceRepoImpl(), workOrderRepo);
        PaymentService paymentService = new PaymentService(new PaymentRepoImpl(), invoiceService);

        AccessGuard accessGuard = new AccessGuard(AppSession.getInstance());
        invoiceController = new InvoiceController(invoiceService, accessGuard);
        paymentController = new PaymentController(paymentService, accessGuard);

        garageSystem = new GarageSystem(
                new CustomerService(customerRepo, vehicleRepo),
                new VehicleService(vehicleRepo, customerRepo, bookingRepo),
                new BookingService(vehicleRepo, bookingRepo),
                new WorkOrderService(bookingRepo, workOrderRepo, mechanicRepo, serviceItemRepo),
                new MechanicService(mechanicRepo, workOrderRepo),
                new ServiceItemService(serviceItemRepo, servicePackRepo),
                invoiceService,
                paymentService);

        authenticator = new DatabaseAuthenticator(new UserRepoImpl(), AppSession.getInstance(), new PasswordHasher());
    }

    @Override
    public GarageSystem garageSystem() {
        return garageSystem;
    }

    @Override
    public InvoiceController invoiceController() {
        return invoiceController;
    }

    @Override
    public PaymentController paymentController() {
        return paymentController;
    }

    @Override
    public Authenticator authenticator() {
        return authenticator;
    }

    @Override
    public CurrentUserProvider currentUserProvider() {
        return AppSession.getInstance();
    }
}
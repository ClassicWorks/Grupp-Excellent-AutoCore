package com.wac.autocore.config;

import com.wac.autocore.data.*;
import com.wac.autocore.security.AppSession;
import com.wac.autocore.security.Authenticator;
import com.wac.autocore.security.DatabaseAuthenticator;
import com.wac.autocore.security.PasswordHasher;
import com.wac.autocore.service.*;

public class ManualAppContext implements AppContext {

    private final GarageSystem garageSystem;
    private final Authenticator authenticator;

    public ManualAppContext() {
        VehicleRepo vehicleRepo = new VehicleRepoImpl();
        CustomerRepo customerRepo = new CustomerRepoImpl();
        MechanicRepo mechanicRepo = new MechanicRepoImpl();
        ServiceItemRepo serviceItemRepo = new ServiceItemRepoImpl();
        BookingRepo bookingRepo = new BookingRepoImpl();
        WorkOrderRepo workOrderRepo = new WorkOrderRepoImpl();
        ServicePackRepo servicePackRepo = new ServicePackRepoImpl();

        InvoiceService invoiceService = new InvoiceService(new InvoiceRepoImpl(), workOrderRepo);

        garageSystem = new GarageSystem(
                new CustomerService(customerRepo, vehicleRepo),
                new VehicleService(vehicleRepo, customerRepo, bookingRepo),
                new BookingService(vehicleRepo, bookingRepo),
                new WorkOrderService(bookingRepo, workOrderRepo, mechanicRepo, serviceItemRepo),
                new MechanicService(mechanicRepo, workOrderRepo),
                new ServiceItemService(serviceItemRepo, servicePackRepo),
                invoiceService,
                new PaymentService(new PaymentRepoImpl(), invoiceService));

        authenticator = new DatabaseAuthenticator(new UserRepoImpl(), AppSession.getInstance(), new PasswordHasher());
    }

    @Override
    public GarageSystem garageSystem() {
        return garageSystem;
    }

    @Override
    public Authenticator authenticator() {
        return authenticator;
    }
}

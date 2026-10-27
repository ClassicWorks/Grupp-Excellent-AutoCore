package com.wac.autocore.config;

import com.wac.autocore.data.*;
import com.wac.autocore.service.*;

public class ManualAppContext implements AppContext {

    private final GarageSystem garageSystem;

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
    }

    @Override
    public GarageSystem garageSystem() {
        return garageSystem;
    }
}

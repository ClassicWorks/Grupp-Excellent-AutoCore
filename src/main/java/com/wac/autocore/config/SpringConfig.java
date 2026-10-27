package com.wac.autocore.config;

import com.wac.autocore.data.*;
import com.wac.autocore.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    VehicleRepo vehicleRepo() { return new VehicleRepoImpl(); }
    @Bean
    CustomerRepo customerRepo() { return new CustomerRepoImpl(); }
    @Bean
    MechanicRepo mechanicRepo() { return new MechanicRepoImpl(); }
    @Bean
    ServiceItemRepo serviceItemRepo() { return new ServiceItemRepoImpl(); }
    @Bean
    BookingRepo bookingRepo() { return new BookingRepoImpl(); }
    @Bean
    WorkOrderRepo workOrderRepo() { return new WorkOrderRepoImpl(); }
    @Bean
    ServicePackRepo servicePackRepo() { return new ServicePackRepoImpl(); }
    @Bean
    InvoiceRepo invoiceRepo() { return new InvoiceRepoImpl(); }
    @Bean PaymentRepo paymentRepo() { return new PaymentRepoImpl(); }

    @Bean
    CustomerService customerService(CustomerRepo customerRepo, VehicleRepo vehicleRepo) {
        return new CustomerService(customerRepo, vehicleRepo);
    }

    @Bean
    VehicleService vehicleService(VehicleRepo vehicleRepo, CustomerRepo customerRepo, BookingRepo bookingRepo) {
        return new VehicleService(vehicleRepo, customerRepo, bookingRepo);
    }

    @Bean
    BookingService bookingService(VehicleRepo vehicleRepo, BookingRepo bookingRepo) {
        return new BookingService(vehicleRepo, bookingRepo);
    }

    @Bean
    WorkOrderService workOrderService(BookingRepo bookingRepo, WorkOrderRepo workOrderRepo,
                                      MechanicRepo mechanicRepo, ServiceItemRepo serviceItemRepo) {
        return new WorkOrderService(bookingRepo, workOrderRepo, mechanicRepo, serviceItemRepo);
    }

    @Bean
    MechanicService mechanicService(MechanicRepo mechanicRepo, WorkOrderRepo workOrderRepo) {
        return new MechanicService(mechanicRepo, workOrderRepo);
    }

    @Bean
    ServiceItemService serviceItemService(ServiceItemRepo serviceItemRepo, ServicePackRepo servicePackRepo) {
        return new ServiceItemService(serviceItemRepo, servicePackRepo);
    }

    @Bean
    InvoiceService invoiceService(InvoiceRepo invoiceRepo, WorkOrderRepo workOrderRepo) {
        return new InvoiceService(invoiceRepo, workOrderRepo);
    }

    @Bean
    PaymentService paymentService(PaymentRepo paymentRepo, InvoiceService invoiceService) {
        return new PaymentService(paymentRepo, invoiceService);
    }

    @Bean
    GarageSystem garageSystem(CustomerService customerService, VehicleService vehicleService,
                              BookingService bookingService, WorkOrderService workOrderService,
                              MechanicService mechanicService, ServiceItemService serviceItemService,
                              InvoiceService invoiceService, PaymentService paymentService) {
        return new GarageSystem(customerService, vehicleService, bookingService, workOrderService,
                mechanicService, serviceItemService, invoiceService, paymentService);
    }
}

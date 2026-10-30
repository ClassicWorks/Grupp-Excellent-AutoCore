package com.wac.autocore.config;

import com.wac.autocore.controller.*;
import com.wac.autocore.data.*;
import com.wac.autocore.security.AccessGuard;
import com.wac.autocore.security.Authenticator;
import com.wac.autocore.security.CurrentUserProvider;
import com.wac.autocore.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Bean UserRepo userRepo() { return new UserRepoImpl(); }
    @Bean CurrentUserProvider currentUserProvider() { return new SpringUserProvider(); }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(new SpringUserDetailsService(userRepo));
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    Authenticator authenticator (AuthenticationManager authenticationManager) {
        return new SpringAuthenticator(authenticationManager);
    }

    @Bean
    CustomerService customerService(CustomerRepo customerRepo, VehicleRepo vehicleRepo, AccessGuard accessGuard) {
        return new CustomerService(customerRepo, vehicleRepo, accessGuard);
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
    ServiceItemService serviceItemService(ServiceItemRepo serviceItemRepo, ServicePackRepo servicePackRepo, AccessGuard accessGuard) {
        return new ServiceItemService(serviceItemRepo, servicePackRepo, accessGuard);
    }

    @Bean
    InvoiceService invoiceService(InvoiceRepo invoiceRepo, WorkOrderRepo workOrderRepo,
                                  AccessGuard accessGuard) {
        return new InvoiceService(invoiceRepo, workOrderRepo, accessGuard);
    }

    @Bean
    PaymentService paymentService(PaymentRepo paymentRepo, InvoiceService invoiceService,
                                  AccessGuard accessGuard) {
        return new PaymentService(paymentRepo, invoiceService, accessGuard);
    }

    @Bean
    GarageSystem garageSystem(CustomerService customerService, VehicleService vehicleService,
                              BookingService bookingService, WorkOrderService workOrderService,
                              MechanicService mechanicService, ServiceItemService serviceItemService,
                              InvoiceService invoiceService, PaymentService paymentService) {
        return new GarageSystem(customerService, vehicleService, bookingService, workOrderService,
                mechanicService, serviceItemService, invoiceService, paymentService);
    }

    @Bean
    AccessGuard accessGuard(CurrentUserProvider currentUserProvider) {
        return new AccessGuard(currentUserProvider);
    }

    @Bean
    ErrorHandler errorHandler() {
        return new ErrorHandler();
    }

    @Bean
    InvoiceController invoiceController(InvoiceService invoiceService, AccessGuard accessGuard,
                                        ErrorHandler errorHandler) {
        return new InvoiceController(invoiceService, accessGuard, errorHandler);
    }

    @Bean
    PaymentController paymentController(PaymentService paymentService, AccessGuard accessGuard,
                                        ErrorHandler errorHandler) {
        return new PaymentController(paymentService, accessGuard, errorHandler);
    }

    @Bean
    LoginController loginController(Authenticator authenticator) {
        return new LoginController(authenticator);
    }

    @Bean
    WorkOrderController workOrderController(WorkOrderService workOrderService, ErrorHandler errorHandler) {
        return new WorkOrderController(workOrderService, errorHandler);
    }

    @Bean
    CustomerController customerController(CustomerService customerService, ErrorHandler errorHandler) {
        return new CustomerController(customerService, errorHandler);
    }

    @Bean
    CatalogController catalogController(ServiceItemService serviceItemService, ErrorHandler errorHandler) {
        return new CatalogController(serviceItemService, errorHandler);
    }

    @Bean
    AppControllers appControllers(WorkOrderController workOrderController,
                                  InvoiceController invoiceController,
                                  PaymentController paymentController,
                                  CustomerController customerController,
                                  CatalogController catalogController,
                                  LoginController loginController) {
        return new AppControllers(workOrderController, invoiceController, paymentController,
                customerController, catalogController, loginController);
    }
}
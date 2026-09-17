package com.wac.autocore.view;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SideNav {
    /*customer
    * Vehicles
    * Bookings
    * Mechanics
    * Services
    * work orders
    * invoices
    * Payments
    * Exit*/



    /**
     * Creates the navigation menu, containing several buttons calling ViewManager to change view.
     *
     * @return a Parent object representing the navigation menu layout.
     */
    public Parent show(){
        //Create navPane
        BorderPane navPane = new BorderPane();
        navPane.setId("navPane");

        //Create all nav buttons
        Button customerBtn = new Button("Kunder");
        Button vehicleBtn = new Button("Fordon");
        Button bookingBtn = new Button("Bokningar");
        Button mechanicsBtn = new Button("Mekaniker");
        Button serviceBtn = new Button("Service");
        Button workOrderBtn = new Button("Ordrar");
        Button invoiceBtn = new Button("Fakturor");
        Button paymentBtn = new Button("Betalningar");
        Button exitBtn = new Button("Avsluta");

        customerBtn.getStyleClass().addAll("nav-btn", "customer");
        vehicleBtn.getStyleClass().addAll("nav-btn", "vehicle");
        bookingBtn.getStyleClass().addAll("nav-btn", "booking");
        mechanicsBtn.getStyleClass().addAll("nav-btn", "mechanics");
        serviceBtn.getStyleClass().addAll("nav-btn", "services");
        workOrderBtn.getStyleClass().addAll("nav-btn", "work-order");
        invoiceBtn.getStyleClass().addAll("nav-btn", "invoice");
        paymentBtn.getStyleClass().addAll("nav-btn", "payment");
        exitBtn.getStyleClass().addAll("nav-btn", "destructive");

        //Calls on ViewManager
        /*customerBtn.setOnAction(e -> ViewManager.showCustomers());
        vehicleBtn.setOnAction(e -> ViewManager.showVehicles());
        bookingBtn.setOnAction(e -> ViewManager.showBookings());
        mechanicsBtn.setOnAction(e -> ViewManager.showMechanics());
        serviceBtn.setOnAction(e -> ViewManager.showServices());
        workOrderBtn.setOnAction(e -> ViewManager.showWorkOrders());
        invoiceBtn.setOnAction(e -> ViewManager.showinvoices());
        paymentBtn.setOnAction(e -> ViewManager.showPayments());
        exitBtn.setOnAction(e -> ViewManager.exit());*/

        //Place all buttons in navPane
        VBox navBox = new VBox(
                customerBtn,
                vehicleBtn,
                bookingBtn,
                mechanicsBtn,
                serviceBtn,
                workOrderBtn,
                invoiceBtn,
                paymentBtn);

        navPane.setCenter(navBox);
        navPane.setBottom(exitBtn);

        return navPane;
    }
}

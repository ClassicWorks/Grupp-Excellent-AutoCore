package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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


    private VBox navBox;
    /**
     * Creates the navigation menu, containing several buttons calling ViewManager to change view.
     *
     * @return a Parent object representing the navigation menu layout.
     */
    public Parent show(){
        //Create navPane
        BorderPane navPane = new BorderPane();
        navPane.getStyleClass().add("side-bar");

        //Create logo
        ImageView logo = new ImageView(new Image("imgs/WCA logo.png", 100,100,true, false));

        VBox logoBox = new VBox(logo);
        logoBox.getStyleClass().add("logo-container");

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

        customerBtn.getStyleClass().addAll("menu-btn", "customer");
        vehicleBtn.getStyleClass().addAll("menu-btn", "vehicle");
        bookingBtn.getStyleClass().addAll("menu-btn", "booking");
        mechanicsBtn.getStyleClass().addAll("menu-btn", "mechanics");
        serviceBtn.getStyleClass().addAll("menu-btn", "services");
        workOrderBtn.getStyleClass().addAll("menu-btn", "work-order");
        invoiceBtn.getStyleClass().addAll("menu-btn", "invoice");
        paymentBtn.getStyleClass().addAll("menu-btn", "payment");
        exitBtn.getStyleClass().addAll("menu-btn", "exit-btn");


        customerBtn.setOnAction(e -> {
            ViewManager.getInstance().showCustomers();
            setIsSelected(customerBtn);

        });
        vehicleBtn.setOnAction(e -> {
            ViewManager.getInstance().showVehicles();
            setIsSelected(vehicleBtn);

        });
        bookingBtn.setOnAction(e -> {
            ViewManager.getInstance().showBookings();
            setIsSelected(bookingBtn);

        });
        mechanicsBtn.setOnAction(e -> {
            ViewManager.getInstance().showMechanics();
            setIsSelected(mechanicsBtn);

        });
        serviceBtn.setOnAction(e -> {
            ViewManager.getInstance().showServices();
            setIsSelected(serviceBtn);

        });
        workOrderBtn.setOnAction(e -> {
            ViewManager.getInstance().showWorkOrders();
            setIsSelected(workOrderBtn);

        });
        invoiceBtn.setOnAction(e -> {
            ViewManager.getInstance().showInvoices();
            setIsSelected(invoiceBtn);

        });
        paymentBtn.setOnAction(e -> {
            ViewManager.getInstance().showPayments();
            setIsSelected(paymentBtn);

        });
        exitBtn.setOnAction(e -> {
            ViewManager.getInstance().exit();
        });

        //Place all buttons in navPane
        navBox = new VBox(
                customerBtn,
                vehicleBtn,
                bookingBtn,
                mechanicsBtn,
                serviceBtn,
                workOrderBtn,
                invoiceBtn,
                paymentBtn);

        VBox exitBox = new VBox(exitBtn);

        navBox.getStyleClass().add("side-nav");
        exitBox.getStyleClass().add("side-nav");

        navPane.setTop(logoBox);
        navPane.setCenter(navBox);
        navPane.setBottom(exitBox);

        return navPane;
    }

    private void setIsSelected(Button button) {
        navBox.getChildrenUnmodifiable().forEach(node -> {
            if (node.getStyleClass().contains("menu-btn")) {
                node.getStyleClass().remove("is-selected");
            }
        });

        button.getStyleClass().add("is-selected");
    }
}

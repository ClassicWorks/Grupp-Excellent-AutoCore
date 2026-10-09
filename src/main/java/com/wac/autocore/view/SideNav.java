package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.util.StylingUtil;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;

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
        Button customerBtn = new Button(LanguageManager.getString("nav.customers"));
        Button vehicleBtn = new Button(LanguageManager.getString("nav.vehicles"));
        Button bookingBtn = new Button(LanguageManager.getString("nav.bookings"));
        Button mechanicsBtn = new Button(LanguageManager.getString("nav.mechanics"));
        Button serviceBtn = new Button(LanguageManager.getString("nav.services"));
        Button workOrderBtn = new Button(LanguageManager.getString("nav.workorders"));
        Button invoiceBtn = new Button(LanguageManager.getString("nav.invoices"));
        Button paymentBtn = new Button(LanguageManager.getString("nav.payments"));
        Button exitBtn = new Button(LanguageManager.getString("nav.exit"));
        Button languageBtn = new Button(LanguageManager.getCurrentLanguage().equals("sv") ? "English" : "Svenska");
        languageBtn.setGraphic(createGlobeIcon());

        customerBtn.getStyleClass().addAll("menu-btn", "customer");
        vehicleBtn.getStyleClass().addAll("menu-btn", "vehicle");
        bookingBtn.getStyleClass().addAll("menu-btn", "booking");
        mechanicsBtn.getStyleClass().addAll("menu-btn", "mechanics");
        serviceBtn.getStyleClass().addAll("menu-btn", "services");
        workOrderBtn.getStyleClass().addAll("menu-btn", "work-order");
        invoiceBtn.getStyleClass().addAll("menu-btn", "invoice");
        paymentBtn.getStyleClass().addAll("menu-btn", "payment");
        exitBtn.getStyleClass().addAll("menu-btn", "exit-btn");
        languageBtn.getStyleClass().add("language-btn");


        customerBtn.setOnAction(e -> {
            ViewManager.getInstance().showCustomers();
            StylingUtil.setSelected(customerBtn, "menu-btn");

        });
        vehicleBtn.setOnAction(e -> {
            ViewManager.getInstance().showVehicles();
            StylingUtil.setSelected(vehicleBtn, "menu-btn");

        });
        bookingBtn.setOnAction(e -> {
            ViewManager.getInstance().showBookings();
            StylingUtil.setSelected(bookingBtn, "menu-btn");

        });
        mechanicsBtn.setOnAction(e -> {
            ViewManager.getInstance().showMechanics();
            StylingUtil.setSelected(mechanicsBtn, "menu-btn");

        });
        serviceBtn.setOnAction(e -> {
            ViewManager.getInstance().showServices();
            StylingUtil.setSelected(serviceBtn, "menu-btn");

        });
        workOrderBtn.setOnAction(e -> {
            ViewManager.getInstance().showWorkOrders();
            StylingUtil.setSelected(workOrderBtn, "menu-btn");

        });
        invoiceBtn.setOnAction(e -> {
            ViewManager.getInstance().showInvoices();
            StylingUtil.setSelected(invoiceBtn, "menu-btn");

        });
        paymentBtn.setOnAction(e -> {
            ViewManager.getInstance().showPayments();
            StylingUtil.setSelected(paymentBtn, "menu-btn");

        });
        exitBtn.setOnAction(e -> {
            ViewManager.getInstance().exit();
        });

        languageBtn.setOnAction(e -> {
            LanguageManager.toggleLanguage();
            ViewManager.getInstance().refreshSideNav();
            ViewManager.getInstance().refreshCurrentView();
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

        VBox exitBox = new VBox(languageBtn, exitBtn);

        navBox.getStyleClass().add("side-nav");
        exitBox.getStyleClass().add("side-nav");

        navPane.setTop(logoBox);
        navPane.setCenter(navBox);
        navPane.setBottom(exitBox);

        return navPane;
    }

    private Group createGlobeIcon() {
        Circle outline = new Circle(12, 12, 10);
        Ellipse meridian = new Ellipse(12, 12, 4.5, 10);
        Line equator = new Line(2, 12, 22, 12);

        outline.getStyleClass().add("globe-icon");
        meridian.getStyleClass().add("globe-icon");
        equator.getStyleClass().add("globe-icon");

        Group globe = new Group(outline, meridian, equator);
        globe.setScaleX(0.75);
        globe.setScaleY(0.75);
        return globe;
    }
}
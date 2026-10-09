package com.wac.autocore.manager;

import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.*;
import com.wac.autocore.view.components.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ViewManager {

    private static ViewManager instance;

    private Stage primaryStage;
    private BorderPane rootLayout;
    private SideNav sideNav;
    private String currentNav;
    private Runnable currentViewRefresher;
    private final GarageSystem garageSystem;

    private ViewManager() {
        garageSystem = new GarageSystem();
    }

    public static synchronized ViewManager getInstance() {
        if (instance == null) {
            instance = new ViewManager();
        }
        return instance;
    }

    public void init(Stage stage) {
        this.primaryStage = stage;

        rootLayout = new BorderPane();
        sideNav = new SideNav();
        rootLayout.setLeft(sideNav.show());

        Scene scene = new Scene(rootLayout, 1300, 1000);
        scene.getStylesheets().add("style/stylesheet.css");

        stage.setTitle("Wigell AutoCore");
        stage.setScene(scene);
        stage.show();
    }

    public void showView(Node view) {
        rootLayout.setCenter(view);
    }

    public void showPopup(Stage popup, String title, Parent content, Runnable onClose) {

        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(primaryStage);
        popup.setTitle(title);

        Scene scene = new Scene(content, 400, 400);
        scene.getStylesheets().add("style/stylesheet.css");
        popup.setScene(scene);

        popup.showAndWait();
        if (onClose != null) {
            onClose.run();
        }
    }

    private void selectNav(String name) {
        currentNav = name;
        if (sideNav != null) {
            sideNav.select(name);
        }
    }


    /*
     * Byt ut placeholder-metoder (Labels) mot riktig view allt eftersom de byggs.
     * Exempel: showView(new CustomerView().show());
     *
     * showView(node) sig ska INTE ändras, den tar emot vilken Node som helst,
     * oavsett om det är en Label eller en färdig view.
     */

    public void showCustomers() {
        currentViewRefresher = this::showCustomers;
        selectNav(SideNav.CUSTOMERS);
        showView(new ShowCustomersView().show());
    }

    public void showVehicles() {
        currentViewRefresher = this::showVehicles;
        selectNav(SideNav.VEHICLES);
        showView(new ShowVehicleView().show());
    }

    public void showBookings() {
        currentViewRefresher = this::showBookings;
        selectNav(SideNav.BOOKINGS);
        showView(new ShowBookingsView().show());
    }

    public void showMechanics() {
        currentViewRefresher = this::showMechanics;
        selectNav(SideNav.MECHANICS);
        showView(new ShowMechanicsView().show());
    }

    public void showServices() {
        currentViewRefresher = this::showServices;
        selectNav(SideNav.SERVICES);
        showView(new ShowServiceItemsView().show());
    }

    public void showWorkOrders() {
        currentViewRefresher = this::showWorkOrders;
        selectNav(SideNav.WORK_ORDERS);
        showView(new ShowWorkOrdersView().show());
    }

    public void showInvoices() {
        currentViewRefresher = this::showInvoices;
        selectNav(SideNav.INVOICES);
        showView(new ShowInvoicesView().show());
    }

    public void showPayments() {
        currentViewRefresher = this::showPayments;
        selectNav(SideNav.PAYMENTS);
        showView(new ShowPaymentsView().show());
    }

    public void refreshSideNav() {
        sideNav = new SideNav();
        rootLayout.setLeft(sideNav.show());
        if (currentNav != null) {
            sideNav.select(currentNav);
        }
    }

    public void refreshCurrentView() {
        if (currentViewRefresher != null) {
            currentViewRefresher.run();
        }
    }

    public void showCreateBooking() {
        Stage popup = new Stage();
        Parent content = new CreateBookingForm(popup, garageSystem).show();
        showPopup(popup, LanguageManager.getString("bookings.create"), content, this::showBookings);
    }

    public void showCreateBooking(int vehicleId) {
        Stage popup = new Stage();
        Parent content = new CreateBookingForm(popup, garageSystem).show(vehicleId);
        showPopup(popup, LanguageManager.getString("bookings.create"), content, null);
    }
    public void showCreateWorkOrderPopup(int bookingId) {
        Stage popup = new Stage();
        Parent content = new CreateWorkOrderForm(popup).show(bookingId);

        showPopup(popup, LanguageManager.getString("workorder.form.title"), content, null);
    }

    public void showCreateWorkOrderPopup(int bookingId, List<Integer> preselectedServiceIds) {
        Stage popup = new Stage();
        Parent content = new CreateWorkOrderForm(popup).show(bookingId, preselectedServiceIds);

        showPopup(popup, LanguageManager.getString("workorder.form.title"), content, this::showWorkOrders);
    }

    public void showEditWorkOrderItemsPopUp(int workOrderId, Runnable onClose) {
        Stage popup = new Stage();
        Parent content = new EditWorkOrderItemsForm(popup, workOrderId, garageSystem).show();

        showPopup(popup,
                String.format(LanguageManager.getString("workorder.edit.title"), workOrderId),
                content,
                onClose);
    }
    public void showCreateVehiclePopup() {
        Stage popup = new Stage();
        Parent content = new CreateVehicleForm(garageSystem, popup).show();
        showPopup(popup, LanguageManager.getString("vehicle.form.title"), content, this::showVehicles);
    }

    public void showCreateVehiclePopup(int customerId) {
        Stage popup = new Stage();
        Parent content = new CreateVehicleForm(garageSystem, popup).show(customerId);
        showPopup(popup, LanguageManager.getString("vehicle.form.title"), content, null);
    }

    public void showCreateCustomerPopup() {
        Stage popup = new Stage();
        Parent content = new CreateCustomer(popup, null).show();

        showPopup(popup, LanguageManager.getString("customer.form.title.create"), content, this::showCustomers);
    }

    public void showProcessPaymentPopup() {
        Stage popup = new Stage();
        Parent content = new ProcessPaymentForm(popup).show();

        showPopup(popup, LanguageManager.getString("payment.form.title"), content, this::showPayments);
    }

    public void showCreateInvoicePopup() {
        Stage popup = new Stage();
        Parent content = new CreateInvoiceForm(popup).show();

        showPopup(popup, LanguageManager.getString("invoice.form.title"), content, this::showInvoices);
    }

    public void showInvoiceDetailsPopup(int invoiceId) {
        Stage popup = new Stage();
        Parent content = new InvoiceDetailsPopup(popup, invoiceId).show();

        showPopup(popup,
                String.format(LanguageManager.getString("invoice.id"), invoiceId),
                content,
                null);
    }
    public void showCreateServicePackPopup(){
        Stage popup = new Stage();
        Parent content = new CreateServicePackForm(garageSystem, popup).show();

        showPopup(popup, LanguageManager.getString("servicePack.form.title"), content, this::showServices);

    }

    public void exit() {
        primaryStage.close();
    }
}
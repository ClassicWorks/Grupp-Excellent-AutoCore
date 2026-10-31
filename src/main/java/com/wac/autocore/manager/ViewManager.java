package com.wac.autocore.manager;

import com.wac.autocore.controller.AppControllers;
import com.wac.autocore.security.AccessGuard;
import com.wac.autocore.security.CurrentUserProvider;
import com.wac.autocore.security.Permission;
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
    private GarageSystem garageSystem;

    private CurrentUserProvider currentUserProvider;
    private AccessGuard accessGuard;
    private AppControllers controllers;

    private ViewManager() {

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

        Scene scene = new Scene(rootLayout, 1300, 1000);
        scene.getStylesheets().add("style/stylesheet.css");

        stage.setTitle("Wigell AutoCore");
        stage.setScene(scene);
        stage.show();
    }

    public GarageSystem getGarageSystem() {
        return garageSystem;
    }

    public void setGarageSystem(GarageSystem garageSystem) {
        this.garageSystem = garageSystem;
    }

    public AppControllers getControllers() {
        return controllers;
    }

    public void setControllers(AppControllers controllers) {
        this.controllers = controllers;
    }

    public void setCurrentUserProvider(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    public void setAccessGuard(AccessGuard accessGuard){
        this.accessGuard = accessGuard;
    }

    public AccessGuard getAccessGuard(){
        return accessGuard;
    }

    public void showView(Node view) {
        rootLayout.setCenter(view);
    }

    public void showLogin() {
        rootLayout.setLeft(null);
        showView(new LoginView(controllers.login(), this::onLoggedIn).show());
    }


    private void onLoggedIn() {
        refreshSideNav();
        showVehicles();
    }

    public void logout() {
        controllers.login().logout();
        currentViewRefresher = null;
        currentNav = null;
        showLogin();
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
        sideNav = new SideNav(currentUserProvider);
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
        accessGuard.require(Permission.MANAGE_WORK_ORDERS, "ViewManager.showCreateBooking");
        Stage popup = new Stage();
        Parent content = new CreateBookingForm(popup, garageSystem).show();
        showPopup(popup, LanguageManager.getString("bookings.create"), content, this::refreshCurrentView);
    }

    public void showCreateBooking(int vehicleId) {
        accessGuard.require(Permission.MANAGE_WORK_ORDERS, "ViewManager.showCreateBooking(vehicleId)");
        Stage popup = new Stage();
        Parent content = new CreateBookingForm(popup, garageSystem).show(vehicleId);
        showPopup(popup, LanguageManager.getString("bookings.create"), content, this::refreshCurrentView);
    }

    public void showCreateBooking(int vehicleId, List<Integer> preselectedServiceIds) {
        accessGuard.require(Permission.MANAGE_WORK_ORDERS, "ViewManager.showCreateBooking(vehicleId, serviceIds)");
        Stage popup = new Stage();
        Parent content = new CreateBookingForm(popup, garageSystem).show(vehicleId, preselectedServiceIds);
        showPopup(popup, LanguageManager.getString("bookings.create"), content, this::refreshCurrentView);
    }

    public void showEditWorkOrderItemsPopUp(int workOrderId, Runnable onClose) {
        accessGuard.require(Permission.MANAGE_WORK_ORDERS, "ViewManager.showEditWorkOrderItemsPopUp");
        Stage popup = new Stage();
        Parent content = new EditWorkOrderItemsForm(popup, workOrderId, garageSystem).show();

        showPopup(popup,
                String.format(LanguageManager.getString("workorder.edit.title"), workOrderId),
                content,
                onClose);
    }

    public void showCreateVehiclePopup() {
        accessGuard.require(Permission.MANAGE_VEHICLES, "ViewManager.showCreateVehiclePopup");
        Stage popup = new Stage();
        Parent content = new CreateVehicleForm(garageSystem, popup).show();
        showPopup(popup, LanguageManager.getString("vehicle.form.title"), content, this::showVehicles);
    }

    public void showCreateVehiclePopup(int customerId) {
        accessGuard.require(Permission.MANAGE_VEHICLES, "ViewManager.showCreateVehiclePopup(customerId)");
        Stage popup = new Stage();
        Parent content = new CreateVehicleForm(garageSystem, popup).show(customerId);
        showPopup(popup, LanguageManager.getString("vehicle.form.title"), content, null);
    }

    public void showCreateCustomerPopup() {
        accessGuard.require(Permission.MANAGE_CUSTOMERS, "ViewManager.showCreateCustomerPopup");
        Stage popup = new Stage();
        Parent content = new CreateCustomer(popup, null).show();

        showPopup(popup, LanguageManager.getString("customer.form.title.create"), content, this::showCustomers);
    }

    public void showProcessPaymentPopup() {
        accessGuard.require(Permission.MANAGE_PAYMENTS, "ViewManager.showProcessPaymentPopup");
        Stage popup = new Stage();
        Parent content = new ProcessPaymentForm(popup).show();

        showPopup(popup, LanguageManager.getString("payment.form.title"), content, this::showPayments);
    }

    public void showCreateInvoicePopup() {
        accessGuard.require(Permission.MANAGE_INVOICES, "ViewManager.showCreateInvoicePopup");
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
        Parent content = new CreateServicePackForm(popup).show();

        showPopup(popup, LanguageManager.getString("servicePack.form.title"), content, this::showServices);

    }

    public void exit() {
        primaryStage.close();
    }
}
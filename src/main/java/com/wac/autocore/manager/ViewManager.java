package com.wac.autocore.manager;

import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.*;
import com.wac.autocore.view.components.CreateBookingForm;
import com.wac.autocore.view.components.CreateInvoiceForm;
import com.wac.autocore.view.components.CreateVehicleForm;
import com.wac.autocore.view.components.CreateWorkOrderForm;
import com.wac.autocore.view.components.CreateCustomer;
import com.wac.autocore.view.components.ProcessPaymentForm;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ViewManager {

    private static ViewManager instance;

    private Stage primaryStage;
    private BorderPane rootLayout;
    private Runnable currentViewRefresher;

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
        rootLayout.setLeft(new SideNav().show());

        Scene scene = new Scene(rootLayout, 900, 600);

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
        popup.setScene(scene);

        popup.showAndWait();
        if (onClose != null) {
            onClose.run();
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
        showView(new ShowCustomersView().show());
    }

    public void showVehicles() {
        currentViewRefresher = this::showVehicles;
        showView(new ShowVehicleView().show());
    }

    public void showBookings() {
        currentViewRefresher = this::showBookings;
        showView(new ShowBookingsView().show());
    }

    public void showMechanics() {
        currentViewRefresher = this::showMechanics;
        showView(new ShowMechanicsView().show());
    }

    public void showServices() {
        currentViewRefresher = this::showServices;
        showView(new ShowServiceItemsView().show());
    }

    public void showWorkOrders() {
        currentViewRefresher = this::showWorkOrders;
        showView(new ShowWorkOrdersView().show());
    }

    public void showInvoices() {
        currentViewRefresher = this::showInvoices;
        showView(new ShowInvoicesView().show());
    }

    public void showPayments() {
        currentViewRefresher = this::showPayments;
        showView(new ShowPaymentsView().show());
    }

    public void refreshSideNav() {
        rootLayout.setLeft(new SideNav().show());
    }

    public void refreshCurrentView() {
        if (currentViewRefresher != null) {
            currentViewRefresher.run();
        }
    }

    public void showCreateBooking() {
        Stage popup = new Stage();
        Parent content = new CreateBookingForm(popup).show();
        showPopup(popup, LanguageManager.getString("bookings.create"), content, this::showBookings);
    }

    public void showCreateBooking(int vehicleId) {
        Stage popup = new Stage();
        Parent content = new CreateBookingForm(popup).show(vehicleId);
        showPopup(popup, LanguageManager.getString("bookings.create"), content, null);
    }
    public void showCreateWorkOrderPopup(int bookingId) {
        Stage popup = new Stage();
        Parent content = new CreateWorkOrderForm(popup).show(bookingId);

        showPopup(popup, LanguageManager.getString("workorder.form.title"), content, this::showWorkOrders);
    }
    public void showCreateVehiclePopup() {
        Stage popup = new Stage();
        Parent content = new CreateVehicleForm(popup).show();
        showPopup(popup, LanguageManager.getString("vehicle.form.title"), content, this::showVehicles);
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

    public void exit() {
        primaryStage.close();
    }
}
package com.wac.autocore.manager;

import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.CreateBookingView;
import com.wac.autocore.view.ShowBookingsView;
import com.wac.autocore.view.ShowVehicleView;
import com.wac.autocore.view.SideNav;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ViewManager {

    private static ViewManager instance;
    private GarageSystem garageSystem;

    private Stage primaryStage;
    private BorderPane rootLayout;

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
        rootLayout.setLeft(new SideNav().show());

        Scene scene = new Scene(rootLayout, 900, 600);

        stage.setTitle("Wigell AutoCore");
        stage.setScene(scene);
        stage.show();
    }

    public void showView(Node view) {
        rootLayout.setCenter(view);
    }

    public void showNewWindow(Parent view){
        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);

        Scene scene = new Scene(view);

        stage.setScene(scene);
        stage.showAndWait();
    }


    /*
     * Byt ut placeholder-metoder (Labels) mot riktig view allt eftersom de byggs.
     * Exempel: showView(new CustomerView().show());
     *
     * showView(node) sig ska INTE ändras, den tar emot vilken Node som helst,
     * oavsett om det är en Label eller en färdig view.
     */

    public void showCustomers() {
        showView(new Label("Customers - placeholder"));
    }

    public void showVehicles() {
        showView(new ShowVehicleView().show());
    }

    public void showBookings() {
        showView(new ShowBookingsView(garageSystem).show());
    }

    public void showMechanics() {
        showView(new Label("Mechanics - placeholder"));
    }

    public void showServices() {
        showView(new Label("Services - placeholder"));
    }

    public void showWorkOrders() {
        showView(new Label("Work orders - placeholder"));
    }

    public void showInvoices() {
        showView(new Label("Invoices - placeholder"));
    }

    public void showPayments() {
        showView(new Label("Payments - placeholder"));
    }

    public void showCreateBooking(){showNewWindow(
            new CreateBookingView(garageSystem).show());
    }

    public void exit() {
        primaryStage.close();
    }
}
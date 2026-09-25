package com.wac.autocore.manager;

import com.wac.autocore.view.ShowMechanicsView;
import com.wac.autocore.view.ShowBookingsView;
import com.wac.autocore.view.ShowPaymentsView;
import com.wac.autocore.view.ShowVehicleView;
import com.wac.autocore.view.SideNav;
import com.wac.autocore.view.components.CreateInvoiceForm;
import com.wac.autocore.view.components.CreateVehicleForm;
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
        showView(new Label("Customers - placeholder"));
    }

    public void showVehicles() {
        showView(new ShowVehicleView().show());
    }

    public void showBookings() {
        showView(new ShowBookingsView().show());
    }

    public void showMechanics() {
        showView(new ShowMechanicsView().show());
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
        showView(new ShowPaymentsView().show());
    }

    public void showCreateBooking() {
        //TODO change to new method
  /*showNewWindow(
            new CreateBookingForm().show());*/
    } // la till detta

        public void showCreateVehiclePopup() {
            Stage popup = new Stage();
            Parent content = new CreateVehicleForm(popup).show();

            showPopup(popup, "Create new vehicle", content, this::showVehicles);
        }

        public void showCreateCustomerPopup() {
            Stage popup = new Stage();
            Parent content = new CreateCustomer(popup, null).show();

            showPopup(popup, "Skapa ny kund", content, this::showCustomers);
        }

         public void showProcessPaymentPopup() {
            Stage popup = new Stage();
            Parent content = new ProcessPaymentForm(popup).show();

            showPopup(popup, "Process payment", content, this::showPayments);
        }

        public void showCreateInvoicePopup() {
            Stage popup = new Stage();
            Parent content = new CreateInvoiceForm(popup).show();

            showPopup(popup, "Create invoice", content, this::showInvoices);
        }

        public void exit() {
            primaryStage.close();
        }
    }
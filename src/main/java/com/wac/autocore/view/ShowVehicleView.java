package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.components.KanbanGridUtil;
import com.wac.autocore.view.components.VehicleCard;
import com.wac.autocore.view.components.VehicleDetails;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ShowVehicleView {
    private final GarageSystem garageSystem;

    public ShowVehicleView() {
        garageSystem = new GarageSystem();
    }

    public Parent show(){
        BorderPane layout = new BorderPane();
        layout.setTop(getHeader());

        //Split Stage 50/50
        GridPane mainContent = KanbanGridUtil.getKanbanGrid(2);

        //Filtrerings nodes
        //TODO Just nu finns ingen logik för filtrering, är det något som ska implementeras? Vilka typer?
        /*ObservableList<String> sortings = FXCollections.observableArrayList(
                "A-Ö", "Ö-A", "Bokade", "Ej Bokade"
        );
        ComboBox<String> sortingComboBox = new ComboBox<>(sortings);
        sortingComboBox.setPromptText("Sortera");
        TextField searchBar = new TextField();
        searchBar.setPromptText("Sökord");
        HBox filterBox = new HBox(sortingComboBox, searchBar);*/

        //List all cards for vehicles
        VBox vehiclesBox = new VBox();

        //Show details of car

        //TODO listan ska kunna uppdateras baserat på filtering
        for (Vehicle vehicle : garageSystem.getVehicles()){
            Customer customer = vehicle.getCustomer();
            VehicleCard vehicleCard = new VehicleCard(vehicle, customer);
            vehicleCard.setOnMouseClicked(e-> {

                VehicleDetails  vehicleDetails = new VehicleDetails(vehicle, customer);
                vehicleDetails.setMaxHeight(Double.MAX_VALUE);

                //If anything is in right column, remove content
                mainContent.getChildren().removeIf(node ->
                        GridPane.getColumnIndex(node) != null
                                && GridPane.getColumnIndex(node) == 1);

                mainContent.add(
                        vehicleDetails,
                        1, 0
                );
                //Make vehicleDetail as big as allowed (vehicleScroll has already from KanbanGridUtil)
                GridPane.setVgrow(vehicleDetails, Priority.ALWAYS);
            });
            vehiclesBox.getChildren().add(vehicleCard);
        }

        VBox vehiclesScroll = KanbanGridUtil.getScrollableColumnWithTitle("", vehiclesBox);

        mainContent.add(
                vehiclesScroll,
                0, 0);

        layout.setCenter(mainContent);
        return layout;
    }

    private VehicleDetails createVehicleDetails(Vehicle vehicle, Customer customer) {
        return null;
    }

    private Node getHeader(){
        BorderPane headerPane = new BorderPane();
        Label title = new Label("Vehicles");
        title.getStyleClass().setAll("page-title");
        Button createBookingBtn = new Button("Create new vehicle");
        createBookingBtn.getStyleClass().addAll("create-btn");
        createBookingBtn.setOnAction(e -> ViewManager.getInstance().showCreateVehiclePopup());
        headerPane.setCenter(title);
        headerPane.setRight(createBookingBtn);
        return headerPane;
    }
}

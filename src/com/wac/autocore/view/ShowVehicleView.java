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
    private final VehicleDetails vehicleDetails = new VehicleDetails();

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
        vehicleDetails.setMaxHeight(Double.MAX_VALUE);

        //TODO listan ska kunna uppdateras baserat på filtering
        for (Vehicle vehicle : garageSystem.getVehicles()){
            Customer customer = garageSystem.getCustomer(vehicle.getCustomerId()).orElse(null);
            VehicleCard vehicleCard = new VehicleCard(vehicle, customer);
            vehicleCard.setOnMouseClicked(e-> vehicleDetails.populate(vehicle, customer));
            vehiclesBox.getChildren().add(vehicleCard);
        }

        VBox vehiclesScroll = KanbanGridUtil.getScrollableColumnWithTitle("", vehiclesBox);

        mainContent.add(
                vehiclesScroll,
                0, 0);
        mainContent.add(
                vehicleDetails,
                1, 0);

        //Make vehicleDetail as big as allowed (vehicleScroll has already from KanbanGridUtil)
        GridPane.setVgrow(vehicleDetails, Priority.ALWAYS);

        layout.setCenter(mainContent);
        return layout;
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

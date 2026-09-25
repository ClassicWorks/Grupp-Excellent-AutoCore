package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.components.VehicleCard;
import com.wac.autocore.view.components.VehicleDetails;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ShowVehicleView {
    private GarageSystem garageSystem;

    public ShowVehicleView() {
        garageSystem = new GarageSystem();
    }

    public Parent show(){
        VBox layout = new VBox();
        layout.getChildren().add(getHeader());

        //Split Stage 50/50
        GridPane mainContent = new GridPane();
        ColumnConstraints left = new ColumnConstraints();
        left.setPercentWidth(50);
        ColumnConstraints right = new ColumnConstraints();
        right.setPercentWidth(50);
        mainContent.getColumnConstraints().addAll(left, right);

        //Filtrerings nodes
        //TODO Just nu finns ingen logik för filtrering, är det något som ska implementeras? Vilka typer?
        ObservableList<String> sortings = FXCollections.observableArrayList(
                "A-Ö", "Ö-A", "Bokade", "Ej Bokade"
        );
        ComboBox<String> sortingComboBox = new ComboBox<>(sortings);
        sortingComboBox.setPromptText("Sortera");
        TextField searchBar = new TextField();
        searchBar.setPromptText("Sökord");
        HBox filterBox = new HBox(sortingComboBox, searchBar);
        mainContent.getChildren().add(filterBox);

        //List all cards for vehicles
        VBox vehiclesBox = new VBox();
        ScrollPane vehiclesBoxScroll = new ScrollPane(vehiclesBox);
        vehiclesBoxScroll.setMaxHeight(Double.MAX_VALUE);

        //Show details of car
        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMaxHeight(Double.MAX_VALUE);

        //TODO listan ska kunna uppdateras baserat på filtering
        for (Vehicle vehicle : garageSystem.getVehicles()){
            Customer customer = garageSystem.getCustomer(vehicle.getCustomerId()).orElse(null);
            VehicleCard vehicleCard = new VehicleCard(vehicle, customer);
            vehicleCard.setOnMouseClicked(e-> vehicleDetails.populate(vehicle, customer));
            vehiclesBox.getChildren().add(vehicleCard);
        }

        //Make them as big as allowed
        mainContent.add(vehiclesBoxScroll, 0, 0);
        mainContent.add(vehicleDetails, 1, 0);
        GridPane.setVgrow(vehiclesBox, Priority.ALWAYS);
        GridPane.setVgrow(vehicleDetails, Priority.ALWAYS);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        VBox.setVgrow(vehiclesBoxScroll, Priority.ALWAYS);

        layout.getChildren().addAll(mainContent);
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

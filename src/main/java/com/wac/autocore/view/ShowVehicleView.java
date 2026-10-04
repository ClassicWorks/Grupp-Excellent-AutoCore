package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.StylingUtil;
import com.wac.autocore.view.components.kanban.KanbanGrid;
import com.wac.autocore.util.LanguageManager;
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
        Node header = getHeader();
        header.getStyleClass().add("content-header-container");
        layout.setTop(header);

        //Split Stage 50/50
        GridPane mainContent = new KanbanGrid(2);

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
        vehiclesBox.getStyleClass().add("card-container");

        VBox detailsContainer = new VBox();

        //Show details of car
        for (Vehicle vehicle : garageSystem.getVehicles()){
            Customer customer = vehicle.getCustomer();
            VehicleCard vehicleCard = new VehicleCard(vehicle, customer,
                    v -> ViewManager.getInstance().showCreateBooking(v.getId())
            );

            vehicleCard.setOnMouseClicked(e-> {
                VehicleDetails vehicleDetails = new VehicleDetails(vehicle, customer);
                detailsContainer.getChildren().setAll(vehicleDetails);
                VBox.setVgrow(vehicleDetails, Priority.ALWAYS);

                StylingUtil.setSelected(vehicleCard, "card");
            });
            vehicleCard.getStyleClass().add("clickable");
            vehiclesBox.getChildren().add(vehicleCard);
        }

        ScrollPane vehiclesScroll = new ScrollPane(vehiclesBox);
        vehiclesScroll.setFitToWidth(true);
        vehiclesScroll.setFitToHeight(true);

        GridPane.setVgrow(vehiclesScroll, Priority.ALWAYS);
        GridPane.setVgrow(detailsContainer, Priority.ALWAYS);

        mainContent.add(
                vehiclesScroll,
                0, 0);
        mainContent.add(
                detailsContainer,
                1, 0);

        layout.setCenter(mainContent);
        return layout;
    }

    private Node getHeader(){
        BorderPane headerPane = new BorderPane();
        Label title = new Label(LanguageManager.getString("vehicles.title"));
        title.getStyleClass().setAll("page-title");
        Button createBookingBtn = new Button(LanguageManager.getString("vehicles.create"));
        createBookingBtn.getStyleClass().addAll("confirm-btn");
        createBookingBtn.setOnAction(e -> ViewManager.getInstance().showCreateVehiclePopup());

        headerPane.setCenter(title);
        headerPane.setRight(createBookingBtn);
        return headerPane;
    }
}
package com.wac.autocore.view;

import com.wac.autocore.data.Database;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.view.components.VehicleCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class ShowVehicleView {

    public Parent show(){
        VBox vBox = new VBox();

        //Skapa filtrerings nodes
        ObservableList<String> sortings = FXCollections.observableArrayList(
                "A-Ö", "Ö-A", "Bokade", "Ej Bokade"
        );
        ComboBox<String> sortingComboBox = new ComboBox<>(sortings);
        sortingComboBox.setPromptText("Sortera");
        TextField searchBar = new TextField();
        searchBar.setPromptText("Sökord");
        HBox filterBox = new HBox(sortingComboBox, searchBar);
        vBox.getChildren().add(filterBox);

        //Skapa alla cards för vehicles
        VBox vehiclesBox = new VBox();
        //TODO listan ska kunna uppdateras baserat på filtering
        List<Vehicle> vehicles = Database.getVehicles();
        for (Vehicle vehicle : vehicles){
            Node vehicleCard = VehicleCard.getCard(vehicle);
            vehiclesBox.getChildren().add(vehicleCard);
        }

        //TODO lägg till knappar för att lägga till bil

        vBox.getChildren().add(vehiclesBox);
        return vBox;
    }
}

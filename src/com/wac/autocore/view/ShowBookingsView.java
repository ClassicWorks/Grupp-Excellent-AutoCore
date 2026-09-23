package com.wac.autocore.view;

import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.components.BookingCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.awt.print.Book;


public class ShowBookingsView {
    private final GarageSystem garageSystem;

    public ShowBookingsView(GarageSystem garageSystem) {
        this.garageSystem = garageSystem;
    }

    public Parent show(){
        VBox layout = new VBox();
        layout.getChildren().add(getHeader());

        GridPane mainContent = new GridPane();
        ColumnConstraints left = new ColumnConstraints();
        left.setPercentWidth(50);
        ColumnConstraints right = new ColumnConstraints();
        right.setPercentWidth(50);
        mainContent.getColumnConstraints().addAll(left, right);

        //Filtrerings nodes
        //TODO Just nu finns ingen logik för filtrering, är det något som ska implementeras? Vilka typer?
        ObservableList<String> sortOrderList = FXCollections.observableArrayList(
                "A-Ö", "Ö-A"
        );
        ComboBox<String> sortingComboBox = new ComboBox<>(sortOrderList);
        sortingComboBox.setPromptText("Sortera");
        TextField searchBar = new TextField();
        searchBar.setPromptText("Sökord");
        HBox filterBox = new HBox(sortingComboBox, searchBar);

        //Show alla cards of bookings
        VBox bookingsBox = new VBox();

        ScrollPane bookingsBoxScroll = new ScrollPane(bookingsBox);

        bookingsBoxScroll.setPrefHeight(200);
        bookingsBoxScroll.setPrefViewportWidth(400);

        for(Booking booking : garageSystem.getBookings()){
            if(garageSystem.getVehicle(booking.getVehicleId()).isPresent()) {
                bookingsBox.getChildren().add(BookingCard.getCard(
                        booking,
                        garageSystem.getVehicle(booking.getVehicleId()).get(),
                        null));
            }
            else {
               bookingsBox.getChildren().add(new Label(String.format("Booking faulty: %d", booking.getId())));
            }
        }


        VBox listBookingsBox = new VBox(filterBox, bookingsBoxScroll);
        listBookingsBox.setStyle("-fx-background-color: #a33c3c;");
        VBox bookingInfoBox = new VBox();
        bookingInfoBox.setStyle("-fx-background-color: #000000;");
        mainContent.add(listBookingsBox, 0, 0);
        mainContent.add(bookingInfoBox, 1, 0);

        layout.getChildren().add(mainContent);

        return layout;
    }

    private Node getHeader(){
        BorderPane headerPane = new BorderPane();
        Label title = new Label("Bookings");
        title.getStyleClass().setAll("page-title");
        Button createBookingBtn = new Button("Create new booking");
        createBookingBtn.getStyleClass().addAll("create-btn");
        createBookingBtn.setOnAction(e -> System.out.println("Should call on ViewManager.getInstance().showCreateBooking()"));
        headerPane.setCenter(title);
        headerPane.setRight(createBookingBtn);
        return headerPane;
    }
}

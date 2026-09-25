package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Customer;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.view.components.BookingCard;
import com.wac.autocore.view.components.BookingDetails;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;



public class ShowBookingsView {
    private final GarageSystem garageSystem;

    public ShowBookingsView() {
        this.garageSystem = new GarageSystem();
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

        //more information block
        BookingDetails bookingDetails = new BookingDetails();

        //Filtrerings nodes
        //TODO Just nu finns ingen logik för filtrering, är det något som ska implementeras? Vilka typer?
        ObservableList<String> sortOrderList = FXCollections.observableArrayList(
                "A-Ö", "Ö-A"
        );
        ComboBox<String> sortingComboBox = new ComboBox<>(sortOrderList);
        sortingComboBox.setPromptText("Sortera");
        //TODO If you want to see all types of bookings. Should then not be Combobox, maybe checkboxes?
        /*ObservableList<String> filterList = FXCollections.observableArrayList(
                "Booked", "In progress","Awaiting invoice", "Awaiting payment", "Payed"
        );
        ComboBox<String> filterComboBox = new ComboBox<>(filterList);
        filterComboBox.setPromptText("Sortera");*/
        HBox filterBox = new HBox(sortingComboBox);

        //Show alla cards of bookings
        VBox bookingsBox = new VBox();

        ScrollPane bookingsScroll = new ScrollPane(bookingsBox);
        bookingsScroll.setFitToWidth(true);

        //Take as much space as possible
        bookingsScroll.setMaxHeight(Double.MAX_VALUE);
        bookingDetails.setMaxHeight(Double.MAX_VALUE);

        //Populate list
        for(Booking booking : garageSystem.getBookings()){
            try{
                //Only show bookings waiting on work order
                if(!booking.getStatus().equalsIgnoreCase("BOOKED")) {
                    continue;
                }
                Vehicle vehicle = garageSystem.getVehicle(booking.getVehicleId())
                        .orElseThrow(() -> new NullPointerException(String.format("No vehicle with id %d found.", booking.getVehicleId())));
                Customer customer = garageSystem.getCustomer(vehicle.getCustomerId())
                        .orElseThrow(() -> new NullPointerException(String.format("No customer with id %d found.", vehicle.getCustomerId())));
                Mechanic mechanic = garageSystem.getMechanic(booking.getMechanicId()).orElse(null);

                BookingCard bookingCard = new BookingCard(
                        booking,
                        vehicle,
                        mechanic);
                bookingCard.setOnMouseClicked(e -> bookingDetails.populate(
                        booking,
                        customer,
                        vehicle,
                        mechanic
                        ));
                bookingsBox.getChildren().add(bookingCard);
            } catch (Exception e){
                Label errorMessage = new Label(String.format("Booking faulty. booking ID: %d, error: %s",
                    booking.getId(), e.getMessage()));
                bookingsBox.getChildren().add(errorMessage);
            }
        }

        VBox listBookingsBox = new VBox(filterBox, bookingsScroll);

        //Make nodes possible to fill entire view
        GridPane.setVgrow(listBookingsBox, Priority.ALWAYS);
        GridPane.setVgrow(bookingDetails, Priority.ALWAYS);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        VBox.setVgrow(bookingsScroll, Priority.ALWAYS);

        mainContent.add(listBookingsBox, 0, 0);
        mainContent.add(bookingDetails, 1, 0);

        layout.getChildren().add(mainContent);
        return layout;
    }

    private Node getHeader(){
        BorderPane headerPane = new BorderPane();
        Label title = new Label("Bookings");
        title.getStyleClass().setAll("page-title");
        Button createBookingBtn = new Button("Create new booking");
        createBookingBtn.getStyleClass().addAll("create-btn");
        createBookingBtn.setOnAction(e -> ViewManager.getInstance().showCreateBooking());
        headerPane.setCenter(title);
        headerPane.setRight(createBookingBtn);
        return headerPane;
    }
}

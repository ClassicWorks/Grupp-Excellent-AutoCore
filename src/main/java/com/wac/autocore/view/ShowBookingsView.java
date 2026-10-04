package com.wac.autocore.view;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Booking;
import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.service.GarageSystem;
import com.wac.autocore.util.StylingUtil;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.BookingCard;
import com.wac.autocore.view.components.BookingDetails;
import com.wac.autocore.view.components.kanban.KanbanGrid;
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
        Node header = getHeader();
        header.getStyleClass().add("content-header-container");
        layout.getChildren().add(header);

        KanbanGrid mainContent = new KanbanGrid(2);

        //Filtrerings nodes
        //TODO Just nu finns ingen logik för filtrering, är det något som ska implementeras? Vilka typer?
        /*ObservableList<String> sortOrderList = FXCollections.observableArrayList(
                "A-Ö", "Ö-A"
        );
        ComboBox<String> sortingComboBox = new ComboBox<>(sortOrderList);
        sortingComboBox.setPromptText("Sortera");
        //TODO If you want to see all types of bookings. Should then not be Combobox, maybe checkboxes?
        ObservableList<String> filterList = FXCollections.observableArrayList(
                "Booked", "In progress","Awaiting invoice", "Awaiting payment", "Payed"
        );
        ComboBox<String> filterComboBox = new ComboBox<>(filterList);
        filterComboBox.setPromptText("Sortera");
        HBox filterBox = new HBox(sortingComboBox);*/

        //Show alla cards of bookings
        VBox bookingsBox = new VBox();
        bookingsBox.getStyleClass().add("card-container");

        //Populate list
        for(Booking booking : garageSystem.getBookings()){
            try{
                //Only show bookings waiting on work order
                if(!booking.getStatus().equalsIgnoreCase("BOOKED")) {
                    continue;
                }

                Vehicle vehicle = booking.getVehicle();
                if(vehicle == null){
                    throw new NullPointerException(LanguageManager.getString("booking.error.noVehicle"));
                }

                Mechanic mechanic = booking.getMechanic();

                //TODO Include consumer in constructor
                BookingCard bookingCard = new BookingCard(
                        booking,
                        vehicle,
                        mechanic);
                bookingCard.getStyleClass().add("clickable");

                bookingCard.setOnMouseClicked(e -> {
                    //If anything is in right column, remove content
                    mainContent.getChildren().removeIf(node ->
                            GridPane.getColumnIndex(node) != null
                    && GridPane.getColumnIndex(node) == 1);
                    //Add bookingDetails
                    BookingDetails bookingDetails = new BookingDetails(
                            booking,
                            garageSystem.getAvailableMechanics(),
                            this::saveBooking,
                            this::deleteBooking
                    );

                    mainContent.add(
                            bookingDetails,
                            1,0
                    );

                    bookingDetails.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setVgrow(bookingDetails, Priority.ALWAYS);

                    StylingUtil.setSelected(bookingCard, "card");
                });
                bookingsBox.getChildren().add(bookingCard);
            } catch (Exception e){
                Label errorMessage = new Label(String.format(LanguageManager.getString("booking.error.faulty"),
                        booking.getId(), e.getMessage()));
                bookingsBox.getChildren().add(errorMessage);
            }
        }

        ScrollPane listBookingsBox = new ScrollPane(bookingsBox);
        listBookingsBox.setFitToWidth(true);
        listBookingsBox.setFitToHeight(true);

        //Make nodes possible to fill entire view
        GridPane.setVgrow(listBookingsBox, Priority.ALWAYS);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        mainContent.add(listBookingsBox, 0, 0);

        layout.getChildren().add(mainContent);
        return layout;
    }

    private Node getHeader(){
        Label title = new Label(LanguageManager.getString("bookings.title"));
        title.getStyleClass().setAll("page-title");

        Button createBookingBtn = new Button(LanguageManager.getString("bookings.create"));
        createBookingBtn.getStyleClass().addAll("confirm-btn");
        createBookingBtn.setOnAction(e -> ViewManager.getInstance().showCreateBooking());

        BorderPane headerPane = new BorderPane();
        headerPane.setCenter(title);
        headerPane.setRight(createBookingBtn);
        return headerPane;
    }

    private Booking saveBooking(Booking updatedBooking){
        System.out.printf("Should call ViewManager.getInstance.saveBooking(%d, %s)\n", updatedBooking.getId(), updatedBooking);
        //garageSystem.updateBooking(updatedBooking.getId(), updatedBooking);
        return updatedBooking;
    }

    private void deleteBooking(Booking booking){
        System.out.printf("Should call ViewManager.getInstance.confirmDelete(vehicle, garagesystem.deleteBooking(%d)\n",
                booking.getId());
        //garageSystem.deleteBooking(booking.getId());
    }
}
package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;

public class CustomerHyperLink extends Hyperlink {
    private Customer customer;

    public CustomerHyperLink(Customer customer){
        this.customer = customer;

        //TODO change to component
        ImageView imageView = new ImageView("resources/imgs/user-solid.png");
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        this.setGraphic(imageView);

        if (customer == null){
            setText("No customer assigned");
            setDisable(true);
        } else {
            setText(customer.getName());
            setOnAction(e ->
                    ViewManager.getInstance().showCustomers());
        }
    }

    public CustomerHyperLink(){
        setText("No customer assigned");
        setDisable(true);
    }

    public void setCustomer(Customer customer){
        this.customer = customer;

        if (customer == null) {
            setText("No customer connected");
            setDisable(true);
            return;
        }
        setText(customer.getName());
        setDisable(false);
        setOnAction(e ->
                ViewManager.getInstance().showCustomers());
    }
}

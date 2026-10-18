package com.wac.autocore.view.components;

import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.Customer;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.Hyperlink;

public class CustomerHyperLink extends Hyperlink {
    private Customer customer;

    public CustomerHyperLink(Customer customer){
        this.customer = customer;

        ImageViewWithAltText userIcon = new ImageViewWithAltText("/imgs/user-solid.png", 20, 20,
                LanguageManager.getString("image.user-solid"));
        this.setGraphic(userIcon);

        if (customer == null){
            setText(LanguageManager.getString("customer.none.assigned"));
            setDisable(true);
        } else {
            setText(customer.getName());
            setOnAction(e ->
                    ViewManager.getInstance().showCustomers());
        }
    }

    public void setCustomer(Customer customer){
        this.customer = customer;

        if (customer == null) {
            setText(LanguageManager.getString("customer.none.connected"));
            setDisable(true);
            return;
        }
        setText(customer.getName());
        setDisable(false);
        setOnAction(e ->
                ViewManager.getInstance().showCustomers());
    }
}
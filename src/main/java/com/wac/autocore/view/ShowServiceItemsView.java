package com.wac.autocore.view;

import com.wac.autocore.controller.CatalogController;
import com.wac.autocore.controller.Result;
import com.wac.autocore.manager.ViewManager;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePack;
import com.wac.autocore.util.StylingUtil;
import com.wac.autocore.view.components.*;
import com.wac.autocore.view.components.kanban.KanbanColumn;
import com.wac.autocore.view.components.kanban.KanbanGrid;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.List;

public class ShowServiceItemsView {
    private final CatalogController catalogController;
    private final UiResultHandler uiResultHandler;

    public ShowServiceItemsView() {
        catalogController = ViewManager.getInstance().getControllers().catalog();
        uiResultHandler = new UiResultHandler();
    }

    private VBox serviceItemsBox;
    private VBox servicePackBox;
    private VBox detailPanel;

    public Parent show() {
        BorderPane layout = new BorderPane();
        Node header = getHeader();
        header.getStyleClass().add("content-header-container");
        layout.setTop(header);

        GridPane mainContent = new KanbanGrid(2);

        for (int i = 0; i < 2; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(50);
            mainContent.getRowConstraints().add(rowConstraints);
        }

        detailPanel = new VBox();

        serviceItemsBox = new VBox();
        serviceItemsBox.getStyleClass().add("card-container");
        fillServiceItems();

        BorderPane servicePackColumnHeader = getServicePackColumnHeader();

        servicePackBox = new VBox();
        servicePackBox.getStyleClass().add("card-container");
        fillServicePacks();

        ScrollPane serviceItemsColumn = new ScrollPane(serviceItemsBox);
        serviceItemsColumn.setFitToWidth(true);
        serviceItemsColumn.setFitToHeight(true);

        KanbanColumn servicePackColumn = new KanbanColumn(servicePackColumnHeader, servicePackBox);

        mainContent.add(serviceItemsColumn, 0, 0);
        mainContent.add(servicePackColumn, 0, 1);
        mainContent.add(detailPanel, 1, 0, 1,2);

        GridPane.setVgrow(serviceItemsColumn, Priority.ALWAYS);
        GridPane.setVgrow(servicePackColumn, Priority.ALWAYS);
        GridPane.setVgrow(detailPanel, Priority.ALWAYS);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        layout.setCenter(mainContent);
        return layout;
    }

    private void fillServiceItems() {
        Result<List<ServiceItem>> serviceItemsResult = uiResultHandler.handle(catalogController.getServiceItems());
        serviceItemsBox.getChildren().clear();

        if(!serviceItemsResult.isSuccess() || serviceItemsResult.value().isEmpty()){
            Label errorLabel = new Label(LanguageManager.getString("services.empty"));
            errorLabel.getStyleClass().add("error-label");
            serviceItemsBox.getChildren().add(errorLabel);
        }

        for (ServiceItem serviceItem : serviceItemsResult.value()) {
            ServiceItemCard card = new ServiceItemCard(serviceItem, s ->
            {
                ServiceItemDetails details = new ServiceItemDetails(s,
                        () -> {fillServiceItems(); fillServicePacks();}
                );
                VBox.setVgrow(details, Priority.ALWAYS);
                detailPanel.getChildren().setAll(
                        details);
            }
            );
            card.getStyleClass().add("clickable");
            serviceItemsBox.getChildren().add(card);
        }
    }

    private static BorderPane getServicePackColumnHeader() {
        Label servicePackColumnTitle = new Label(
                LanguageManager.getString("servicePack.column.title")
        );
        servicePackColumnTitle.getStyleClass().add("column-title");

        Button createServicePackBtn = new Button(
                LanguageManager.getString("servicePack.create.button")
        );
        createServicePackBtn.getStyleClass().add("confirm-btn");
        createServicePackBtn.setOnAction(e -> ViewManager.getInstance().showCreateServicePackPopup());

        BorderPane servicePackColumnHeader = new BorderPane();
        servicePackColumnHeader.getStyleClass().add("column-header");
        servicePackColumnHeader.setCenter(servicePackColumnTitle);
        servicePackColumnHeader.setRight(createServicePackBtn);
        return servicePackColumnHeader;
    }

    private void fillServicePacks() {
        Result<List<ServicePack>> servicePacksResult = uiResultHandler.handle(catalogController.getServicePacks());
        Result<List<ServiceItem>> serviceItemsResult = uiResultHandler.handle(catalogController.getServiceItems());
        servicePackBox.getChildren().clear();

        if (!servicePacksResult.isSuccess() || servicePacksResult.value().isEmpty()) {
            Label errorLabel = new Label(LanguageManager.getString("services.empty"));
            errorLabel.getStyleClass().add("error-label");
            servicePackBox.getChildren().add(errorLabel);
            return;
        }

        for (ServicePack servicePack : servicePacksResult.value()) {
            ServicePackCard card = new ServicePackCard(servicePack);

            if(serviceItemsResult.isSuccess()) {
                card.setOnMouseClicked(e -> {
                    ServicePackDetails servicePackDetails = new ServicePackDetails(servicePack,
                            serviceItemsResult.value(),
                            s -> saveServicePack(s),
                            id -> deleteServicePack(id));
                    VBox.setVgrow(servicePackDetails, Priority.ALWAYS);

                    //TODO när man trycker på Tjänst och sedan servicepaket ser båda ut att vara valda
                    StylingUtil.setSelected(card, "card");


                    detailPanel.getChildren().setAll(servicePackDetails);
                });
                card.getStyleClass().add("clickable");
            }else {
                Label errorLabel = new Label(LanguageManager.getString("services.notEditable"));
                errorLabel.getStyleClass().add("error-label");
                servicePackBox.getChildren().addFirst(errorLabel);
            }
            servicePackBox.getChildren().add(card);
        }
    }

    private void deleteServicePack(int servicePackId) {
        boolean deleteConfirmed = AppDialog.showConfirm(
                LanguageManager.getString("servicePack.delete.title"),
                String.format(
                        LanguageManager.getString("servicePack.delete.message"),
                        servicePackId
                ),
                LanguageManager.getString("servicePack.delete.warning")
        );

        if (deleteConfirmed) {
            Result<Void> result = uiResultHandler.handle(catalogController.deleteServicePack(servicePackId));

            if(result.isSuccess()) {
                AppDialog.showInformation(
                        LanguageManager.getString("servicePack.delete.success.title"),
                        LanguageManager.getString("servicePack.delete.success.message"),
                        ""
                );

                detailPanel.getChildren().clear();
                fillServicePacks();
            }
        }
    }

    private void saveServicePack(ServicePack servicePack) {
            Result<ServicePack> savedServicePackResult =
                    uiResultHandler.handle(
                            catalogController.updateServicePack(
                                servicePack.getId(),
                                servicePack
                    ));

            if(savedServicePackResult.isSuccess()) {
                ServicePack savedServicePack = savedServicePackResult.value();
                AppDialog.showInformation(
                        LanguageManager.getString("servicePack.save.success.title"),
                        LanguageManager.getString("servicePack.save.success.message"),
                        String.format(
                                LanguageManager.getString("servicePack.save.success.details"),
                                savedServicePack.getName(),
                                savedServicePack.getServiceItems().size()
                        )
                );

                fillServicePacks();
            }
    }

    private Node getHeader() {
        BorderPane headerPane = new BorderPane();
        Label title = new Label(LanguageManager.getString("services.title"));
        title.getStyleClass().setAll("page-title");

        headerPane.setCenter(title);
        return headerPane;
    }
}
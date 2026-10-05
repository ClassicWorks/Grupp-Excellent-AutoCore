package com.wac.autocore.view.components;

import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.*;

import java.util.Optional;

public class AppDialog {

    public static void showInformation(
            String title,
            String header,
            String message) {

        show(
                Alert.AlertType.INFORMATION,
                title,
                header,
                message,
                createCloseButton()
        );
    }

    public static void showWarning(
            String title,
            String header,
            String message) {

        show(
                Alert.AlertType.WARNING,
                title,
                header,
                message,
                createCloseButton()
        );
    }

    public static void showError(
            String title,
            String header,
            String message) {

        show(
                Alert.AlertType.ERROR,
                title,
                header,
                message,
                createCloseButton()
        );
    }

    public static boolean showConfirm(
            String title,
            String header,
            String message) {

        ButtonType confirmBtn = new ButtonType(
                LanguageManager.getString("dialog.confirm"),
                ButtonBar.ButtonData.YES
        );

        ButtonType cancelBtn = new ButtonType(
                LanguageManager.getString("dialog.cancel"),
                ButtonBar.ButtonData.NO
        );



        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                message,
                confirmBtn,
                cancelBtn
        );

        alert.setTitle(title);
        alert.setHeaderText(header);

        style(alert);
        addButtonStyle(alert, confirmBtn, "confirm-btn");
        addButtonStyle(alert, cancelBtn, "cancel-btn");

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == confirmBtn;
    }

    private static void show(
            Alert.AlertType type,
            String title,
            String header,
            String message,
            ButtonType button) {

        Alert alert = new Alert(
                type,
                message,
                button
        );

        alert.setTitle(title);
        alert.setHeaderText(header);

        style(alert);

        alert.showAndWait();
    }

    private static ButtonType createCloseButton() {
        return new ButtonType(
                LanguageManager.getString("dialog.close"),
                ButtonBar.ButtonData.CANCEL_CLOSE
        );
    }

    private static void style(Alert alert) {
        DialogPane pane = alert.getDialogPane();

        pane.getStylesheets().add(
                AppDialog.class
                        .getResource("/style/stylesheet.css")
                        .toExternalForm()
        );

        pane.getStyleClass().add("app-dialog");
    }

    private static void addButtonStyle(
            Alert alert,
            ButtonType buttonType,
            String styleClass) {

        Button button =
                (Button) alert.getDialogPane().lookupButton(buttonType);

        button.getStyleClass().add(styleClass);
    }
}
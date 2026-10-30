package com.wac.autocore.view;

import com.wac.autocore.controller.LoginController;
import com.wac.autocore.controller.Result;
import com.wac.autocore.util.LanguageManager;
import com.wac.autocore.view.components.FormFieldContainer;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginView {
    private final LoginController loginController;
    private final Runnable onLoginSuccess;

    public LoginView(LoginController loginController, Runnable onLoginSuccess) {
        this.loginController = loginController;
        this.onLoginSuccess = onLoginSuccess;
    }

    public Parent show() {
        Label title = new Label(LanguageManager.getString("login.title"));
        title.getStyleClass().add("form-title");

        TextField usernameField = new TextField();
        usernameField.setPromptText(LanguageManager.getString("login.username.prompt"));
        VBox usernameBox = new FormFieldContainer(LanguageManager.getString("login.username.label"), usernameField, false);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(LanguageManager.getString("login.password.prompt"));
        VBox passwordBox = new FormFieldContainer(LanguageManager.getString("login.password.label"), passwordField, false);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Button loginBtn = new Button(LanguageManager.getString("login.button"));
        loginBtn.getStyleClass().add("confirm-btn");
        loginBtn.setDefaultButton(true);

        loginBtn.setOnAction(e -> {
            errorLabel.setText("");

            Result<Void> result = loginController.login(usernameField.getText(), passwordField.getText());
            passwordField.clear();

            if (result.isSuccess()) {
                onLoginSuccess.run();
            } else {
                errorLabel.setText(result.error());
            }
        });

        VBox form = new VBox(
                title,
                usernameBox,
                passwordBox,
                loginBtn,
                errorLabel);
        form.getStyleClass().add("form-container");
        form.setMaxWidth(350);

        VBox root = new VBox(form);
        root.setAlignment(Pos.CENTER);
        return root;
    }
}
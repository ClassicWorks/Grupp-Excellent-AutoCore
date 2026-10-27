package com.wac.autocore.view;

import com.wac.autocore.security.Authenticator;
import com.wac.autocore.util.LanguageManager;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginView {
    private final Authenticator authenticator;
    private final Runnable onLoginSuccess;

    public LoginView(Authenticator authenticator, Runnable onLoginSuccess) {
        this.authenticator = authenticator;
        this.onLoginSuccess = onLoginSuccess;
    }

    public Parent show() {
        Label title = new Label(LanguageManager.getString("login.title"));
        title.getStyleClass().add("form-title");

        TextField usernameField = new TextField();
        usernameField.setPromptText(LanguageManager.getString("login.username"));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(LanguageManager.getString("login.password"));

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Button loginBtn = new Button(LanguageManager.getString("login.button"));
        loginBtn.getStyleClass().add("confirm-btn");
        loginBtn.setDefaultButton(true);

        loginBtn.setOnAction(e -> {
            errorLabel.setText("");
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText(LanguageManager.getString("login.error.empty"));
                return;
            }

            if (authenticator.login(username, password)) {
                passwordField.clear();
                onLoginSuccess.run();
            } else {
                passwordField.clear();
                errorLabel.setText(LanguageManager.getString("login.error.invalid"));
            }
        });

        VBox form = new VBox(title, usernameField, passwordField, loginBtn, errorLabel);
        form.getStyleClass().add("form-container");
        form.setMaxWidth(350);

        VBox root = new VBox(form);
        root.setAlignment(Pos.CENTER);
        return root;
    }
}
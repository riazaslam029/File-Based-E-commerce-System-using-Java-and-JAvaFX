package com.example.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AdminLoginController {


    /**
     * This controller handles the login screen functionality
     * for  admin  roles.
     */

    @FXML
    private TextField username;          // Input field for username
    @FXML
    private PasswordField password;      // Input field for password (hidden characters)
    @FXML
    private Label wrongLogin;            // Label to show login error messages

    private final String usersFile = "users.txt";  // File containing registered user credentials

    /**
     * Called when the user clicks the login button.
     * It verifies role and credentials and redirects to the appropriate screen.
     */
    @FXML
    private void userLogin(ActionEvent event) {
        String user = username.getText().trim();
        String pass = password.getText().trim();
        String selectedRole = RoleSelectionController.currentRole;

        // Check if username or password is empty
        if (user.isEmpty() || pass.isEmpty()) {
            wrongLogin.setText("Username or password cannot be empty!");
            return;
        }

        try {
            if ("admin".equals(selectedRole)) {
                // Admin login check (hardcoded credentials)
                if (user.equals("admin") && pass.equals("1234")) {
                    wrongLogin.setText("");
                    HelloApplication.changeScene("AdminSelection.fxml");
                } else {
                    wrongLogin.setText("Incorrect admin username or password!");
                }
            } else {
                // No role selected
                wrongLogin.setText("Please select a role first.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            wrongLogin.setText("Error loading scene!");
        }
    }


    /**
     * Navigates back to the role selection screen.
     */
    @FXML
    private void handleBack(ActionEvent event) throws Exception {
        HelloApplication.changeScene("role-selection.fxml");
    }
}


package com.example.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Controller for the role selection screen.
 * Allows the user to choose between admin and user login.
 */
public class RoleSelectionController {

    // Holds the currently selected role (either "admin" or "user")
    public static String currentRole = "";

    /**
     * Called when the Admin Login button is clicked.
     * Sets the current role to "admin" and opens the login screen.
     */
    @FXML
    public void handleAdminLogin(ActionEvent event) throws Exception {
        currentRole = "admin";
        HelloApplication.changeScene("adminLoginView.fxml"); // Navigate to login screen
    }

    /**
     * Called when the User Login button is clicked.
     * Sets the current role to "user" and opens the login screen.
     */
    @FXML
    public void handleUserLogin(ActionEvent event) throws Exception {
        currentRole = "user";
        HelloApplication.changeScene("userLoginView.fxml"); // Navigate to login screen
    }
}

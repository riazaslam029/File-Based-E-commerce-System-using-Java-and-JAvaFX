package com.example.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This controller handles the login screen functionality
 * for both admin and user roles.
 */
public class UserloginController {

    @FXML private TextField username;          // Input field for username
    @FXML private PasswordField password;      // Input field for password (hidden characters)
    @FXML private Label wrongLogin;            // Label to show login error messages

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
                    HelloApplication.changeScene("adminSelection.fxml");
                } else {
                    wrongLogin.setText("Incorrect admin username or password!");
                }
            } else if ("user".equals(selectedRole)) {
                // Regular user login validation from users.txt
                if (isValidUser(user, pass)) {
                    wrongLogin.setText("");
                    HelloApplication.changeScene("UserDashboard.fxml");
                } else {
                    wrongLogin.setText("Incorrect user credentials!");
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
     * Validates the user credentials against the records in users.txt.
     */
    private boolean isValidUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;  // Valid user found
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users file: " + e.getMessage());
        }
        return false;  // User not found
    }

    /**
     * Navigates back to the role selection screen.
     */
    @FXML
    private void handleBack(ActionEvent event) throws Exception {
        HelloApplication.changeScene("role-selection.fxml");
    }

    /**
     * Opens the registration form for users only.
     * Admins are not allowed to register from this screen.
     */
    @FXML
    private void handleRegister(ActionEvent event) throws Exception {
        String selectedRole = RoleSelectionController.currentRole;
        if ("admin".equals(selectedRole)) {
            wrongLogin.setText("Admin new account cannot be created, use already registered!");
        } else {
            HelloApplication.changeScene("register.fxml");
        }
    }
}

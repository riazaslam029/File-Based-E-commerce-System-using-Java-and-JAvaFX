
package com.example.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;

/**
 * Controller class for handling user registration.
 */
public class RegisterController {

    @FXML private TextField newUsername;         // Input field for new username
    @FXML private PasswordField newPassword;     // Input field for new password
    @FXML private Label statusLabel;             // Label to display status messages

    private final String usersFile = "users.txt"; // File where registered users are stored

    /**
     * This method is called when the user clicks the Register button.
     * It validates input, checks if the username is already taken,
     * and saves the new user if valid.
     */
    @FXML
    public void handleRegister() {
        String username = newUsername.getText().trim();
        String password = newPassword.getText().trim();

        // Check for empty fields
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Fields cannot be empty.");
            return;
        }

        // Check if username already exists
        if (isUsernameTaken(username)) {
            statusLabel.setText("Username already exists.");
            return;
        }

        // Save the new user to users.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile, true))) {
            writer.write(username + "," + password);
            writer.newLine();
            statusLabel.setText("Registration successful!");
        } catch (IOException e) {
            statusLabel.setText("Error writing to file.");
        }
    }

    /**
     * Checks whether the given username already exists in users.txt.
     */
    private boolean isUsernameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(username)) {
                    return true; // Username found
                }
            }
        } catch (IOException e) {
            return false; // If file can't be read, assume username not taken
        }
        return false;
    }

    /**
     * Navigates back to the login screen when the user clicks Back.
     */
    @FXML
    public void handleBack() throws Exception {
        HelloApplication.changeScene("userLoginView.fxml");
    }
}

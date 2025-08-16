package com.example.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class userManagementController implements Initializable {

    @FXML
    private TextArea userDisplayArea;  // Area to display all users

    @FXML
    private TextField usernameField;   // Input field to enter username to delete



    private final String userFile = "users.txt";  // Path to user data file

    // Automatically called when scene is loaded
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUsersFromFile();  // Display users when page opens
    }

    // Reads users from the file and displays them in the TextArea
    private void loadUsersFromFile() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%-5s %-25s %-20s%n", "No.", "Username", "Password"));
        builder.append(String.format("%-5s %-25s %-20s%n", "---", "-------------------------", "--------------------"));

        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    count++;
                    builder.append(String.format("%-5d %-25s %-20s%n", count, username, password));
                }
            }

            builder.append("\nTotal Users: ").append(count);
            userDisplayArea.setText(builder.toString());

        } catch (IOException e) {
            userDisplayArea.setText("Error reading users.txt");
            e.printStackTrace();
        }
    }


    // Deletes a user from the file after matching both username and password
    @FXML
    private void deleteUser() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter  username .");
            return;
        }

        File inputFile = new File(userFile);
        File tempFile = new File("temp_users.txt");
        boolean userFound = false;

        // Read all users and copy non-matching ones to temp file
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String fileUser = parts[0].trim();
                    String filePass = parts[1].trim();
                    if (fileUser.equals(username) ) {
                        userFound = true; // match found, skip writing this line
                        continue;
                    }
                }
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "File Error", "An error occurred while processing the file.");
            return;
        }

        // Replace original file if user was found and deleted
        if (userFound) {
            if (inputFile.delete() && tempFile.renameTo(inputFile)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
                loadUsersFromFile();  // Refresh user list
            } else {
                showAlert(Alert.AlertType.ERROR, "File Error", "Failed to update the user file.");
            }
        } else {
            tempFile.delete();  // delete temp file if no match found
            showAlert(Alert.AlertType.INFORMATION, "Not Found", "No matching user found.");
        }

        usernameField.clear();
    }

    // Navigates back to the admin dashboard
    @FXML
    public void handleBack(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("AdminSelection.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    // Shows a simple alert dialog with custom message
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

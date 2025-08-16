package com.example.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminSelectionController {

    @FXML
    private Text DashboardText;

    @FXML
    private Button DashboardSalesReport;

    @FXML
    private Button DashboardSaleReport;

    @FXML
    private Button DashboardProductManagement;

    // Opens the sales report scene
    @FXML
    public void openSalesReport(ActionEvent event) throws IOException {
        System.out.println("Opening Sales Report...");
        try {
            HelloApplication.changeScene("SalesReport.fxml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // Opens the user management scene
    @FXML
    public void openUserManagement(ActionEvent event) throws IOException {
        System.out.println("Opening User Management...");
        try {
            HelloApplication.changeScene("userManagement.fxml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // Opens the product management scene
    @FXML
    public void openProductManagement(ActionEvent event) throws IOException {
        System.out.println("Opening Product Management...");
        try {
            HelloApplication.changeScene("productManagement.fxml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // Navigates back to the login screen
    @FXML
    public void handleBack1(ActionEvent event) throws IOException {
        try {
            HelloApplication.changeScene("adminLoginView.fxml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

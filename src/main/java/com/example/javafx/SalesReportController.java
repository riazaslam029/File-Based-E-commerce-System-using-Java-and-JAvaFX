package com.example.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.io.*;
import java.time.LocalDate;

/**
 * This controller manages the Sales Report screen.
 * It shows total customers, products sold, revenue, and displays a chart.
 */
public class SalesReportController {

    @FXML private Label soldProducts;
    @FXML private Label totalCustomers;
    @FXML private Label totalrevenue;
    @FXML private AreaChart<String, Number> revenueChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private final String adminDataFile = "adminData.txt"; // Stores sales stats
    private final String usersFile = "users.txt";         // Stores user credentials

    @FXML
    public void initialize() {
        // Count number of users from users.txt
        int userCount = countUsers(usersFile);

        // Load sales stats from adminData.txt
        SalesStats stats = readStatsFromFile(adminDataFile);

        // Display values on labels
        totalCustomers.setText(String.valueOf(userCount));
        soldProducts.setText(String.valueOf(stats.products));
        totalrevenue.setText("$ " + String.format("%.2f", stats.totalSales));

        // Load chart with current month's revenue
        setupRevenueChart(stats.totalSales);
    }

    /**
     * Counts the number of registered users in users.txt
     */
    private int countUsers(String filePath) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    count++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users file: " + e.getMessage());
        }
        return count;
    }


    /**
     * Reads admin stats (customers, products sold, total sales) from file.
     */
    private SalesStats readStatsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line1 = br.readLine();
            String line1 = br.readLine();
            String line2 = br.readLine();

            if ( line1 != null && line2 != null) {
               // int customers = Integer.parseInt();
                int products = Integer.parseInt(line1.trim());
                double sales = Double.parseDouble(line2.trim());
                return new SalesStats(countUsers(filePath), products, sales);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading admin stats: " + e.getMessage());
        }
        return new SalesStats(0, 0, 0.0); // Return default if read fails
    }

    /**
     * Generates an area chart showing revenue for each month.
     * Currently, only the current month shows actual revenue.
     */
    private void setupRevenueChart(double revenue) {
        xAxis.setLabel("Month");
        yAxis.setLabel("Revenue ($)");
        yAxis.setAutoRanging(true);
//        yAxis.setLowerBound(5000);
//        yAxis.setUpperBound(1500000);
//        yAxis.setTickUnit(70000);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Revenue");

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int currentMonthIndex = LocalDate.now().getMonthValue();

        for (int i = 0; i < currentMonthIndex; i++) {
            double monthlyRevenue = (i == currentMonthIndex - 1) ? revenue : 0;
            series.getData().add(new XYChart.Data<>(months[i], monthlyRevenue));
        }

        revenueChart.getData().add(series);
    }

    /**
     * Handles back button to return to Admin Dashboard.
     */
    @FXML
    public void handleBack(ActionEvent event) throws IOException {
        try {
            HelloApplication.changeScene("AdminSelection.fxml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper class to hold sales statistics (customers, products sold, total revenue).
     */

    public static class SalesStats {
        public int customers;
        public int products;
        public double totalSales;

        public SalesStats(int customers, int products, double totalSales) {
            this.customers = customers;
            this.products = products;
            this.totalSales = totalSales;
        }
    }
}

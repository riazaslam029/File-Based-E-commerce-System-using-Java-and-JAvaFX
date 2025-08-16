package com.example.javafx;

import java.io.*;

/**
 * This class acts as a shared data store for tracking total revenue,
 * total sold products, and total customers across the application.
 * It supports reading from and writing to a file to persist data even after restarting the program.
 */
public class AppData {

    // Tracks the total revenue earned from user purchases
    public static double totalRevenue = 0.0;

    // Tracks the total number of products sold
    public static int totalSoldProducts = 0;

    // Tracks the total number of customers
    public static int totalCustomers = 0;

    /**
     * Loads saved data from a file (adminData.txt) to restore state when the app starts.
     * If the file is missing or has invalid data, default values are used.
     */
    public static void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            totalSoldProducts = Integer.parseInt(reader.readLine().trim());
            totalRevenue = Double.parseDouble(reader.readLine().trim());
        } catch (IOException | NumberFormatException e) {
            System.out.println("Failed to load admin data. Using defaults.");
        }
    }

    /**
     * Saves the current state (customers, products sold, revenue) to a file.
     * This ensures that data persists across program restarts.
     */
    public static void saveToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(totalSoldProducts + "\n");
            writer.write(totalRevenue + "\n");
        } catch (IOException e) {
            System.out.println("Failed to save admin data: " + e.getMessage());
        }
    }
}

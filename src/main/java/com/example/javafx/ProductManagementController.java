package com.example.javafx;

import com.example.javafx.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.event.ActionEvent;

import java.io.*;
import java.util.Optional;

public class ProductManagementController{

    @FXML
    private ListView<Product> productListView;

    @FXML
    private ImageView productImageView;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final String filePath = "products.txt";

    @FXML
    public void initialize() {
        // Set the list view with custom cells and load data from file
        productListView.setItems(productList);
        productListView.setCellFactory(param -> new ProductCell());
        productListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                showProductImage(newSel);
            } else {
                productImageView.setImage(null);
            }
        });

        loadProductsFromFile();
    }

    // Handle adding a new product to the list
    @FXML
    public void handleAddProduct() {
        // Step 1: Get product name
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add Product");
        nameDialog.setHeaderText("Step 1: Enter Product Name");
        nameDialog.setContentText("Product Name:");

        Optional<String> nameOpt = nameDialog.showAndWait();
        if (!nameOpt.isPresent() || nameOpt.get().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Product name cannot be empty.");
            return;
        }
        String name = nameOpt.get().trim();

        // Check for duplicate product name
        for (Product p : productList) {
            if (p.getName().equalsIgnoreCase(name)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Product", "A product with this name already exists.");
                return;
            }
        }

        // Step 2: Select product image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Step 2: Select Product Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File imageFile = fileChooser.showOpenDialog(productListView.getScene().getWindow());
        if (imageFile == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Image selection cancelled.");
            return;
        }
        String imagePath = imageFile.getAbsolutePath();

        // Step 3: Get product price
        TextInputDialog priceDialog = new TextInputDialog();
        priceDialog.setTitle("Add Product");
        priceDialog.setHeaderText("Step 3: Enter Product Price");
        priceDialog.setContentText("Price:");

        Optional<String> priceOpt = priceDialog.showAndWait();
        if (!priceOpt.isPresent() || priceOpt.get().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Price cannot be empty.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceOpt.get().trim());
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid price entered.");
            return;
        }

        // Step 4: Get product quantity
        Integer quantity = promptForValidQuantity("Add Product", "Step 4: Enter Product Quantity");
        if (quantity == null) return;

        // Add product to list and save
        Product newProduct = new Product(name, price, imagePath, quantity);
        productList.add(newProduct);
        appendProductToFile(newProduct);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully.");
    }

    // Handle editing an existing product
    @FXML
    public void handleEditProduct() {
        Product selected = productListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to edit.");
            return;
        }

        // Edit name
        TextInputDialog nameDialog = new TextInputDialog(selected.getName());
        nameDialog.setTitle("Edit Product");
        nameDialog.setHeaderText("Edit Product Name");
        nameDialog.setContentText("Product Name:");

        Optional<String> nameOpt = nameDialog.showAndWait();
        if (!nameOpt.isPresent() || nameOpt.get().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Product name cannot be empty.");
            return;
        }
        String newName = nameOpt.get().trim();

        // Check for duplicate
        for (Product p : productList) {
            if (p != selected && p.getName().equalsIgnoreCase(newName)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Product", "A product with this name already exists.");
                return;
            }
        }

        // Optional: change image
        Alert imgAlert = new Alert(Alert.AlertType.CONFIRMATION);
        imgAlert.setTitle("Edit Image");
        imgAlert.setHeaderText("Do you want to change the product image?");
        imgAlert.setContentText("Choose OK to select new image, Cancel to keep current.");

        Optional<ButtonType> imgChoice = imgAlert.showAndWait();
        String newImagePath = selected.getImagePath();
        if (imgChoice.isPresent() && imgChoice.get() == ButtonType.OK) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select New Product Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File imageFile = fileChooser.showOpenDialog(productListView.getScene().getWindow());
            if (imageFile != null) {
                newImagePath = imageFile.getAbsolutePath();
            }
        }

        // Edit price
        TextInputDialog priceDialog = new TextInputDialog(String.valueOf(selected.getPrice()));
        priceDialog.setTitle("Edit Product");
        priceDialog.setHeaderText("Edit Product Price");
        priceDialog.setContentText("Price:");

        Optional<String> priceOpt = priceDialog.showAndWait();
        if (!priceOpt.isPresent() || priceOpt.get().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Price cannot be empty.");
            return;
        }

        double newPrice;
        try {
            newPrice = Double.parseDouble(priceOpt.get().trim());
            if (newPrice < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid price entered.");
            return;
        }

        // Edit quantity
        Integer newQty = promptForValidQuantity("Edit Product", "Enter Product Quantity");
        if (newQty == null) return;

        // Update product and save
        selected.setName(newName);
        selected.setPrice(newPrice);
        selected.setImagePath(newImagePath);
        selected.setQuantity(newQty);

        productListView.refresh();
        writeAllProductsToFile();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully.");
    }

    // Handle removing a product
    @FXML
    public void handleRemoveProduct() {
        Product selected = productListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to remove.");
            return;
        }

        productList.remove(selected);
        writeAllProductsToFile();
        productImageView.setImage(null);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Product removed.");
    }

    // Go back to previous (admin selection) screen
    @FXML
    public void handleBack(ActionEvent event) {
        try {
            HelloApplication.changeScene("AdminSelection.fxml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Show selected product image
    private void showProductImage(Product product) {
        File imgFile = new File(product.getImagePath());
        if (imgFile.exists()) {
            productImageView.setImage(new Image(imgFile.toURI().toString()));
        } else {
            productImageView.setImage(null);
        }
    }

    // Append a new product to products.txt
    private void appendProductToFile(Product product) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(product.getName() + "," + product.getPrice() + "," + product.getImagePath() + "," + product.getQuantity());
            writer.newLine();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Could not write to file.");
        }
    }

    // Overwrite the entire products.txt file with updated list
    private void writeAllProductsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Product p : productList) {
                writer.write(p.getName() + "," + p.getPrice() + "," + p.getImagePath() + "," + p.getQuantity());
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Could not write to file.");
        }
    }

    // Load product data from file at startup
    private void loadProductsFromFile() {
        productList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[0];
                    double price = Double.parseDouble(parts[1]);
                    String imagePath = parts[2];
                    int quantity = Integer.parseInt(parts[3]);
                    productList.add(new Product(name, price, imagePath, quantity));
                }
            }
        } catch (IOException e) {
            System.out.println("Products file not found or error reading file.");
        }
    }

    // Show a standard alert dialog
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Prompt admin to enter a valid quantity repeatedly until correct
    private Integer promptForValidQuantity(String title, String header) {
        while (true) {
            TextInputDialog qtyDialog = new TextInputDialog();
            qtyDialog.setTitle(title);
            qtyDialog.setHeaderText(header);
            qtyDialog.setContentText("Quantity:");

            Optional<String> qtyOpt = qtyDialog.showAndWait();
            if (!qtyOpt.isPresent()) return null;

            String input = qtyOpt.get().trim();
            if (!input.matches("\\d+")) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a whole number (e.g., 1, 2, 3).");
                continue;
            }

            return Integer.parseInt(input);
        }
    }

    // Custom cell for displaying product in ListView
    private static class ProductCell extends ListCell<Product> {
        @Override
        protected void updateItem(Product product, boolean empty) {
            super.updateItem(product, empty);
            if (empty || product == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(product.getName() + " - $" + product.getPrice() + " - Qty: " + product.getQuantity());
            }
        }
    }
}

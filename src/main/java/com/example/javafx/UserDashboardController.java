package com.example.javafx;

import com.example.javafx.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserDashboardController {

    @FXML private FlowPane productFlowPane; // Container to display product cards
    @FXML private ListView<Product> cartList; // ListView to show added items in cart
    @FXML private TextField searchField;

    private ObservableList<Product> products = FXCollections.observableArrayList(); // Stores all products from file
    private final ObservableList<Product> cart = FXCollections.observableArrayList(); // Stores user-selected products

    // Called when the scene loads
    @FXML
    public void initialize() {
        cartList.setItems(cart);      // Bind cart list to UI
        loadProductsFromFile();       // Load products from file
        showProductsInCards();        // Display products as cards
    }

    // Reads products from file and adds to list
    private void loadProductsFromFile() {
        products.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("products.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    String name = data[0].trim();
                    double price = Double.parseDouble(data[1].trim());
                    String imagePath = data[2].trim();
                    int quantity = Integer.parseInt(data[3].trim());
                    products.add(new Product(name, price, imagePath, quantity));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
    }

    // Displays all products in FlowPane as cards
    private void showProductsInCards() {
        productFlowPane.getChildren().clear();
        for (Product product : products) {
            VBox card = createProductCard(product);
            productFlowPane.getChildren().add(card);
        }
    }

    // Displays filtered products based on search
    private void displayProducts(List<Product> filteredProducts) {
        productFlowPane.getChildren().clear();
        for (Product product : filteredProducts) {
            VBox card = createProductCard(product);
            productFlowPane.getChildren().add(card);
        }
    }

    // Called when search button is clicked
    @FXML
    public void handleSearch(ActionEvent event) {
        String query = searchField.getText().toLowerCase();
        List<Product> filtered = products.stream()
                .filter(p -> p.getName().toLowerCase().contains(query))
                .collect(Collectors.toList());
        displayProducts(filtered);
    }

    // Creates a UI card for a single product
    private VBox createProductCard(Product product) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white;");
        card.setPrefWidth(150);

        ImageView imageView = new ImageView();
        File imageFile = new File(product.getImagePath());
        if (imageFile.exists()) {
            imageView.setImage(new Image(imageFile.toURI().toString(), 120, 100, true, true));
        }
        imageView.setSmooth(true);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label priceLabel = new Label(String.format("$%.2f", product.getPrice()));
        priceLabel.setStyle("-fx-text-fill: #2a9d8f; -fx-font-weight: bold;");

        TextField qtyField = new TextField();
        qtyField.setPromptText("Qty");
        qtyField.setPrefWidth(50);

        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.setOnAction(e -> {
            String qtyText = qtyField.getText();
            int qty;
            try {
                qty = Integer.parseInt(qtyText);
                if (qty <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert("Invalid Quantity", "Please enter a valid positive number.");
                return;
            }
            addToCart(product, qty);
            qtyField.clear();
        });

        Label quantityLabel = new Label("In Stock: " + product.getQuantity());
        quantityLabel.setStyle("-fx-text-fill: #555;");

        card.getChildren().addAll(imageView, nameLabel, priceLabel, quantityLabel, qtyField, addToCartBtn);
        return card;
    }

    // Adds a product to the cart
    @FXML
    private void addToCart(Product product, int quantity) {
        if (quantity > product.getQuantity()) {
            showAlert("Insufficient Stock", "Only " + product.getQuantity() + " items in stock.");
            return;
        }

        for (Product p : cart) {
            if (p.getName().equals(product.getName())) {
                if (p.getQuantity() + quantity > product.getQuantity()) {
                    showAlert("Insufficient Stock", "Only " + product.getQuantity() + " items in stock.");
                    return;
                }
                p.setQuantity(p.getQuantity() + quantity);
                cartList.refresh();
                return;
            }
        }

        Product cartProduct = new Product(product.getName(), product.getPrice(), product.getImagePath(), quantity);
        cart.add(cartProduct);
    }
    //this method runs when "done" is clicked
    @FXML
    public void handleDone() {
        if (cart.isEmpty()) {
            showAlert("Cart is empty!", "Please add items to the cart first.");
            return;
        }

        StringBuilder bill = new StringBuilder("Bill Summary:\n\n");
        final double[] total = {0.0};

        for (Product product : cart) {
            bill.append(product.getName())
                    .append(" x").append(product.getQuantity())
                    .append(" - $").append(String.format("%.2f", product.getPrice() * product.getQuantity()))
                    .append("\n");
            total[0] += product.getPrice() * product.getQuantity();
        }

        bill.append("\nTotal: $").append(String.format("%.2f", total[0]));

        // Step 1: Confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Order");
        confirmAlert.setHeaderText("Are you sure you want to place the order?");
        confirmAlert.setContentText("Click OK to proceed or Cancel to go back.");

        Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
        if (confirmResult.isEmpty() || confirmResult.get() != ButtonType.OK) {
            return;
        }

        // Step 2: Payment selection dialog
        Dialog<String> paymentDialog = new Dialog<>();
        paymentDialog.setTitle("Select Payment Method");
        paymentDialog.setHeaderText("Choose your payment method:");

        ButtonType payBtnType = new ButtonType("Pay", ButtonBar.ButtonData.OK_DONE);
        paymentDialog.getDialogPane().getButtonTypes().addAll(payBtnType, ButtonType.CANCEL);

        ComboBox<String> paymentOptions = new ComboBox<>();
        paymentOptions.getItems().addAll("Easypaisa", "JazzCash", "Debit Card", "Credit Card");
        paymentOptions.setValue("Easypaisa");

        VBox content = new VBox(10);
        content.getChildren().add(paymentOptions);
        paymentDialog.getDialogPane().setContent(content);

        paymentDialog.setResultConverter(dialogButton -> {
            if (dialogButton == payBtnType) {
                return paymentOptions.getValue();
            }
            return null;
        });

        Optional<String> selectedPayment = paymentDialog.showAndWait();
        if (selectedPayment.isEmpty()) {
            return;
        }

        // Step 3: Bill confirmation and processing
        Alert billAlert = new Alert(Alert.AlertType.NONE);
        billAlert.setTitle("Your Bill");
        billAlert.setContentText(bill.toString());

        ButtonType payButton = new ButtonType("Pay");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        billAlert.getButtonTypes().setAll(payButton, cancelButton);

        billAlert.showAndWait().ifPresent(response -> {
            if (response == payButton) {
                // Update admin data after purchase
                AppData.totalRevenue += total[0];
                AppData.totalSoldProducts += cart.stream().mapToInt(Product::getQuantity).sum();
                AppData.totalCustomers += 1;

                // Reduce quantity of purchased products
                for (Product cartProduct : cart) {
                    for (Product mainProduct : products) {
                        if (mainProduct.getName().equals(cartProduct.getName())) {
                            int remainingQty = mainProduct.getQuantity() - cartProduct.getQuantity();
                            mainProduct.setQuantity(remainingQty);
                            break;
                        }
                    }
                }

                // Save updated products
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("products.txt"))) {
                    for (Product p : products) {
                        writer.write(p.getName() + "," + p.getPrice() + "," + p.getImagePath() + "," + p.getQuantity());
                        writer.newLine();
                    }
                } catch (IOException e) {
                    System.out.println("Error saving updated product quantities: " + e.getMessage());
                }

                // Save updated admin statistics
                AppData.saveToFile("adminData.txt");

                Alert thankYouAlert = new Alert(Alert.AlertType.INFORMATION);
                thankYouAlert.setTitle("Thank You");
                thankYouAlert.setHeaderText("Thanks for shopping!");
                thankYouAlert.setContentText("Payment Method: " + selectedPayment.get());
                thankYouAlert.showAndWait();

                cart.clear();
                cartList.refresh();
            }
            showProductsInCards(); // Refresh UI to show updated quantities

        });
    }

    // Navigate back to login screen
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("userLoginView.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Utility method to show alert dialogs
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
@FXML
private void clearCart(ActionEvent event) {
        if (cart.isEmpty()) {
            showAlert("Error", "Cart is empty!");
        }else {
            cart.clear();
        }
}
}

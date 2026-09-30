import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ShoppingCartFX extends Application {

    // Same parallel arrays from the original project
    private final String[] products = {"Laptop", "Smartphone", "Headphones", "Smartwatch", "Keyboard"};
    private final double[] prices = {55000.0, 25000.0, 3000.0, 5000.0, 1500.0};
    
    // Same array to track quantities
    private final int[] quantities = new int[products.length];

    // JavaFX GUI Components
    private ComboBox<String> productDropdown;
    private TextField quantityField;
    private TextArea cartArea;
    private Label subtotalLabel;
    private Label discountLabel;
    private Label finalTotalLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Case Study 153 - Online Shopping Cart System");

        // Header Title
        Label titleLabel = new Label("ONLINE SHOPPING CART");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        // --- MODULE 1 & 2 UI: Product Selection ---
        GridPane selectionGrid = new GridPane();
        selectionGrid.setHgap(10);
        selectionGrid.setVgap(10);
        selectionGrid.setAlignment(Pos.CENTER);

        Label selectProductLabel = new Label("Select Product:");
        productDropdown = new ComboBox<>();
        
        // Loop through products to populate the dropdown
        for (int i = 0; i < products.length; i++) {
            productDropdown.getItems().add((i + 1) + ". " + products[i] + " (₹" + prices[i] + ")");
        }
        productDropdown.getSelectionModel().selectFirst();

        Label selectQtyLabel = new Label("Quantity:");
        quantityField = new TextField("1");
        quantityField.setPrefWidth(60);

        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-weight: bold;");
        
        // Button trigger mapped to handle selection
        addToCartBtn.setOnAction(e -> handleAddToCart());

        selectionGrid.add(selectProductLabel, 0, 0);
        selectionGrid.add(productDropdown, 1, 0);
        selectionGrid.add(selectQtyLabel, 2, 0);
        selectionGrid.add(quantityField, 3, 0);
        selectionGrid.add(addToCartBtn, 4, 0);

        // --- MODULE 3 & 5 UI: Cart Display ---
        cartArea = new TextArea();
        cartArea.setEditable(false);
        cartArea.setPrefRowCount(10);
        cartArea.setStyle("-fx-font-family: monospace; -fx-font-size: 13px;");

        // --- MODULE 4 & 5 UI: Billing & Discount Summary ---
        subtotalLabel = new Label("Subtotal: ₹0.00");
        discountLabel = new Label("Discount Applied: -₹0.00");
        finalTotalLabel = new Label("Final Total: ₹0.00");
        finalTotalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #E74C3C;");

        VBox summaryBox = new VBox(5, subtotalLabel, discountLabel, finalTotalLabel);
        summaryBox.setAlignment(Pos.CENTER_RIGHT);

        Button clearCartBtn = new Button("Clear Cart");
        clearCartBtn.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white;");
        clearCartBtn.setOnAction(e -> clearCart());

        Button checkoutBtn = new Button("Checkout & Generate Bill");
        checkoutBtn.setStyle("-fx-background-color: #2980B9; -fx-text-fill: white; -fx-font-weight: bold;");
        checkoutBtn.setOnAction(e -> generateOrderSummary());

        HBox actionBox = new HBox(15, clearCartBtn, checkoutBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        // Layout Configuration
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(
            titleLabel,
            selectionGrid,
            new Label("Cart Details:"),
            cartArea,
            summaryBox,
            actionBox
        );

        updateCartDisplay();

        Scene scene = new Scene(root, 650, 520);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- MODULE 2 & 3: Product Selection using Switch Statement ---
    private void handleAddToCart() {
        int selectedIndex = productDropdown.getSelectionModel().getSelectedIndex();
        int qty;

        try {
            qty = Integer.parseInt(quantityField.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid positive integer quantity.");
            return;
        }

        // Switch statement handling product selection matching original logic
        switch (selectedIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                quantities[selectedIndex] += qty;
                break;
            default:
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid product selection.");
                return;
        }

        updateCartDisplay();
    }

    // --- MODULE 3: Cart Total Calculation Method ---
    private double calculateCartTotal() {
        double subtotal = 0.0;
        for (int i = 0; i < products.length; i++) {
            subtotal += quantities[i] * prices[i];
        }
        return subtotal;
    }

    // --- MODULE 4: Discount Calculation Method (Exact If-Else Logic) ---
    private double calculateDiscount(double subtotal) {
        double discountRate;
        if (subtotal >= 50000) {
            discountRate = 0.15; // 15% discount for orders >= ₹50,000
        } else if (subtotal >= 20000) {
            discountRate = 0.10; // 10% discount for orders >= ₹20,000
        } else if (subtotal >= 5000) {
            discountRate = 0.05;  // 5% discount for orders >= ₹5,000
        } else {
            discountRate = 0.0;   // No discount
        }
        return subtotal * discountRate;
    }

    // --- MODULE 1 & 5: Updating Display Area & Subtotals ---
    private void updateCartDisplay() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%-20s %-12s %-10s %-12s%n", "Product", "Price (₹)", "Qty", "Total (₹)"));
        builder.append("----------------------------------------------------------\n");

        boolean isEmpty = true;
        for (int i = 0; i < products.length; i++) {
            if (quantities[i] > 0) {
                double total = quantities[i] * prices[i];
                builder.append(String.format("%-20s %-12.2f %-10d %-12.2f%n", products[i], prices[i], quantities[i], total));
                isEmpty = false;
            }
        }

        if (isEmpty) {
            builder.append("\n          Your shopping cart is currently empty.");
        }

        cartArea.setText(builder.toString());

        double subtotal = calculateCartTotal();
        double discount = calculateDiscount(subtotal);
        double finalTotal = subtotal - discount;

        subtotalLabel.setText(String.format("Subtotal: ₹%.2f", subtotal));
        discountLabel.setText(String.format("Discount Applied: -₹%.2f", discount));
        finalTotalLabel.setText(String.format("Final Total: ₹%.2f", finalTotal));
    }

    // --- MODULE 5: Checkout Summary ---
    private void generateOrderSummary() {
        double subtotal = calculateCartTotal();
        if (subtotal == 0) {
            showAlert(Alert.AlertType.WARNING, "Empty Cart", "Your cart is empty. Please add items before checking out!");
            return;
        }

        double discount = calculateDiscount(subtotal);
        double finalTotal = subtotal - discount;

        String summaryText = String.format(
            "Order Processed Successfully!\n\n" +
            "Subtotal: ₹%.2f\n" +
            "Discount: -₹%.2f\n" +
            "Final Amount Payable: ₹%.2f\n\n" +
            "Thank you for your purchase!",
            subtotal, discount, finalTotal
        );

        showAlert(Alert.AlertType.INFORMATION, "Order Summary", summaryText);
    }

    private void clearCart() {
        for (int i = 0; i < quantities.length; i++) {
            quantities[i] = 0;
        }
        updateCartDisplay();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
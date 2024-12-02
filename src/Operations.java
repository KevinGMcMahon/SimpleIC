import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import src.Logger;

public class Operations {

    // Test method for debugging
    public static void test() {
        System.out.println("Operations class is accessible!");
    }

    // Method to add a product to the database
    public static void addProduct(String name, String description, int quantity, double price) {
        String query = "INSERT INTO products (name, description, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, quantity);
            statement.setDouble(4, price);
            Logger.logInfo("Attempting to add a product: " + name); // Logger
            statement.executeUpdate();
            System.out.println("Product added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
    }

    // Method to view all products
    public static void viewProducts() {
        Logger.logInfo("Fetching all products from the database."); // Logger

        String query = "SELECT * FROM products";

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id") +
                        ", Name: " + resultSet.getString("name") +
                        ", Description: " + resultSet.getString("description") +
                        ", Quantity: " + resultSet.getInt("quantity") +
                        ", Price: " + resultSet.getDouble("price"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
    }

    // Method to delete a product
    public static void deleteProduct(int productId) {
        String query = "DELETE FROM products WHERE id = ?";

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            Logger.logInfo("Deleting product with ID: " + productId); // Logger
            statement.executeUpdate();
            System.out.println("Product deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
        }
    }

    // Method to update a product
    public static void updateProduct(int productId, String name, String description, int quantity, double price) {
        String query = "UPDATE products SET name = ?, description = ?, quantity = ?, price = ? WHERE id = ?";

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, quantity);
            statement.setDouble(4, price);
            statement.setInt(5, productId);
            Logger.logInfo("Updating product with ID: " + productId + " to new values."); // Logger
            statement.executeUpdate();
            System.out.println("Product updated successfully!");
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
        }
    }
    // Add Search functionality (interaction #5)

    // Add Run Report functionality (interaction #6)
}

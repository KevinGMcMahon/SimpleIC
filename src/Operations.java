import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.FileWriter;
import java.io.IOException;
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
            statement.executeUpdate();
            System.out.println("Product added successfully!");
            Logger.logInfo("Product added: " + name);
        } catch (SQLException e) {
            Logger.logSevere("Error adding product: " + e.getMessage());
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
    //Method to search inventory
    public static void searchProducts(String searchTerm) {
        String query = "SELECT * FROM products WHERE name LIKE ? OR description LIKE ?";
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();
            boolean found = false;

            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id") +
                        ", Name: " + resultSet.getString("name") +
                        ", Description: " + resultSet.getString("description") +
                        ", Quantity: " + resultSet.getInt("quantity") +
                        ", Price: " + resultSet.getDouble("price"));
                found = true;
            }

            if (!found) {
                System.out.println("No products found matching the search term.");
            }
        } catch (SQLException e) {
            Logger.logSevere("Error searching products: " + e.getMessage()); // Logger
            System.err.println("Error searching products: " + e.getMessage());
        }
    }

    // Method to delete a product
    public static void deleteProduct(int productId) {
        String deleteQuery = "DELETE FROM simple_ic.products WHERE id = ?";
        String logQuery = "SELECT message FROM deletion_logs WHERE product_id = ?";

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
             PreparedStatement logStatement = connection.prepareStatement(logQuery)) {

            // Delete the product
            deleteStatement.setInt(1, productId);
            deleteStatement.executeUpdate();
            Logger.logInfo("Deleting product with ID: " + productId); // Logger

            // Retrieve the deletion log
            logStatement.setInt(1, productId);
            ResultSet rs = logStatement.executeQuery();
            if (rs.next()) {
                String logMessage = rs.getString("message");
                Logger.logInfo(logMessage); // Log the deletion message
            }
        } catch (SQLException e) {
            Logger.logSevere("Error deleting product: " + e.getMessage()); // Logger
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

    // Method to generate a report of all products
    public static void exportInventoryToCSV() {
        String query = "SELECT * FROM products";
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery();
             FileWriter writer = new FileWriter("inventory.csv")) {

            // Write CSV Header
            writer.write("ID,Name,Description,Quantity,Price\n");

            // Write data rows
            while (resultSet.next()) {
                writer.write(resultSet.getInt("id") + "," +
                        resultSet.getString("name") + "," +
                        resultSet.getString("description") + "," +
                        resultSet.getInt("quantity") + "," +
                        resultSet.getDouble("price") + "\n");
            }

            System.out.println("Inventory exported successfully to inventory.csv.");
            Logger.logInfo("Inventory exported successfully to inventory.csv"); // Logger

        } catch (SQLException | IOException e) {
            Logger.logSevere("Error exporting inventory: " + e.getMessage()); // Logger
            System.err.println("Error exporting inventory: " + e.getMessage());
        }

    }
    // Method to create necessary database tables if they do not exist
    public static void createTablesIfNotExist(Connection connection) {
        try {
            // Create products table
            String createProductsTableQuery = "CREATE TABLE IF NOT EXISTS products (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "description TEXT, " +
                    "quantity INT NOT NULL, " +
                    "price DECIMAL(10, 2) NOT NULL" +
                    ")";

            // Create deletion_logs table
            String createDeletionLogsTableQuery = "CREATE TABLE IF NOT EXISTS deletion_logs (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "product_id INT, " +
                    "message VARCHAR(255), " +
                    "deletion_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

            try (PreparedStatement productsStatement = connection.prepareStatement(createProductsTableQuery);
                 PreparedStatement logsStatement = connection.prepareStatement(createDeletionLogsTableQuery)) {

                productsStatement.execute();
                logsStatement.execute();

                System.out.println("Tables created successfully (if they didn't already exist)");
                Logger.logInfo("Database tables verified/created successfully");
            }
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            Logger.logSevere("Error creating database tables: " + e.getMessage());
        }
    }
}

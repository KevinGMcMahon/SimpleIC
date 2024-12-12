import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import src.Logger;

public class InventoryControlSystemGUI extends JFrame {
    private JTextField hostField, usernameField;
    private JPasswordField passwordField;
    private JTextField nameField, descriptionField, quantityField, priceField, searchField;
    private JTable productsTable;
    private DefaultTableModel tableModel;
    private int selectedProductId = -1;

    public InventoryControlSystemGUI() {
        setTitle("Simple IC: Inventory Control System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Database Connection Panel
        JPanel connectionPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Database Connection"));

        JLabel hostLabel = new JLabel("Host:");
        hostField = new JTextField("localhost:3306");
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField("root");
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField("Dani973$");

        connectionPanel.add(hostLabel);
        connectionPanel.add(hostField);
        connectionPanel.add(usernameLabel);
        connectionPanel.add(usernameField);
        connectionPanel.add(passwordLabel);
        connectionPanel.add(passwordField);

        // Product Details Panel
        JPanel productPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        productPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionField = new JTextField();
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityField = new JTextField();
        JLabel priceLabel = new JLabel("Price:");
        priceField = new JTextField();

        productPanel.add(nameLabel);
        productPanel.add(nameField);
        productPanel.add(descriptionLabel);
        productPanel.add(descriptionField);
        productPanel.add(quantityLabel);
        productPanel.add(quantityField);
        productPanel.add(priceLabel);
        productPanel.add(priceField);

        // Search Panel
        JPanel searchPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Products"));

        JLabel searchLabel = new JLabel("Search Term:");
        searchField = new JTextField();
        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchProducts();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        // Inventory Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Inventory"));

        productsTable = new JTable();
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Description", "Quantity", "Price"}, 0);
        productsTable.setModel(tableModel);
        productsTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                int selectedRow = productsTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedProductId = (int) productsTable.getValueAt(selectedRow, 0);
                    nameField.setText((String) productsTable.getValueAt(selectedRow, 1));
                    descriptionField.setText((String) productsTable.getValueAt(selectedRow, 2));
                    quantityField.setText(String.valueOf((int) productsTable.getValueAt(selectedRow, 3)));
                    priceField.setText(String.valueOf((double) productsTable.getValueAt(selectedRow, 4)));
                } else {
                    selectedProductId = -1;
                    clearProductFields();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(productsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));

        JButton connectButton = new JButton("Connect to DB");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToDatabase();
            }
        });

        JButton addProductButton = new JButton("Add Product");
        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        JButton editProductButton = new JButton("Edit Product");
        editProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editProduct();
            }
        });

        JButton deleteProductButton = new JButton("Delete Product");
        deleteProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
        });

        JButton searchProductsButton = new JButton("Search Products");
        searchProductsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchProducts();
            }
        });

        JButton exportToCSVButton = new JButton("Export Inventory Report As CSV");
        exportToCSVButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportInventoryToCSV();
            }
        });

        buttonPanel.add(connectButton);
        buttonPanel.add(addProductButton);
        buttonPanel.add(editProductButton);
        buttonPanel.add(deleteProductButton);
        buttonPanel.add(searchProductsButton);
        buttonPanel.add(exportToCSVButton);

        setLayout(new BorderLayout(10, 10));
        add(connectionPanel, BorderLayout.NORTH);
        add(productPanel, BorderLayout.WEST);
        add(searchPanel, BorderLayout.EAST);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void connectToDatabase() {
        String host = hostField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + "/simple_ic", username, password)) {
            // Check if the necessary tables exist, and create them if they don't
            Operations.createTablesIfNotExist(connection);
            JOptionPane.showMessageDialog(this, "Connected to the database successfully!", "Connection Successful", JOptionPane.INFORMATION_MESSAGE);
            Logger.logInfo("Connected to the database successfully.");
            viewProducts();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to the database: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            Logger.logSevere("Error connecting to the database: " + e.getMessage());
        }
    }

    private void addProduct() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        int quantity = Integer.parseInt(quantityField.getText());
        double price = Double.parseDouble(priceField.getText());

        Operations.addProduct(name, description, quantity, price);
        clearProductFields();
        viewProducts();
    }

    private void editProduct() {
        if (selectedProductId != -1) {
            String name = nameField.getText();
            String description = descriptionField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());

            Operations.updateProduct(selectedProductId, name, description, quantity, price);
            clearProductFields();
            viewProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to edit.", "No Product Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteProduct() {
        if (selectedProductId != -1) {
            Operations.deleteProduct(selectedProductId);
            clearProductFields();
            viewProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "No Product Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void searchProducts() {
        String searchTerm = searchField.getText();
        tableModel.setRowCount(0);
        Operations.searchProducts(searchTerm);
        viewProducts();
    }

    private void exportInventoryToCSV() {
        Operations.exportInventoryToCSV();
    }

    private void clearProductFields() {
        nameField.setText("");
        descriptionField.setText("");
        quantityField.setText("");
        priceField.setText("");
    }

    private void viewProducts() {
        // Clear existing rows in the table
        tableModel.setRowCount(0);

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/simple_ic", "root", "Dani973$")) {
            String query = "SELECT * FROM products";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    tableModel.addRow(new Object[]{
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getInt("quantity"),
                            resultSet.getDouble("price")
                    });
                }

                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No products found in the database.", "Empty Inventory", JOptionPane.INFORMATION_MESSAGE);
                }

                Logger.logInfo("Successfully retrieved " + tableModel.getRowCount() + " products from the database.");
            } catch (SQLException e) {
                Logger.logSevere("Error fetching products: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Error fetching products: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            Logger.logSevere("Error connecting to the database: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error connecting to the database: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InventoryControlSystemGUI().setVisible(true);
            }
        });
    }
}
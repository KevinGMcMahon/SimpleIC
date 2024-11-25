import java.util.Scanner;
import src.Logger;

public class InventoryControlSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        char choice;

        // Log application start
        Logger.logInfo("Application started."); //Logger

        Operations.test();
        Logger.logInfo("Operations class test completed."); //Logger

        do {
            // Display the menu options
            System.out.println("=================================");
            System.out.println("Welcome to Simple IC: Inventory Control System");
            System.out.println("a. Add new item to inventory");
            System.out.println("v. View current stock levels");
            System.out.println("u. Update item details");
            System.out.println("d. Delete an item from inventory");
            System.out.println("q. Quit");
            System.out.println("=================================");

            // Read the user's choice
            choice = scanner.next().charAt(0);
            Logger.logInfo("User selected menu option: " + choice); //Logger

            switch (choice) {
                case 'a': // Add a new product
                    System.out.print("Enter product name: ");
                    String name = scanner.next();

                    System.out.print("Enter description: ");
                    String description = scanner.next();

                    int quantity = 0;
                    boolean validInput = false;

                    while (!validInput) {
                        System.out.print("Enter quantity: ");
                        if (scanner.hasNextInt()) {
                            quantity = scanner.nextInt();
                            validInput = true; // Exit the loop if input is valid
                        } else {
                            System.out.println("Invalid input. Please enter an integer value.");
                            Logger.logWarning("Invalid input for quantity."); //Logger
                            scanner.next(); // Clear the invalid input
                        }
                    }

                    System.out.print("Enter price: ");
                    double price = scanner.nextDouble();

                    Logger.logInfo("Adding product: Name=" + name + ", Description=" + description + ", Quantity=" + quantity + ", Price=" + price); //Logger
                    Operations.addProduct(name, description, quantity, price);
                    Logger.logInfo("Product added successfully."); //Logger
                    break;

                case 'v': // View all products
                    Logger.logInfo("Fetching all products from the database."); //Logger
                    Operations.viewProducts();
                    Logger.logInfo("All products fetched successfully."); //Logger
                    break;

                case 'u': // Update a product
                    System.out.print("Enter product ID to update: ");
                    int productId = scanner.nextInt();

                    scanner.nextLine(); // Consume the leftover newline character

                    System.out.print("Enter new product name: ");
                    String newName = scanner.nextLine();

                    System.out.print("Enter new description: ");
                    String newDescription = scanner.nextLine();

                    System.out.print("Enter new quantity: ");
                    int newQuantity = scanner.nextInt();

                    scanner.nextLine(); // Consume the leftover newline character

                    System.out.print("Enter new price: ");
                    double newPrice = scanner.nextDouble();

                    scanner.nextLine(); // Consume the leftover newline character

                    Logger.logInfo("Updating product ID=" + productId + " with new values: Name=" + newName + ", Description=" + newDescription + ", Quantity=" + newQuantity + ", Price=" + newPrice); //Logger
                    Operations.updateProduct(productId, newName, newDescription, newQuantity, newPrice);
                    Logger.logInfo("Product updated successfully."); //Logger
                    break;

                case 'd': // Delete a product
                    System.out.print("Enter product ID to delete: ");
                    int deleteId = scanner.nextInt();

                    Logger.logInfo("Attempting to delete product with ID=" + deleteId); //Logger
                    Operations.deleteProduct(deleteId);
                    Logger.logInfo("Product deleted successfully."); //Logger
                    break;

                case 'q': // Quit the program
                    Logger.logInfo("User exited the system."); //Logger
                    System.out.println("Exiting the system. Thank you!");
                    break;

                default: // Handle invalid input
                    Logger.logWarning("Invalid menu option selected: " + choice); //Logger
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();

        } while (choice != 'q'); // Repeat until the user chooses to quit

        // Log application shutdown
        Logger.logInfo("Application shut down."); //Logger
    }
}

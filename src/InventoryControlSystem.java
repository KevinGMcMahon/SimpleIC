

import java.util.Scanner;

public class InventoryControlSystem {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        char choice;

        Operations.test();

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
                            scanner.next(); // Clear the invalid input
                        }
                    }


                    System.out.print("Enter price: ");
                    double price = scanner.nextDouble();

                    Operations.addProduct(name, description, quantity, price);
                    break;

                case 'v': // View all products
                    Operations.viewProducts();
                    break;

                case 'u': // Update a product
                    System.out.print("Enter product ID to update: ");
                    int productId = scanner.nextInt(); // Read the integer input

                    scanner.nextLine(); // Consume the leftover newline character

                    System.out.print("Enter new product name: ");
                    String newName = scanner.nextLine(); // Read the product name as a string

                    System.out.print("Enter new description: ");
                    String newDescription = scanner.nextLine(); // Read the description as a string

                    System.out.print("Enter new quantity: ");
                    int newQuantity = scanner.nextInt(); // Read the quantity as an integer

                    scanner.nextLine(); // Consume the leftover newline character

                    System.out.print("Enter new price: ");
                    double newPrice = scanner.nextDouble(); // Read the price as a double

                    scanner.nextLine(); // Consume the leftover newline character

                    Operations.updateProduct(productId, newName, newDescription, newQuantity, newPrice);

                    break;

                case 'd': // Delete a product
                    System.out.print("Enter product ID to delete: ");
                    int deleteId = scanner.nextInt();

                    Operations.deleteProduct(deleteId);
                    break;

                case 'q': // Quit the program
                    System.out.println("Exiting the system. Thank you!");
                    break;

                default: // Handle invalid input
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();

        } while (choice != 'q'); // Repeat until the user chooses to quit
    }
}

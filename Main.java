import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String fileName = "inventory.csv";
        String orderFileName = "orders.csv";

        try {
            // Check if the file exists
            File file = new File(fileName);
            if (!file.exists()) {
                System.out.println("File not found. Creating a new inventory file...");
                InventoryCSVHandler.writeToCSV(new ArrayList<>(), fileName);
            }

            // Read from CSV
            List<Drug> inventory = InventoryCSVHandler.readFromCSV(fileName);

            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            while (!exit) {
                // Display Inventory
                inventory.sort(Comparator.comparing(Drug::getExpirationDateAsLocalDate));
                System.out.println("\nCurrent Inventory (sorted by expiration date):");
                for (Drug item : inventory) {
                    System.out.printf(
                            "Name: %s, \n\tQty: %d, \n\tExpiration Date: %s, \n\tPrice: $%.2f, \n\tCategory: %s, \n\tLocation: %s%n\n",
                            item.getName(), item.getQty(), item.getExpirationDate(),
                            item.getPrice(), item.getCategoryLabel(), item.getLocation());
                }

                // Provide options to the user
                System.out.println("\nOptions:");
                System.out.println("1. Add Quantity to a Medicine");
                System.out.println("2. Remove Quantity from a Medicine");
                System.out.println("3. Remove a Medicine Completely");
                System.out.println("4. Add New Medicine");
                System.out.println("5. Order New Medicine");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");

                try {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline

                    switch (choice) {
                        case 1 -> {
                            // Add Quantity
                            System.out.print("Enter the name of the medicine to update: ");
                            String medicineName = scanner.nextLine();
                            Drug drugToUpdate = findDrug(inventory, medicineName);

                            if (drugToUpdate == null) {
                                System.out.println("Medicine not found!");
                            } else {
                                System.out.print("Enter the quantity to add: ");
                                int quantityToAdd = scanner.nextInt();
                                scanner.nextLine(); // Consume the newline

                                System.out.print("Enter the reason for the change: ");
                                String reason = scanner.nextLine();

                                drugToUpdate.updateQuantity(quantityToAdd, reason);
                                InventoryCSVHandler.writeToCSV(inventory, fileName);
                                System.out.println("Inventory updated successfully!");
                            }
                        }
                        case 2 -> {
                            // Remove Quantity
                            System.out.print("Enter the name of the medicine to update: ");
                            String medicineName = scanner.nextLine();
                            Drug drugToUpdate = findDrug(inventory, medicineName);

                            if (drugToUpdate == null) {
                                System.out.println("Medicine not found!");
                            } else {
                                System.out.print("Enter the quantity to remove: ");
                                int quantityToRemove = scanner.nextInt();
                                scanner.nextLine(); // Consume the newline

                                System.out.print("Enter the reason for the change: ");
                                String reason = scanner.nextLine();

                                drugToUpdate.reduceQuantity(quantityToRemove, reason);
                                InventoryCSVHandler.writeToCSV(inventory, fileName);
                                System.out.println("Inventory updated successfully!");
                            }
                        }
                        case 3 -> {
                            // Remove Medicine
                            System.out.print("Enter the name of the medicine to remove completely: ");
                            String medicineName = scanner.nextLine();
                            boolean removed = inventory.removeIf(drug -> drug.getName().equalsIgnoreCase(medicineName));

                            if (removed) {
                                InventoryCSVHandler.writeToCSV(inventory, fileName);
                                System.out.println("Medicine removed successfully!");
                            } else {
                                System.out.println("Medicine not found!");
                            }
                        }
                        case 4 -> {
                            // Add New Medicine
                            System.out.print("Enter the name of the new medicine: ");
                            String name = scanner.nextLine();

                            System.out.print("Enter the quantity (No Units): ");
                            int qty = scanner.nextInt();
                            scanner.nextLine(); // Consume the newline

                            System.out.print("Enter the location: ");
                            String location = scanner.nextLine();

                            System.out.print("Enter the price: $");
                            double price = scanner.nextDouble();
                            scanner.nextLine(); // Consume the newline

                            System.out.print("Enter the expiration date (MM-DD-YYYY): ");
                            String expirationDate = scanner.nextLine();

                            System.out.print(
                                    "Enter the category (1: Prescription Drug, 2: Non-Prescription Drug, 3: Non-Drug Item): ");
                            int category = scanner.nextInt();
                            scanner.nextLine(); // Consume the newline

                            Drug newDrug = new Drug(name, qty, qty / 80.0, "Newly added medicine",
                                    expirationDate, category, "", price, "", location);
                            inventory.add(newDrug);
                            InventoryCSVHandler.writeToCSV(inventory, fileName);
                            System.out.println("New medicine added successfully!");
                        }
                        case 5 -> {
                            System.out.print("Enter the name of the medicine to order: ");
                            String medicineName = scanner.nextLine();
                            Drug drugToUpdate = findDrug(inventory, medicineName);
                            if (drugToUpdate == null) {
                                System.out.println("Medicine not found!");
                            } else {
                                System.out.println("Current Quantity: " + drugToUpdate.getQty());
                                if (drugToUpdate.getQty() > 120) {
                                    System.out.println("Can't order more! Minimum quantity to order more is 120.");
                                } else {
                                    System.out.print("Enter the quantity to order: ");
                                    int quantityToOrder = scanner.nextInt();
                                    scanner.nextLine(); // Consume the newline
                                    System.out.print("Enter the reason for the order: ");
                                    String reason = scanner.nextLine();

                                    drugToUpdate.updateQuantity(quantityToOrder, reason);
                                    InventoryCSVHandler.writeToCSV(inventory, fileName);
                                    InventoryCSVHandler.writeToCSV(drugToUpdate, quantityToOrder, orderFileName);
                                    System.out.println("Inventory updated successfully!");
                                }
                            }
                        }
                        case 6 -> exit = true;
                        default -> System.out.println("Choose one of the options.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number corresponding to one of the options.");
                    scanner.nextLine(); // Clear the invalid input
                }
            }

            System.out.println("Exiting...");
        } catch (IOException e) {
            System.err.println("Error handling CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Drug findDrug(List<Drug> inventory, String name) {
        return inventory.stream()
                .filter(drug -> drug.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}

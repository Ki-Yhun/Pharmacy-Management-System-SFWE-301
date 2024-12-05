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
                // Sort inventory by expiration date (soonest first)
                inventory.sort(Comparator.comparing(Drug::getExpirationDateAsLocalDate));

                // Display Inventory
                System.out.println("\nCurrent Inventory (sorted by expiration date):");
                for (Drug item : inventory) {
                                      System.out.println("Name: " + item.getName());
                                      System.out.println("Quantity: " + item.getQty());
                                      System.out.println("Expiration Date: " + item.getExpirationDate());
                                      System.out.println("Price: $" + String.format("%.2f", item.getPrice()));
                                      System.out.println("Category: " + item.getCategoryLabel());
                                      System.out.println("Location: " + item.getLocation());
                                      System.out.println(); // Add an empty line between inventory items

                           
                }

                // Provide options to the user
                System.out.println("\nOptions:");
                System.out.println("1. Add Quantity to a Medicine");
                System.out.println("2. Remove Quantity from a Medicine");
                System.out.println("3. Remove a Medicine Completely");
                System.out.println("4. Add New Medicine");
                System.out.println("5. Fill Prescription");
                System.out.println("6. Order More Stock");
                System.out.println("7. Exit");
                System.out.print("Choose an option: ");

                try {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline

                    switch (choice) {
                        case 1 -> {
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
                            System.out.print("Enter the name of the medicine to update: ");
                            String medicineName = scanner.nextLine();
                            Drug drugToUpdate = findDrug(inventory, medicineName);

                            if (drugToUpdate == null) {
                                System.out.println("Medicine cannot be found!");
                            } else {
                                System.out.print("Enter the quantity to remove: ");
                                int quantityToRemove = scanner.nextInt();
                                if(quantityToRemove <= 0){
                                    System.out.println("Invalid quantity entered");

                                }else if(quantityToRemove > drugToUpdate.getQty()){
                                    System.out.println("Not Enough Stock to remove");
                                }else{
                                scanner.nextLine(); // Consume the newline
                    
                                System.out.print("Enter the reason for the change: ");
                                String reason = scanner.nextLine();

                                drugToUpdate.reduceQuantity(quantityToRemove, reason);
                                InventoryCSVHandler.writeToCSV(inventory, fileName);
                                System.out.println("Inventory updated successfully!");
                                }
                            }
                        }
                        case 3 -> {
                            System.out.print("Enter the name of the medicine to remove completely: ");
                            String medicineName = scanner.nextLine();
                            boolean removed = inventory.removeIf(drug -> drug.getName().equalsIgnoreCase(medicineName));

                            if (removed) {
                                InventoryCSVHandler.writeToCSV(inventory, fileName);
                                System.out.println("Medicine removed successfully!");
                            } else {
                                System.out.println("Medicine not found.");
                            }
                        }
                            case 4 -> {
                                System.out.print("Enter the name of the new medicine: ");
                                String name = scanner.nextLine();

                                // Check if the medicine already exists
                                if (inventory.stream().anyMatch(drug -> drug.getName().equalsIgnoreCase(name))) {
                                    System.out.println("Medicine already exists.");
                                } else {
                                    System.out.print("Enter the quantity: ");
                                    int qty = scanner.nextInt();
                                    scanner.nextLine(); // Consume the newline

                                    if (qty < 0) {
                                        System.out.println("Invalid data entered: Quantity cannot be negative.");
                                    } else {
                                        System.out.print("Enter the location: ");
                                        String location = scanner.nextLine();

                                        System.out.print("Enter the price: ");
                                        double price = scanner.nextDouble();
                                        scanner.nextLine(); // Consume the newline 
                                        System.out.print("Enter the expiration date (MM-dd-yyyy): ");
                                        String expirationDate = scanner.nextLine();
                                        String month = expirationDate.substring(0, 2);
                                        if(Integer.parseInt(month) > 12){
                                            System.out.println("Invalid expiration date format for: " + name);
                                        }else{
                                        System.out.print("Enter the category (1: Prescription Drug, 2: Non-Prescription Drug, 3: Non-Drug Item): ");
                                        int category = scanner.nextInt();
                                        scanner.nextLine(); // Consume the newline

                                        // Add the new medicine to inventory
                                        Drug newDrug = new Drug(name, qty, qty / 80.0, "Newly added medicine",
                                                expirationDate, category, "", price, "", location);
                                        inventory.add(newDrug);
                                        InventoryCSVHandler.writeToCSV(inventory, fileName);
                                        System.out.println("New medicine added successfully!");
                                        }
                                    }
                                }
                            }

                        case 5 -> {
                            System.out.print("Enter the name of the medicine to fill the prescription: ");
                            String medicineName = scanner.nextLine();

                            // Find the medicine in the inventory
                            Drug drugToFill = inventory.stream()
                                    .filter(drug -> drug.getName().equalsIgnoreCase(medicineName))
                                    .findFirst()
                                    .orElse(null);

                            if (drugToFill == null) {
                                // Medicine does not exist
                                System.out.println("Medicine not found.");
                            } else {
                                System.out.print("Enter the quantity to fill: ");
                                int quantityToFill = scanner.nextInt();
                                scanner.nextLine(); // Consume the newline

                                if (quantityToFill > drugToFill.getQty()) {
                                    // Requested quantity exceeds stock
                                    System.out.println("Not enough stock to fill.");
                                } else {
                                    // Fill the prescription
                                    drugToFill.reduceQuantity(quantityToFill, "Prescription filled");
                                    InventoryCSVHandler.writeToCSV(inventory, fileName);
                                    System.out.printf("Prescription filled for %s. Remaining stock: %d%n",
                                            medicineName, drugToFill.getQty());
                                }
                            }
                        }

                        case 6 -> { // Assuming case 6 is for ordering medicine
                            System.out.print("Enter the name of the medicine to order: ");
                            String medicineName = scanner.nextLine();

                            // Find the medicine in the inventory
                            Drug drugToOrder = inventory.stream()
                                    .filter(drug -> drug.getName().equalsIgnoreCase(medicineName))
                                    .findFirst()
                                    .orElse(null);

                            if (drugToOrder == null) {
                                // Medicine does not exist
                                System.out.println("Medicine not found.");
                            } else {
                                System.out.println("Current Quantity: " + drugToOrder.getQty());
                                System.out.print("Enter the quantity to order: ");
                                int quantityToOrder = scanner.nextInt();
                                scanner.nextLine(); // Consume the newline

                                if (quantityToOrder < 0) {
                                    // Invalid quantity entered
                                    System.out.println("Invalid quantity entered.");
                                } else {
                                    // Process the order
                                    drugToOrder.updateQuantity(quantityToOrder, "Medicine ordered");
                                    InventoryCSVHandler.writeToCSV(inventory, fileName);
                                    InventoryCSVHandler.writeToCSV(drugToOrder,quantityToOrder, "orders.csv");
                                    System.out.printf("Order placed for %s. Updated stock: %d%n",
                                            medicineName, drugToOrder.getQty());
                                }
                            }
                        }

                        case 7 -> exit = true;
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

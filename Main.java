import java.io.*;
import java.util.*;
import java.time.LocalDate;

class InventoryCSVHandler {
    private static final String HEADER = "Name,Quantity (Tablets),Quantity (Boxes),Description,Expiration Date,Category,Category Label,Price Per Tablet, Notes, Location";
    private static final String ORDERHEADER = "Date,Name,Order Quantity,ID,Status";

    // Write inventory items to CSV
    public static void writeToCSV(List<Drug> inventory, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write header
            writer.write(HEADER);
            writer.newLine();

            // Write items
            for (Drug item : inventory) {
                writer.write(item.toCsvString());
                writer.newLine();
            }
        }
    }

    public static void writeToCSV(Drug drugToUpdate, int quantityToOrder, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write header
            writer.write(ORDERHEADER);
            writer.newLine();

            LocalDate time = LocalDate.now();
            Random random = new Random();

            // Generate a random number between 10000 and 99999 (inclusive)
            int orderID = random.nextInt(90000) + 10000;

            String orderLine = String.format("%s,\n\t%s,\n\t%d,\n\t%d,\n\t%s",
                    time.toString(),
                    drugToUpdate.getName(),
                    quantityToOrder,
                    orderID,
                    "Delivered");

            writer.write(orderLine);
            writer.newLine();

        }
    }

    // Read inventory items from CSV
    public static List<Drug> readFromCSV(String fileName) throws IOException {
        List<Drug> inventory = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");

                    Drug item = new Drug(
                            parts[0].replace(";", ","), // name
                            Integer.parseInt(parts[1]), // qty tablets
                            Double.parseDouble(parts[2]), // Qty Box
                            parts[3].replace(";", ","), // description
                            parts[4].replace(";", ","), // expiration date
                            Integer.parseInt(parts[5]), // Category
                            parts[6].replace(";", ","), // Category Label // Category Label
                            Double.parseDouble(parts[7]), // price
                            parts[8].replace(";", ","), // notes
                            parts[9].replace(";", ",") // location
                    );
                    inventory.add(item);

                } catch (NumberFormatException e) {
                    System.err.println("Error parsing line: " + line);
                }
            }
        }
        return inventory;
    }

}


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

    private static Drug findDrug(List<Drug> inventory, String name) {
        return inventory.stream()
                .filter(drug -> drug.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}

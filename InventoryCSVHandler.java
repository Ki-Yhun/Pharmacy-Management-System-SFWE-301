import java.util.*;
import java.time.LocalDate;
import java.io.*;

public class InventoryCSVHandler {
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

    //write to the orders.csv
    public static void writeToCSV(Drug drugToUpdate, int quantityToOrder, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write header
            writer.write(ORDERHEADER);
            writer.newLine();

            LocalDate time = LocalDate.now();
            Random random = new Random();

            // Generate a random number between 10000 and 99999 (inclusive)
            int orderID = random.nextInt(90000) + 10000;

            String orderLine = String.format("%s,%s,%d,%d,%s",
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

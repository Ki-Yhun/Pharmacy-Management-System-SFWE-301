import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Prescription {

    private String medicineName, patientName, pharmacistName, description;
    private int quantity = 0, maxQuantity = 0, ID;
    private LocalDate expirationDate;
     
    public Prescription(String medicineName, int maxQuantity, int ID, LocalDate expirationDate, String patientName, String pharmacistName, String description) {
        this.medicineName = medicineName;
        this.patientName = patientName;
        this.pharmacistName = pharmacistName;
        this.maxQuantity = maxQuantity;
        this.ID = ID;
        this.expirationDate = expirationDate;
        this.description = description;
    }

    public boolean fill(int quantity){
        this.quantity = quantity;
        String fileName = "inventory.csv";

        try{
            List<Drug> inventory = InventoryCSVHandler.readFromCSV(fileName);
            Drug drugToUpdate = Main.findDrug(inventory, medicineName);
            drugToUpdate.reduceQuantity(quantity, "Prescription Fill");
            InventoryCSVHandler.writeToCSV(inventory, fileName);
            if(quantity != maxQuantity){return false;}
            return true;

        }
        catch (IOException e) {
            System.err.println("Error handling CSV file: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public LocalDate getExpirationDate(){
        return this.expirationDate;
    }

    public String getName(){
        return this.medicineName;
    }

    public int getQuantity(){
        return this.quantity;
    }

    public int getMaxQuantity(){
        return this.maxQuantity;
    }

    public int getID(){
        return this.ID;
    }
}

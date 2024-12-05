import java.io.*;
import java.util.*;

public class Prescription {

    private String medicineName, patientName, pharmacistName;
    private int quantity = 0, ID;
    private String expirationDate, description;
     
    public Prescription(String medicineName, int quantity, int ID, String expirationDate, String patientName, String pharmacistName, String description) {
        this.medicineName = medicineName;
        this.patientName = patientName;
        this.pharmacistName = pharmacistName;
        this.quantity = quantity;
        this.ID = ID;
        this.expirationDate = expirationDate;
        this.description = description;
    }

    public void fill(int quantity){
        this.quantity += quantity;
        String fileName = "inventory.csv";

        try{
            List<Drug> inventory = InventoryCSVHandler.readFromCSV(fileName);
            Drug drugToUpdate = Main.findDrug(inventory, medicineName);

            if(drugToUpdate == null){
                System.out.println("Medicine not found!");
            }
            else{
                drugToUpdate.reduceQuantity(quantity, "Prescription Fill");
                InventoryCSVHandler.writeToCSV(inventory, fileName);
            } 
        }
        catch (IOException e) {
            System.err.println("Error handling CSV file: " + e.getMessage());
            e.printStackTrace();
        }
        
        
    }

    public String getName(){
        return this.medicineName;
    }

    public int getQuantity(){
        return this.quantity;
    }
}

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BackendSystem {
    final static HashMap map = new HashMap<>();

    public static String currentUser = "Null"; 
    public static boolean loggedIn = false;

    //Admin logging 
    private String filepath = "log.csv";
    private String patientFilePath = "patientData.csv";
    private File file = new File(filepath);
    private File patientFiles = new File(patientFilePath);
    private String[] headers = {"Timestamp", " Username", " Action"};
    private String[] patientHeaders = {"Username", " Rx ID", " Prescription", "Current Quantity", " Max Quantity", " Records"};
    private boolean fileExists = file.exists();
    private boolean patientFileExists = file.exists();

    BackendSystem() {
        Account defaultUser = new Account(Account.Roles.PharmacistManager, "Pickles#4");
        map.put("DefaultUser", defaultUser);
        
        try (FileWriter writer = new FileWriter(filepath, true)) {
            if(!fileExists) {
                writer.write(String.join(", ", headers) + "\n");
            }
        }  
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try (FileWriter writer = new FileWriter(patientFilePath, true)) {
            if(!fileExists) {
                writer.write(String.join(", ", patientHeaders) + "\n");
            }
        }  
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
    }

    public boolean Login(String username, String password) {
        Account user = (Account)map.get(username);

        if(user.getLockOut() < 5) {
            if (user != null && user.checkPassword(password)) {
                //Logout();
                currentUser = username;
                loggedIn = true; 
                user.resetLockOut();
                writeToLog(username, "LoggedIn");
                return true;
                
            }
            else {
                user.incrementLockOut();
            }
        }
        return false;
    }

    public boolean Logout() {
        if (!currentUser.equals("Null") && loggedIn == true) {
            writeToLog(currentUser, "LoggedOut");
            currentUser = "Null";
            loggedIn = false;
            return true;
        }
        return false;
    }

    public boolean createAccount(Account.Roles role, String username, String password) {
        Account user = (Account)map.get(currentUser);
        if (user.getJobRole() == Account.Roles.PharmacistManager && loggedIn) {
            Account newUser = new Account(role, password);
            if (map.get(username) == null) {
                map.put(username, newUser);
                return true;
            }
        }
        return false;
    }

    public boolean createAccount(String username) {
        Account user = (Account)map.get(currentUser);
        if (loggedIn) {
            Account newUser = new Account();
            if (map.get(username) == null) {
                map.put(username, newUser);
                return true;
            }
        }
        return false;
    }

    public boolean deleteAccount(String username) {
        Account user = (Account)map.get(currentUser);
        if (user.getJobRole() == Account.Roles.PharmacistManager && loggedIn && username != currentUser) {
            map.remove(username);
            return true;
        }
        return false;
    }

    public boolean unlockAccount(String username) {
        Account user = (Account)map.get(currentUser);
        if(user.getJobRole() == Account.Roles.PharmacistManager && loggedIn) {
            if ((Account)map.get(username) != null) {
                ((Account)map.get(username)).resetLockOut();
                return true;
            }
        }
        return false;
    }

    private void writeToLog(String username, String action) {
        try (FileWriter writer = new FileWriter(filepath, true)) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();             
            writer.write(formatter.format(date) + ": " + username + ", " + action + "\n");
        }
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void updatePasswordExpire(String username, LocalDate amount) {
        Account user = (Account)map.get(currentUser);
        if (user.getJobRole() == Account.Roles.PharmacistManager && loggedIn && map.get(username) != null) {
            ((Account)map.get(username)).updatePasswordExpire(amount);
        }
    }

    public boolean editAccountInfo(String username, String[] records) {
        if(map.get(username) != null && map.get(currentUser) != null) {
            if ( ((Account)map.get(username)).getJobRole() == Account.Roles.Patient || 
                ((Account)map.get(currentUser)).getJobRole() == Account.Roles.PharmacistManager) {
                    ((Account)map.get(username)).updateRecords(records);
                    return true;
            }
        }
        return false;
    }

    public String readAccountInfo(String username) {
        if (loggedIn) {
            return ((Account)map.get(username)).displayRecords();
        }
        return null;
    }

    private boolean writeToPatientData(String username, Prescription prescription) {
        try (FileWriter writer = new FileWriter(patientFilePath, true)) {             
            writer.write(username + ", " + prescription.getID() + ", " + prescription.getName() + ", " + prescription.getQuantity() + ", " + prescription.getMaxQuantity() + ", " + ((Account)map.get(username)).displayRecords() + "\n");
        }
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean createPrescription(String userName, String medicineName, int quantity, int ID, LocalDate expirationDate, String patientName, String pharmacistName, String description){
        Account user = (Account)map.get(currentUser);

        if(expirationDate.isBefore(LocalDate.now())){
            return false;
        }

        if((user.getJobRole() == Account.Roles.PharmacistManager || user.getJobRole() == Account.Roles.Pharmacist) && loggedIn){
            if (!isInStock(medicineName)){return false;}

            Account patient = (Account)map.get(patientName);
            Prescription newRx = new Prescription(medicineName, quantity, ID, expirationDate, patientName, pharmacistName, description);
            patient.setPrescription(newRx);
            writeToPatientData(patientName, newRx);
            writeToLog(userName, "created Prescription");
            return true;
        }
        return false;
    }

    public boolean fillPrescription(String patientName, int quantity){
        Account user = (Account)map.get(currentUser);

        if((user.getJobRole() == Account.Roles.PharmacistManager || user.getJobRole() == Account.Roles.Pharmacist) && loggedIn){
            Account patient = (Account)map.get(patientName);
            Prescription Rx = patient.getPrescription();
            if(Rx.getExpirationDate().isBefore(LocalDate.now())){
                return false;
            }

            if(Rx.fill(quantity)){
                clearPatientData();
                writeToPatientData(patientName, Rx);
                writeToLog(currentUser, "filled Prescription");
                return true;
            }
        }
        return false;
    }

    public void clearPatientData(){

        try (FileWriter writer = new FileWriter(patientFilePath)) {
            if(!fileExists) {
                writer.write("");
            }
        }  
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try (FileWriter writer = new FileWriter(patientFilePath, true)) {
                writer.write(String.join(",", patientHeaders) + "\n");
        }  
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public boolean isInStock(String medicineName){
        String fileName = "inventory.csv";
        try{
            List<Drug> inventory = InventoryCSVHandler.readFromCSV(fileName);
            Drug drugToUpdate = Main.findDrug(inventory, medicineName);

            if(drugToUpdate == null){
                return false;
            }
        }
        catch (IOException e) {
            System.err.println("Error handling CSV file: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    
}

import java.time.LocalDate;

public class Account {
    public enum Roles {Pharmacist, Patient, PharmacistTech, PharmacistManager, Cashier}
    
    public static int nextID = 10000; 

    private int ID;
    private Roles jobRoles;
    private String password = null;
    private LocalDate passwordExpire = null;
    private int failedAttempts = 0; 
    private Prescription prescription;

    private String[] records = {"Name: ", "Birthdate: ", "Email: ", "Phone Number: ", "Medical Conditions: N/A", 
    "Presriptions", "Doctor: N/A", "Patient History: N/A", "Licenses and Certifications: N/A", "License Status: N/A"}; 

    Account() {
        ID = nextID;
        nextID++; 
        jobRoles = Roles.Patient;

    }

    Account(Roles role, String password) {
        ID = nextID;
        nextID++; 
        jobRoles = role; 
 
        this.password = password;
        this.passwordExpire = LocalDate.now().plusYears(1);
       // this.username = username;
    }

    public Roles getJobRole() {
        return jobRoles;
    }

    public void setPassword(String password) {
        this.password = password;
        this.passwordExpire = LocalDate.now().plusYears(1);
    }

    public boolean checkPassword(String password) {
        LocalDate curDate = LocalDate.now();
        if (passwordExpire.isAfter(curDate)) {
            return this.password.equals(password);
        }
        return false;
    }

    public int getLockOut() {
        return failedAttempts;
    }

    public void resetLockOut() {
        failedAttempts = 0;
    }

    public void incrementLockOut() {
        failedAttempts++;
    }

    public String displayRecords() {
        String str = String.join(", ", records);
        return str;
    }

    public void updateRecords(String[] records) {
        this.records = records; 
    }

    //used for testing checkpassword 
    public void updatePasswordExpire(LocalDate newDate) {
        this.passwordExpire = newDate;
    }

    public Prescription getPrescription(){
        return this.prescription;
    }

    public void setPrescription(Prescription Rx){
        this.prescription = Rx;
    }
}

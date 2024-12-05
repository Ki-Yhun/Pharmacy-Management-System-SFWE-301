import java.time.LocalDate;

public class BESTest {
    
    
    public static void main(String[] args) {
        BackendSystem sys = new BackendSystem();
        int i;

        System.out.println("\n");
        //Testcase 1.1: Authentication / Access Log Storage

            // Login with Default Admin
            if (!sys.Login("DefaultUser", "Pickles#4")) {
                System.out.println("Test 1.1.1 Failed: could not login with DefaultUser");
                return;
            }
            if(!sys.Logout()) {
                System.out.println("Test 1.1.2 Failed: Could not logout");
                return;
            }
            if (!sys.Login("DefaultUser", "Pickles#4")) {
                System.out.println("Test 1.1.3 Failed: could not login with DefaultUser");
                return;
            }
            
        //Testcase 1.2: Create Accounts, Account Types, Employee Account 
            
            //Create another account
            if (!sys.createAccount(Account.Roles.Cashier, "Hannibal Barca", "AlpineElephants")) {
                System.out.println("Test 1.2.1 Failed: Could not create Cashier account");
            }
            sys.Login("Hannibal Barca", "AlpineElephants");

            //check if cashier can create employee account
            if (sys.createAccount(Account.Roles.PharmacistTech, "Carthagian Hegemony", "2ndPunicWar")) {
                System.out.println("Test 1.2.2 Failed: Non-Pharmacist Manager successfully created account");
                //sys.Login("Carthagian Hegemony", password)
                return;
            }

            //check if cashier can create patient account
            if (!sys.createAccount("Scipio Africanus")) {
                System.out.println("Test 1.2.3 Failed: Employee unable to create patient account");
                return;
            }
            
        
        //Testcase 1.3: Track Failed Attempts, Failed Password Lock, Authentication

            //log in with Manager account and create Pharmacist account
            sys.Login("DefaultUser", "Pickles#4");
            sys.createAccount(Account.Roles.Pharmacist, "Heinz Doofenshmirtz", "PerryThePlatypus!!!");

            //fail login 5 times
            for (i = 0; i < 5; i++) {
                if (sys.Login("Heinz Doofenshmirtz", "APlatypus?")) {
                    System.out.println("Test 1.3.1 Failed: Login with invalid password");
                    return;
                }
            }

            //check if account is locked
            if (sys.Login("Heinz Doofenshmirtz", "PerryThePlatypus!!!")) {
                System.out.println("Test 1.3.2 Failed: Login with locked out account");
                return;
            }
            
            //check if account can be unlocked
            if (!sys.unlockAccount("Heinz Doofenshmirtz")) {
                System.out.println("Test 1.3.3 Failed: Cannot unlock account");
                return;
            }

            //check if unlocked account can be logged into
            if (!sys.Login( "Heinz Doofenshmirtz", "PerryThePlatypus!!!")) {
                System.out.println("Test 1.3.4 Failed: Could not login with unlocked account");
                return;
            }
            sys.Login("DefaultUser", "Pickles#4");

            //fail login 3 times
            for (i = 0; i < 3; i++) {
                if (sys.Login("Heinz Doofenshmirtz", "APlatypus?")) {
                    System.out.println("Test 1.3.5 Failed: Login with invalid password");
                    return;
                }
            }
            //check if unlocked account can be logged into
            if (!sys.Login( "Heinz Doofenshmirtz", "PerryThePlatypus!!!")) {
                System.out.println("Test 1.3.6 Failed: Could not login with unlocked account");
                return;
            }


        //Testcase 1.4: Force Password Change
            sys.Login("DefaultUser", "Pickles#4");
            sys.createAccount(Account.Roles.PharmacistTech, "Steve", "123456789");
            sys.updatePasswordExpire("Steve", LocalDate.now().minusYears(2)); 
            if (sys.Login("Steve", "123456789")) {
                System.out.println("Test 1.4.1 Failed: Login with Expired password");
                return;
            }
            sys.updatePasswordExpire("Steve", LocalDate.now().plusYears(2));
            if (!sys.Login("Steve", "123456789")) {
                System.out.println("Test 1.4.2 Failed: Cannot login with unexpired password");
                return;
            }

        //Testcase 1.5: Account Storage, Patient Account, Employee Account, Pharmacy Account

            String[] info = {"Name: Hannibal Barca ", "Birthdate: 247 BC", "Email: HBarca@website.com", "Phone Number: N/A", 
            "Medical Conditions: Mental exhaustion and deteriorating health after years of campaigning in Italy", 
            "Presriptions: War Elephants", "Doctor: Baal", "Patient History: No previous visits", "Licenses and Certifications: N/A", "License Status: N/A"};
            
            sys.Login("Steve", "123456789");
            sys.editAccountInfo("Hannibal Barca", info);
            if(sys.readAccountInfo("Hannibal Barca").equals(String.join(", ", info))) {
                System.out.println("Test 1.5.1 Failed: Non pharmacist manager account modified employee data");
                return;
            }

            sys.Login("DefaultUser", "Pickles#4");
            sys.editAccountInfo("Hannibal Barca", info);
            
            if(!sys.readAccountInfo("Hannibal Barca").equals(String.join(", ", info))) {
                System.out.println("Test 1.5.2 Failed: Pharmacist manager could not modfity employee data");
                return;
            }

        //Testcase 1.6: Account Creation / Deletion

            //Non-pharmacy manager cannot delete other accounts
            sys.Login("Steve", "123456789");
            if(sys.deleteAccount("HDoofenshmirtz")) {
                System.out.println("Test 1.6.1 Failed: Non-Pharmacy Manager deleted an account");
            }
            
            //Pharmacy manager can delete other accounts
            sys.Login("DefaultUser", "Pickles#4");
            if(!sys.deleteAccount("HDoofenshmirtz")) {
                System.out.println("Test 1.6.2 Failed: Pharmacy Manager unable to delete account");
            }

            //Cannot delete own account
            if(sys.deleteAccount("DefaultUser")) {
                System.out.println("Test 1.6.3 Failed: Deleted own account");
            }

        //Successfully completed all testcases
        sys.Logout();
        System.out.println("Test 1: All testcases successfully passed");

        //Clear any data in patientData for testing
        sys.clearPatientData();

        //Testcase 2.1: Prescription Creation and Prescriptions Added to Patient Data csv
        String[] info1 = {"Name: John Smith ", "Birthdate: October 12 1992", "Email: JSmith@website.com", "Phone Number: N/A", 
            "Medical Conditions: Trouble breathing during workouts, low VO2 max", 
            "Presriptions: Albuterol", "Doctor: Mike Rogers", "Patient History: No previous visits", "Licenses and Certifications: N/A", "License Status: N/A"};

        String[] info2 = {"Name: Adam Sandler ", "Birthdate: May 27 2004", "Email: JSmith@website.com", "Phone Number: N/A", 
            "Medical Conditions: Trouble breathing during workouts, low VO2 max", 
            "Presriptions: Albuterol", "Doctor: Mike Rogers", "Patient History: No previous visits", "Licenses and Certifications: N/A", "License Status: N/A"};

        sys.Login("DefaultUser", "Pickles#4"); 
        
        sys.createAccount(Account.Roles.Patient, "John Smith", "123456789");
        sys.editAccountInfo("John Smith", info1);
        if(!sys.createPrescription("DefaultUser", "Albuterol", 2, 9835, LocalDate.now().plusYears(1), "John Smith", "Albert Einstein", "Inhale 2 puffs every 4 hours, if needed, for wheezing, coughing, increased work, or difficulty breahting")){
            System.out.println("Test 2.1.1 Failed: Did not create a new prescription");
            return;
        }

        //Testcase 2.1.2 Prescriptions not in inventory will not be created
        sys.createAccount(Account.Roles.Patient, "Adam Sandler", "123456789");
        sys.editAccountInfo("Adam Sandler", info2);
        if(sys.createPrescription("DefaultUser", "Flonase", 50, 829, LocalDate.now().plusYears(1), "Adam Sandler", "Albert Einstein", "Take 1-2 pills every 24 hours, if needed, for allergies.")){
            System.out.println("Test 2.1.2 Failed: Created new prescription when medicine is not available");
            return;
        }

        //Testcase 2.2: Prescription fill / Authentication check
        if(!sys.fillPrescription("John Smith", 2)){
            System.out.println("Test 2.2.1 Failed: Did not fill prescription");
            return;
        }

        //Testcase 2.3 Non-Authenticated users shall not create prescriptions
        sys.Login("John Smith", "123456789");
        if(sys.createPrescription("DefaultUser", "Acetaminophen", 100, 8370, LocalDate.now().plusYears(1), "John Smith", "Albert Einstein", "Inhale 2 puffs every 4 hours, if needed, for wheezing, coughing, increased work, or difficulty breahting")){
            System.out.println("Test 2.3.1 Failed: Created a new prescription");
            return;
        }


        sys.Logout();
        System.out.println("Test 2: All testcases successfully passed");
    } //Main Bracket
} // Class Bracket

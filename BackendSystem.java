package PharmMgmtSys;

import PharmMgmtSys.Account.Roles;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

public class BackendSystem {
    private static HashMap map = new HashMap<>();

    public static String currentUser = "Null"; 
    public static boolean loggedIn = false;

    //Admin logging 
    private String filepath = "log.csv";
    private File file = new File(filepath);
    private String[] headers = {"Timestamp", "Username", "Action"};
    private boolean fileExists = file.exists();

    BackendSystem() {
        Account defaultUser = new Account(Roles.PharmacistManager, "Pickles#4");
        map.put("DefaultUser", defaultUser);
        
        try (FileWriter writer = new FileWriter(filepath, true)) {
            if(!fileExists) {
                writer.write(String.join(",", headers) + "\n");
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

    public boolean createAccount(Roles role, String username, String password) {
        Account user = (Account)map.get(currentUser);
        if (user.getJobRole() == Roles.PharmacistManager && loggedIn) {
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
        if (user.getJobRole() == Roles.PharmacistManager && loggedIn && username != currentUser) {
            map.remove(username);
            return true;
        }
        return false;
    }

    public boolean unlockAccount(String username) {
        Account user = (Account)map.get(currentUser);
        if(user.getJobRole() == Roles.PharmacistManager && loggedIn) {
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
        if (user.getJobRole() == Roles.PharmacistManager && loggedIn && map.get(username) != null) {
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

    public String[] readAccountInfo(String username) {
        if (loggedIn) {
            return ((Account)map.get(username)).displayRecords();
        }
        return null;
    }
}

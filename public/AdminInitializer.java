import java.util.HashMap;
import java.util.Map;

public class AdminInitializer {
    public static void initializeAdmin(Map<String, String> users, Map<String, Boolean> paidUsers) {
        String adminUsername = "Admin";
        String adminPassword = "Mk993300##";
        
        // Add admin to the "database"
        users.put(adminUsername, adminPassword);
        paidUsers.put(adminUsername, true);
        
        System.out.println("Admin account initialized!");
    }
}
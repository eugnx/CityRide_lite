
public class Admin {

    private static final String DEFAULT_PASSWORD = "admin";

    private String password;


    //Constructs an Admin with the default password.

    public Admin() {
        this.password = DEFAULT_PASSWORD;
    }

    // Verifies whether the supplied password matches the stored password.

    public boolean verifyPassword(String attempt) {
        return password != null && password.equals(attempt);
    }


    // Updates the admin password after verifying the current one.

    public boolean changePassword(String currentPassword, String newPassword) {
        if (!verifyPassword(currentPassword)) {
            return false;
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }
        this.password = newPassword.trim();
        return true;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

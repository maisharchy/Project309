package backend.demo2.ProfileSettings;

public class UpdatePasswordRequest {
    private String currentPassword;
    private String newPassword;

    // Getters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    // Setters
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

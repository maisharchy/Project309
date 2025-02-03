package backend.demo2.ProfileSettings;

public class EditProfileRequest {
    private String firstname;
    private String lastname;
    private String username;
    private String bio;
    private String avatar;
    private String userTitle;

    // Getters
    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUserTitle() {
        return userTitle;
    }

    // Setters
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }
}

package backend.demo2.User;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;
import backend.demo2.ProfileSettings.Profile;

/**
 * @author Maisha Rahman Chowdhury
 */
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Integer id;

    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private String firstname;

    @NonNull
    private String lastname;

    @Column(name = "logged_in") // Maps to the 'logged_in' column in the database
    private Boolean loggedIn;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;

    // Default constructor is required by JPA
    public User() {
    }

    public User(String username, String email, String password, String firstname, String lastname) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }
    public User(Integer id) {
        this.id = id; // Set ID for existing user
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    @NonNull
    public String getFirstname() {
        return firstname;
    }

    @NonNull
    public String getLastname() {
        return lastname;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    public void setFirstname(@NonNull String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(@NonNull String lastname) {
        this.lastname = lastname;
    }

    public Boolean isLoggedIn() {
        return loggedIn; // Getter for logged_in field
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn; // Setter for logged_in field
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }


}

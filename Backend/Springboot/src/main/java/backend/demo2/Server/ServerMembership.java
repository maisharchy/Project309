package backend.demo2.Server;

import backend.demo2.ProfileSettings.Profile;
import backend.demo2.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference; // Import this
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ServerMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Primary Key

    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    @JsonBackReference // This annotation prevents serialization of the back-reference
    private Server server;  // Reference to the Server entity

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Reference to the User entity

    @Column(nullable = false)
    private String role;  // e.g., admin, player, moderator

    @Column(nullable = true)
    private Integer score;  // Score of the user in the server


    @ManyToMany
    @JoinTable(
            name = "membership_profiles",
            joinColumns = @JoinColumn(name = "membership_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Profile> profiles = new HashSet<>(); // A set to hold associated profiles
    // Constructors, Getters, Setters
    public ServerMembership() {}

    public ServerMembership(Server server, User user, String role, Integer score) {
        this.server = server;
        this.user = user;
        this.role = role;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
    public Set<Profile> getProfiles() {
        return profiles; // Add this method to access profiles
    }
}

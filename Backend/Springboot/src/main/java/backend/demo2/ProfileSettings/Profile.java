package backend.demo2.ProfileSettings;

import backend.demo2.User.User;
import backend.demo2.Server.ServerMembership;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Profiles")
public class Profile {

    @Id
    @Column(name = "user_id")
    private int userId;  // Primary key and foreign key referencing Users(id)

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String bio;
    private String avatar;


    @ManyToMany(mappedBy = "profiles")
    private Set<ServerMembership> serverMemberships = new HashSet<>();

    // Constructors
    public Profile() {}

    public Profile(User user, String bio, String avatar) {
        this.user = user;
        this.bio = bio;
        this.avatar = avatar;

    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio != null ? bio.trim() : null;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = (avatar != null) ? avatar.replaceAll("\\s+$", "") : null;
    }


    public Set<ServerMembership> getServerMemberships() {
        return serverMemberships;
    }

    public void setServerMemberships(Set<ServerMembership> serverMemberships) {
        this.serverMemberships = serverMemberships;
    }
}

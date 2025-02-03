package backend.demo2.Server;

import backend.demo2.User.User;
import com.fasterxml.jackson.annotation.JsonManagedReference; // Import this
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // This will auto-generate the serverId
    private Integer serverId;  // Auto-incremented primary key

    @Column(nullable = false)
    private String name;

    // One-to-many relationship with ServerMembership
    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // This annotation indicates the managed side of the relationship
    private List<ServerMembership> memberships = new ArrayList<>(); // Initialize the list

    // Default constructor
    public Server() {
        this.memberships = new ArrayList<>(); // Initialize the memberships list
    }

    // Constructor with name parameter
    public Server(String name) {
        this.name = name;
        this.memberships = new ArrayList<>(); // Initialize the memberships list
    }

    // Getters and Setters
    public Integer getServerId() {
        return serverId;  // This is now the auto-generated ID
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ServerMembership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<ServerMembership> memberships) {
        this.memberships = memberships;
    }
}

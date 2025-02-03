package backend.demo2.ProfileSettings;

import backend.demo2.Server.ServerMembership;
import backend.demo2.User.User;

import java.util.List;

// ProfileDTO for display
public class ProfileDTO {
    private final User user;
    private final Profile profile;
    private final List<ServerMembership> memberships;

    public ProfileDTO(User user, Profile profile, List<ServerMembership> memberships) {
        this.user = user;
        this.profile = profile;
        this.memberships = memberships;
    }

    // Getters
    public User getUser() {
        return user;
    }

    public Profile getProfile() {
        return profile;
    }

    public List<ServerMembership> getMemberships() {
        return memberships;
    }
}





package backend.demo2.ProfileSettings;

import backend.demo2.Complaint.ComplaintRepository;
import backend.demo2.GuessTheNumber.PlayerScoreRepository;
import backend.demo2.ProblemReport.ProblemReportRepository;
import backend.demo2.Server.ServerMembershipRepository;
import backend.demo2.Server.ServerRepository;
import backend.demo2.Trivia.TriviaGameplayRepository;
import backend.demo2.User.User;
import backend.demo2.User.UserRepository;
import backend.demo2.Server.ServerMembership;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile Controller", description = "Manage user profiles")
public class ProfileController {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private ServerMembershipRepository serverMembershipRepository;

    @Autowired
    private ProblemReportRepository problemReportRepository;

    @Autowired
    private TriviaGameplayRepository triviaGameplayRepository;

    @Autowired
    private PlayerScoreRepository playerScoreRepository;

    @Operation(summary = "Get user profile", description = "Retrieve the profile information of a user by their ID")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@Parameter(description = "ID of the user to be retrieved") @PathVariable int userId) {
        Optional<Profile> profileOpt = profileRepository.findById(userId);
        if (profileOpt.isEmpty()) return ResponseEntity.notFound().build();

        Profile profile = profileOpt.get();
        User user = profile.getUser();
        List<ServerMembership> memberships = serverRepository.findMembershipsByUserId(userId);

        return ResponseEntity.ok(new ProfileDTO(user, profile, memberships));
    }

    @Operation(summary = "Create user profile", description = "Create a new profile for the user")
    @PostMapping("/createProfile/{userId}")
    public ResponseEntity<?> createProfile(@Parameter(description = "ID of the user to create the profile for") @PathVariable int userId,
                                           @RequestBody Profile profileData) {
        Optional<User> userOpt = userRepository.findById((long) userId);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        User user = userOpt.get();
        profileData.setUser(user); // Associate the profile with the user
        profileRepository.save(profileData);

        return ResponseEntity.ok("Profile created successfully.");
    }

    @Operation(summary = "Update user bio", description = "Update the bio of a user's profile")
    @PutMapping("/{userId}/edit/bio")
    public ResponseEntity<?> updateBio(@Parameter(description = "ID of the user whose bio is to be updated") @PathVariable int userId,
                                       @RequestBody UpdateBioRequest request) {
        Optional<Profile> profileOpt = profileRepository.findById(userId);
        if (profileOpt.isEmpty()) return ResponseEntity.notFound().build();

        Profile profile = profileOpt.get();
        profile.setBio(request.getBio());
        profileRepository.save(profile);

        return ResponseEntity.ok("Bio updated successfully.");
    }

    @Operation(summary = "Update user avatar", description = "Update the avatar of a user's profile")
    @PutMapping("/{userId}/edit/avatar")
    public ResponseEntity<?> updateAvatar(@Parameter(description = "ID of the user whose avatar is to be updated") @PathVariable int userId,
                                          @RequestBody UpdateAvatarRequest request) {
        Optional<Profile> profileOpt = profileRepository.findById(userId);
        if (profileOpt.isEmpty()) return ResponseEntity.notFound().build();

        Profile profile = profileOpt.get();
        profile.setAvatar(request.getAvatar());
        profileRepository.save(profile);

        return ResponseEntity.ok("Avatar updated successfully.");
    }

    @Operation(summary = "Update username", description = "Update the username of a user")
    @PutMapping("/{userId}/edit/username")
    public ResponseEntity<?> updateUsername(@Parameter(description = "ID of the user whose username is to be updated") @PathVariable int userId,
                                            @RequestBody UpdateUsernameRequest request) {
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        User user = userOpt.get();
        user.setUsername(request.getUsername());
        userRepository.save(user);

        return ResponseEntity.ok("Username updated successfully.");
    }

    @Operation(summary = "Update user name", description = "Update the first and last name of a user")
    @PutMapping("/{userId}/edit/name")
    public ResponseEntity<?> updateName(@Parameter(description = "ID of the user whose name is to be updated") @PathVariable int userId,
                                        @RequestBody UpdateNameRequest request) {
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        User user = userOpt.get();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        userRepository.save(user);

        return ResponseEntity.ok("Name updated successfully.");
    }

    @Operation(summary = "Update user password", description = "Update the password of a user")
    @PutMapping("/{userId}/update-password")
    public ResponseEntity<?> updatePassword(@Parameter(description = "ID of the user whose password is to be updated") @PathVariable int userId,
                                            @RequestBody UpdatePasswordRequest request) {
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        User user = userOpt.get();
        if (!request.getCurrentPassword().equals(user.getPassword())) {
            return ResponseEntity.status(403).body("Incorrect current password.");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.ok("Password updated successfully.");
    }

    @Operation(summary = "Delete user account", description = "Delete the user account including all related data")
    @DeleteMapping("/{userId}/delete-account")
    @Transactional
    public ResponseEntity<?> deleteAccount(@Parameter(description = "ID of the user to delete the account") @PathVariable int userId,
                                           @RequestBody DeleteAccountRequest request) {
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        User user = userOpt.get();
        if (!request.getPassword().equals(user.getPassword())) {
            return ResponseEntity.status(403).body("Incorrect password.");
        }
        playerScoreRepository.deleteByUser(user);
        complaintRepository.deleteByComplainedUserId(userId);
        complaintRepository.deleteByComplainantUserId(userId);
        problemReportRepository.deleteByUserId(userId);
        profileRepository.deleteById(userId);
        // Delete any server memberships the user has
        List<ServerMembership> memberships = serverRepository.findMembershipsByUserId(userId);
        for (ServerMembership membership : memberships) {
            serverMembershipRepository.delete(membership);
        }
        triviaGameplayRepository.deleteByUserId(Long.valueOf(userId));
        userRepository.deleteById(Long.valueOf(userId));

        return ResponseEntity.ok("Account deleted successfully.");
    }
}

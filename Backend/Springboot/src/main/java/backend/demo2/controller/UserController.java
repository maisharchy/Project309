package backend.demo2.controller;

import backend.demo2.model.SignUpUser;
import backend.demo2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint for Signup (Registering a new user)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpUser user) {
        boolean isRegistered = userService.registerUser(user);
        if (isRegistered) {
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User registration failed.");
        }
    }

    // Endpoint for Getting User by Username
    @GetMapping("/{username}")
    public ResponseEntity<SignUpUser> getUser(@PathVariable String username) {
        SignUpUser user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }

    // Endpoint for Logging Out
    @PostMapping("/logout/{username}")
    public ResponseEntity<String> logout(@PathVariable String username) {
        boolean isLoggedOut = userService.logoutUser(username);
        if (isLoggedOut) {
            return ResponseEntity.ok("Logged out successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logout failed. User may already be logged out.");
        }
    }

    // Endpoint for Updating a User Profile
    @PutMapping("/update/{username}")
    public ResponseEntity<String> updateUserProfile(@PathVariable String username, @RequestBody SignUpUser updatedUser) {
        boolean isUpdated = userService.updateUserProfile(username, updatedUser);
        if (isUpdated) {
            return ResponseEntity.ok("User profile updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or username already exists.");
        }
    }
}


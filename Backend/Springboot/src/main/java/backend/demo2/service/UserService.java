package backend.demo2.service;

import backend.demo2.model.SignUpUser;
import backend.demo2.repository.SignUpUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    @Autowired
    private SignUpUserRepository userRepository;

    public boolean registerUser(SignUpUser user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null.");
        }


        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }
        user.setLoggedIn(false); // Default to logged out when registered
        userRepository.save(user);
        return true; // Registration successful
    }

    public boolean logoutUser(String username) {
        SignUpUser user = userRepository.findByUsername(username);
        if (user != null) {
            user.setLoggedIn(false); // Set to logged out
            userRepository.save(user);
            return true;
        }
        return false; // User not found or already logged out
    }

    public SignUpUser getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean updateUserProfile(String currentUsername, SignUpUser updatedUser) {
        SignUpUser existingUser = userRepository.findByUsername(currentUsername);
        if (existingUser != null) {
            // If the username is changing, ensure the new one isn't already taken
            if (!currentUsername.equals(updatedUser.getUsername()) &&
                    userRepository.findByUsername(updatedUser.getUsername()) != null) {
                throw new IllegalArgumentException("Username already exists.");
            }
            // Update all fields, including the username
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setFirstname(updatedUser.getFirstname());
            existingUser.setLastname(updatedUser.getLastname());
            existingUser.setPassword(updatedUser.getPassword()); // Optionally hash the password
            existingUser.setUsername(updatedUser.getUsername());
            userRepository.save(existingUser);
            return true;
        }
        return false; // User not found
    }



}

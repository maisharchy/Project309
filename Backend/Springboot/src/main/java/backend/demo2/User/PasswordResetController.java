package backend.demo2.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityQuestionRepository securityQuestionRepository;

    @Operation(summary = "Reset Password", description = "Resets the user's password after verifying the email and security question answer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset was successful"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Incorrect answer to the security question")
    })
    @PutMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        String email = passwordResetRequest.getEmail();
        String password = passwordResetRequest.getPassword();
        String securityQuestionAnswer = passwordResetRequest.getSecurityQuestionAnswer();

        // Check if the password is null or empty
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password field is required.");
        }

        // Find the user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with email " + email + " not found.");
        }

        // Fetch the security question for the user
        SecurityQuestion securityQuestion = securityQuestionRepository.findByUser(user);
        if (securityQuestion == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No security question found for the user.");
        }

        // Verify the security answer
        if (!securityQuestion.getAnswer().equals(securityQuestionAnswer)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Incorrect answer to the security question.");
        }

        // Update the password
        user.setPassword(password); // Set the new password (consider hashing in a real system)
        userRepository.save(user);

        return ResponseEntity.ok("Password reset was successful! Log in to view content");
    }


    @Operation(summary = "Set Security Question", description = "Sets a security question for the user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Security question set successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "User already has a security question set")
    })
    @PostMapping("/setSecurityQuestion/{userId}")
    public ResponseEntity<String> setSecurityQuestion(@PathVariable Long userId, @RequestBody SecurityQuestionRequest securityQuestionRequest) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + userId + " not found.");
        }

        // Check if the user already has a security question
        SecurityQuestion existingSecurityQuestion = securityQuestionRepository.findByUser(user);
        if (existingSecurityQuestion != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User already has a security question set.");
        }

        // Create and save the security question
        SecurityQuestion securityQuestion = new SecurityQuestion();
        securityQuestion.setUser(user);
        securityQuestion.setQuestion(securityQuestionRequest.getQuestion());
        securityQuestion.setAnswer(securityQuestionRequest.getAnswer());
        securityQuestionRepository.save(securityQuestion);

        return ResponseEntity.ok("Security question set successfully.");
    }

    @Operation(summary = "Set or Update Security Question", description = "Sets or updates the security question for the user based on their email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Security question set or updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/setSecurityQuestion")
    public ResponseEntity<String> setOrUpdateSecurityQuestion(@RequestBody SecurityQuestionRequest securityQuestionRequest) {
        String email = securityQuestionRequest.getEmail();

        // Find the user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with email " + email + " not found.");
        }

        // Check if the user already has a security question
        SecurityQuestion securityQuestion = securityQuestionRepository.findByUser(user);
        if (securityQuestion != null) {
            securityQuestion.setQuestion(securityQuestionRequest.getQuestion());
            securityQuestion.setAnswer(securityQuestionRequest.getAnswer());
            securityQuestionRepository.save(securityQuestion);
            return ResponseEntity.ok("Security question updated successfully.");
        } else {
            securityQuestion = new SecurityQuestion();
            securityQuestion.setUser(user);
            securityQuestion.setQuestion(securityQuestionRequest.getQuestion());
            securityQuestion.setAnswer(securityQuestionRequest.getAnswer());
            securityQuestionRepository.save(securityQuestion);
            return ResponseEntity.ok("Security question set successfully.");
        }
    }

    @Operation(summary = "Get Security Question by User ID", description = "Fetches the security question set for the user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Security question fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User or security question not found")
    })
    @GetMapping("/securityQuestion/{userId}")
    public ResponseEntity<?> getSecurityQuestion(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + userId + " not found.");
        }

        SecurityQuestion securityQuestion = securityQuestionRepository.findByUser(user);
        if (securityQuestion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No security question found for the user.");
        }

        return ResponseEntity.ok(securityQuestion.getQuestion());
    }

    @Operation(summary = "Get User ID by Email", description = "Fetches the user ID using the email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User ID fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/getUserIdByEmail")
    public ResponseEntity<?> getUserIdByEmail(@RequestBody EmailRequest emailRequest) {
        String email = emailRequest.getEmail();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with email " + email + " not found.");
        } else {
            return ResponseEntity.ok(user.getId());
        }
    }
}

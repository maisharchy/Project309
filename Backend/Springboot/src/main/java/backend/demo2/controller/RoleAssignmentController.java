package backend.demo2.controller;

import backend.demo2.Server.Server;
import backend.demo2.Server.ServerMembership;
import backend.demo2.Server.ServerMembershipRepository;
import backend.demo2.Server.ServerRepository;
import backend.demo2.User.User;
import backend.demo2.User.UserRepository;
import backend.demo2.model.RoleAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/servers/roles")
public class RoleAssignmentController {

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServerMembershipRepository serverMembershipRepository;

    @PutMapping("/assign")
    public ResponseEntity<String> assignRole(@RequestBody RoleAssignment request) {
        // Find server and user from the request
        Server server = serverRepository.findByServerId(request.getServerId());
        Optional<User> userOptional = userRepository.findById(request.getUserId().longValue());


        if (server == null || userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Server or User not found");
        }

        User user = userOptional.get();

        // Check if the user is already a member of the server
        Optional<ServerMembership> membershipOptional = serverMembershipRepository.findByUser(user).stream()
                .filter(membership -> membership.getServer().equals(server))
                .findFirst();

        if (membershipOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User is not a member of the server");
        }

        // Update the role
        ServerMembership membership = membershipOptional.get();
        if (request.getRole().equalsIgnoreCase("moderator") ||
                request.getRole().equalsIgnoreCase("player") ||
                request.getRole().equalsIgnoreCase("admin")) {
            membership.setRole(request.getRole().toLowerCase());
            serverMembershipRepository.save(membership);
            return ResponseEntity.ok("Role updated successfully to " + request.getRole());
        } else {
            return ResponseEntity.badRequest().body("Invalid role specified");
        }
    }
}

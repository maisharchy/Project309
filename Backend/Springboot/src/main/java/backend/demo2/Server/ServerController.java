package backend.demo2.Server;

import backend.demo2.ProblemReport.ProblemReport;
import backend.demo2.User.User;
import backend.demo2.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import backend.demo2.Complaint.Complaint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/servers")
@Tag(name = "Server Controller", description = "Operations related to servers, including creation, joining, leaving, and complaints.")
public class ServerController {

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private ServerMembershipRepository serverMembershipRepository;

    @Autowired
    private UserRepository userRepository;

    private final ServerService serverService;

    @Autowired
    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new server", description = "Creates a new server and adds the user as an admin if the user is valid.")
    @ApiResponse(responseCode = "200", description = "Server created successfully.")
    @ApiResponse(responseCode = "400", description = "Bad request if user is not found.")
    public ResponseEntity<Server> createServer(@RequestBody ServerCreationRequest request) {
        Server server = new Server(request.getName());
        Server savedServer = serverRepository.save(server);

        Optional<User> userOptional = userRepository.findById(request.getUserId().longValue());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            ServerMembership membership = new ServerMembership(savedServer, user, "admin", 0);
            serverMembershipRepository.save(membership);
            savedServer.getMemberships().add(membership);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(savedServer);
    }

    @PutMapping("/join")
    @Operation(summary = "Join an existing server", description = "Allows a user to join an existing server if they are not already a member.")
    @ApiResponse(responseCode = "200", description = "User successfully joined the server.")
    @ApiResponse(responseCode = "400", description = "Bad request if server or user is not found or if the user is already a member.")
    public ResponseEntity<String> joinServerByBody(@RequestBody ServerJoinRequest request) {
        Server server = serverRepository.findByServerId(request.getServerId());
        Optional<User> userOptional = userRepository.findById(request.getUserId().longValue());

        if (server == null || userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Server or User not found");
        }

        User user = userOptional.get();
        boolean isAlreadyMember = serverMembershipRepository.findByUser(user).stream()
                .anyMatch(membership -> membership.getServer().equals(server));

        if (isAlreadyMember) {
            return ResponseEntity.badRequest().body("User already a member of the server");
        }

        ServerMembership membership = new ServerMembership(server, user, "player", 0);
        serverMembershipRepository.save(membership);
        return ResponseEntity.ok("User successfully joined the server as a player");
    }

    @GetMapping("/my-servers/{userId}")
    @Operation(summary = "Get all servers the user is a member of", description = "Fetches all servers where the specified user is a member.")
    @ApiResponse(responseCode = "200", description = "List of servers returned.")
    @ApiResponse(responseCode = "400", description = "Bad request if user not found.")
    public ResponseEntity<List<ServerDetailsDTO>> getUserServers(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(List.of(new ServerDetailsDTO(null, "User not found")));
        }

        User user = userOptional.get();
        List<ServerDetailsDTO> serverDetails = serverMembershipRepository.findByUser(user).stream()
                .map(membership -> new ServerDetailsDTO(
                        membership.getServer().getServerId(),
                        membership.getServer().getName()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(serverDetails);
    }

    @PostMapping("/enter/{serverId}/{userId}")
    @Operation(summary = "Enter a server", description = "Allows a user to enter a server they are a member of.")
    @ApiResponse(responseCode = "200", description = "User successfully entered the server.")
    @ApiResponse(responseCode = "400", description = "Bad request if user is not a member of the server or if server/user is not found.")
    public ResponseEntity<String> enterServer(@PathVariable Integer serverId, @PathVariable Long userId) {
        Server server = serverRepository.findByServerId(serverId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (server == null || userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Server or User not found");
        }

        User user = userOptional.get();
        Optional<ServerMembership> membershipOptional = serverMembershipRepository.findByUser(user).stream()
                .filter(membership -> membership.getServer().equals(server))
                .findFirst();

        if (membershipOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User is not a member of the server");
        }

        String userType = membershipOptional.get().getRole();
        return ResponseEntity.ok("User entered server: " + server.getName() + ", User Type: " + userType);
    }

    @GetMapping("/{serverId}/memberships")
    @Operation(summary = "Show all server memberships", description = "Fetches all memberships associated with a server.")
    @ApiResponse(responseCode = "200", description = "List of server memberships returned.")
    @ApiResponse(responseCode = "400", description = "Bad request if server is not found.")
    public ResponseEntity<List<ServerMembership>> showServerMemberships(@PathVariable Integer serverId) {
        Server server = serverRepository.findByServerId(serverId);

        if (server == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<ServerMembership> memberships = serverMembershipRepository.findByServer(server);
        return ResponseEntity.ok(memberships);
    }

    @DeleteMapping("/leave/{serverId}/{userId}")
    @Operation(summary = "Leave a server", description = "Allows a user to leave a server they are currently a member of.")
    @ApiResponse(responseCode = "200", description = "User successfully left the server.")
    @ApiResponse(responseCode = "400", description = "Bad request if user is not a member or if server/user is not found.")
    public ResponseEntity<String> leaveServer(@PathVariable Integer serverId, @PathVariable Long userId) {
        Server server = serverRepository.findByServerId(serverId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (server == null || userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Server or User not found");
        }

        User user = userOptional.get();
        List<ServerMembership> memberships = serverMembershipRepository.findByUser(user);
        ServerMembership membership = memberships.stream()
                .filter(m -> m.getServer().equals(server))
                .findFirst()
                .orElse(null);

        if (membership == null) {
            return ResponseEntity.badRequest().body("User is not a member of the server");
        }

        serverMembershipRepository.delete(membership);
        return ResponseEntity.ok("User has left the server");
    }

    @GetMapping("/{serverId}/complaints")
    @Operation(summary = "Get all complaints for a server", description = "Fetches all complaints for a specified server.")
    @ApiResponse(responseCode = "200", description = "List of complaints returned.")
    @ApiResponse(responseCode = "400", description = "Bad request if server not found.")
    public ResponseEntity<List<Complaint>> getAllComplaintsForServer(@PathVariable Integer serverId) {
        List<Complaint> complaints = serverService.getAllComplaintsForServer(serverId);
        return ResponseEntity.ok(complaints);
    }
    @GetMapping("/{serverId}/problem-reports")
    @Operation(summary = "Get all problem reports for a server", description = "Fetches all problem reports for users in a specified server.")
    @ApiResponse(responseCode = "200", description = "List of problem reports returned.")
    @ApiResponse(responseCode = "400", description = "Bad request if server not found.")
    public ResponseEntity<List<ProblemReport>> getProblemReportsForServer(@PathVariable Integer serverId) {
        try {
            List<ProblemReport> problemReports = serverService.getProblemReportsForServer(serverId);
            return ResponseEntity.ok(problemReports);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}

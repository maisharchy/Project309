package backend.demo2.Server;

import backend.demo2.ProblemReport.ProblemReport;
import backend.demo2.ProblemReport.ProblemReportRepository;
import backend.demo2.Server.Server;
import backend.demo2.Server.ServerMembership;
import backend.demo2.Complaint.Complaint;
import backend.demo2.Complaint.ComplaintRepository;
import backend.demo2.User.User;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServerService {

    private final ServerRepository serverRepository;
    private final ComplaintRepository complaintRepository;
    private final ProblemReportRepository problemReportRepository;

    public ServerService(ServerRepository serverRepository, ComplaintRepository complaintRepository, ProblemReportRepository problemReportRepository) {
        this.serverRepository = serverRepository;
        this.complaintRepository = complaintRepository;
        this.problemReportRepository= problemReportRepository;
    }

    public List<Complaint> getAllComplaintsForServer(Integer serverId) {
        // Fetch the server and its memberships
        Server server = serverRepository.findById(Long.valueOf(serverId))
                .orElseThrow(() -> new RuntimeException("Server not found"));
        List<Complaint> allComplaints = new ArrayList<>();

        for (ServerMembership membership : server.getMemberships()) {
            User user = membership.getUser();
            List<Complaint> userComplaints = complaintRepository.findByComplainedUserOrComplainantUser(user, user);
            allComplaints.addAll(userComplaints);
        }

        return allComplaints;
    }
    public List<ProblemReport> getProblemReportsForServer(Integer serverId) {
        // Fetch the server and validate its existence
        Server server = serverRepository.findByServerId(serverId);
        if (server == null) {
            throw new RuntimeException("Server not found");
        }

        // Get all user IDs from server memberships and convert them to Long
        List<Long> userIds = server.getMemberships().stream()
                .map(membership -> membership.getUser().getId().longValue()) // Convert Integer to Long
                .collect(Collectors.toList());

        if (userIds.isEmpty()) {
            return Collections.emptyList(); // Return an empty list if no users are found
        }

        // Fetch problem reports for all user IDs in one query
        List<ProblemReport> problemReports = problemReportRepository.findByUserIdIn(userIds);

        return problemReports;
    }


}

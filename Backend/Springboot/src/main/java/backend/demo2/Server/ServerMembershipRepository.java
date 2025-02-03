package backend.demo2.Server;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import backend.demo2.User.User;
import backend.demo2.Server.Server;
import backend.demo2.Server.ServerMembership;

public interface ServerMembershipRepository extends JpaRepository<ServerMembership, Long> {
    List<ServerMembership> findByServer(Server server);
    List<ServerMembership> findByUser(User user);
}

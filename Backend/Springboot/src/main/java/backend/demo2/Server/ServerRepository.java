package backend.demo2.Server;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ServerRepository extends JpaRepository<Server, Long> {
    Server findByServerId(Integer serverId);

    @Query("SELECT sm FROM ServerMembership sm WHERE sm.user.id = :userId")
    List<ServerMembership> findMembershipsByUserId(@Param("userId") int userId);
}

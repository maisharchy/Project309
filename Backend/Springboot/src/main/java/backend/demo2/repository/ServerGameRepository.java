package backend.demo2.repository;

import backend.demo2.model.ServerGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerGameRepository extends JpaRepository<ServerGame, Long> {

    // Fetch all games for a specific server
    List<ServerGame> findByServerId(Long serverId);

    // Find a specific game assigned to a specific server
    Optional<ServerGame> findByServerIdAndGameId(Long serverId, Long gameId);

    // Delete a game from a specific server
    void deleteByServerIdAndGameId(Long serverId, Long gameId);
}

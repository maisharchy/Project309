package backend.demo2.GuessTheNumber;

import backend.demo2.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerScoreRepository extends JpaRepository<PlayerScore, Long> {
    List<PlayerScore> findByGameSession(GameSession gameSession);
    //PlayerScore findByGameSessionAndUserId(GameSession gameSession, String userId);
    PlayerScore findByGameSessionAndUser(GameSession gameSession, User user);
    void deleteByUser(User user);
}

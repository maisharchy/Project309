package backend.demo2.Trivia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface TriviaGameplayRepository extends JpaRepository<TriviaGameplay, Long> {
    TriviaGameplay findByUserId(Long userId); // Ensure this matches your user ID type
    TriviaGameplay findTopByOrderByScoreDesc();
    TriviaGameplay findByUserIdAndSessionId(Long userId, Long sessionId);
    @Query("SELECT t FROM TriviaGameplay t WHERE t.sessionId = :sessionId ORDER BY t.score DESC LIMIT 1")
    TriviaGameplay findHighestScorerBySessionId(Long sessionId);
    // Optional: Delete gameplay records for a specific user
    void deleteByUserId(Long userId);
    @Query("SELECT tg FROM TriviaGameplay tg WHERE tg.sessionId = :sessionId AND tg.server.id = :serverId")
    List<TriviaGameplay> findAllBySessionAndServer(Long sessionId, Long serverId);

    @Query("SELECT SUM(tg.score) FROM TriviaGameplay tg WHERE tg.sessionId = :sessionId AND tg.server.id = :serverId")
    Integer findTotalScoreForSessionAndServer(Long sessionId, Long serverId);
    @Query("SELECT tg FROM TriviaGameplay tg WHERE tg.sessionId = :sessionId AND tg.server.id = :serverId ORDER BY tg.score DESC")
    TriviaGameplay findHighestScorerBySessionAndServer(Long sessionId, Long serverId);
    @Query("SELECT tg FROM TriviaGameplay tg WHERE tg.sessionId = :sessionId AND tg.server.id = :serverId")
    List<TriviaGameplay> findAllScoresBySessionAndServer(Long sessionId, Long serverId);

    List<TriviaGameplay> findAllBySessionId(Long sessionId);
}
package backend.demo2.service;

import backend.demo2.model.TriviaStats;
import backend.demo2.repository.TriviaStatsRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class TriviaStatsService {

    private final TriviaStatsRepository statsRepository;
    private final EntityManager entityManager;

    public TriviaStatsService(TriviaStatsRepository statsRepository, EntityManager entityManager) {
        this.statsRepository = statsRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void populateStatsFromGameplay() {
        // Truncate the trivia_stats table
        entityManager.createNativeQuery("TRUNCATE TABLE trivia_stats").executeUpdate();

        // Populate the trivia_stats table with the required data
        String query = """
                INSERT INTO trivia_stats (username, games_played, high_score, average_score)
                SELECT u.username,
                       COUNT(g.id) AS games_played,
                       MAX(g.score) AS high_score,
                       AVG(g.score) AS average_score
                FROM users u
                JOIN trivia_gameplay g ON u.id = g.user_id
                GROUP BY u.id
                ORDER BY high_score DESC
                """;

        entityManager.createNativeQuery(query).executeUpdate();
    }

    public List<TriviaStats> getAllStats() {
        // Fetch all stats from trivia_stats table
        return statsRepository.findAll();
    }
}

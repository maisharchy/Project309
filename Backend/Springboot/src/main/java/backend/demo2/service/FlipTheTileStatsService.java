package backend.demo2.service;

import backend.demo2.model.FlipTheTileStats;
import backend.demo2.repository.FlipTheTileStatsRepository;
import backend.demo2.repository.FlipTheTileGameplayRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class FlipTheTileStatsService {

    private final FlipTheTileStatsRepository statsRepository;
    private final FlipTheTileGameplayRepository gameplayRepository;
    private final EntityManager entityManager;

    public FlipTheTileStatsService(FlipTheTileStatsRepository statsRepository,
                                   FlipTheTileGameplayRepository gameplayRepository,
                                   EntityManager entityManager) {
        this.statsRepository = statsRepository;
        this.gameplayRepository = gameplayRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void populateStatsFromGameplay() {
        // Truncate the flipthetile_stats table before updating
        entityManager.createNativeQuery("TRUNCATE TABLE flipthetile_stats").executeUpdate();

        // Populate the flipthetile_stats table with the required data
        String query = """
                INSERT INTO flipthetile_stats (username, games_played, high_score, average_score)
                SELECT u.username,
                       COUNT(g.id) AS games_played,
                       MAX(g.score) AS high_score,
                       AVG(g.score) AS average_score
                FROM users u
                JOIN flipthetile_gameplay g ON u.id = g.user_id
                GROUP BY u.id
                ORDER BY high_score DESC
                """;

        entityManager.createNativeQuery(query).executeUpdate();
    }

    public List<FlipTheTileStats> getAllStats() {
        // Fetch all stats from flipthetile_stats table
        return statsRepository.findAll();
    }
}

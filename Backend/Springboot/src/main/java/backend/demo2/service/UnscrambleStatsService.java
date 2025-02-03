package backend.demo2.service;

import backend.demo2.model.UnscrambleStats;
import backend.demo2.repository.UnscrambleStatsRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class UnscrambleStatsService {

    private final UnscrambleStatsRepository statsRepository;
    private final EntityManager entityManager;

    public UnscrambleStatsService(UnscrambleStatsRepository statsRepository, EntityManager entityManager) {
        this.statsRepository = statsRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void populateStatsFromGameplay() {
        // Truncate the unscramble_stats table
        entityManager.createNativeQuery("TRUNCATE TABLE unscramble_stats").executeUpdate();

        // Populate the unscramble_stats table with the required data
        String query = """
                INSERT INTO unscramble_stats (username, games_played, high_score, average_score)
                SELECT u.username,
                       COUNT(g.id) AS games_played,
                       MAX(g.score) AS high_score,
                       AVG(g.score) AS average_score
                FROM users u
                JOIN unscramble_gameplay g ON u.id = g.user_id
                GROUP BY u.id
                ORDER BY high_score DESC
                """;

        entityManager.createNativeQuery(query).executeUpdate();
    }

    public List<UnscrambleStats> getAllStats() {
        // Fetch all stats from unscramble_stats table
        return statsRepository.findAll();
    }
}




package backend.demo2.service;

import backend.demo2.model.GuessTheNumberStats;
import backend.demo2.repository.GuessTheNumberStatsRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class GuessTheNumberStatsService {

    private final GuessTheNumberStatsRepository statsRepository;
    private final EntityManager entityManager;

    public GuessTheNumberStatsService(GuessTheNumberStatsRepository statsRepository, EntityManager entityManager) {
        this.statsRepository = statsRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void populateStatsFromGameplay() {
        // Truncate the guess_the_number_stats table
        entityManager.createNativeQuery("TRUNCATE TABLE guess_the_number_stats").executeUpdate();

        // Populate the guess_the_number_stats table with the required data
        String query = """
                INSERT INTO guess_the_number_stats (username, games_played, high_score, average_score)
                SELECT u.username,
                       COUNT(ps.id) AS games_played,
                       MAX(ps.score) AS high_score,
                       AVG(ps.score) AS average_score
                FROM users u
                JOIN player_score ps ON u.id = ps.user_id
                GROUP BY u.id
                ORDER BY high_score DESC
                """;

        entityManager.createNativeQuery(query).executeUpdate();
    }

    public List<GuessTheNumberStats> getAllStats() {
        // Fetch all stats from guess_the_number_stats table
        return statsRepository.findAll();
    }
}

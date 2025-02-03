package backend.demo2.repository;

import backend.demo2.model.GuessTheNumberStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuessTheNumberStatsRepository extends JpaRepository<GuessTheNumberStats, Long> {
}

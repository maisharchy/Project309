package backend.demo2.repository;

import backend.demo2.model.FlipTheTileStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlipTheTileStatsRepository extends JpaRepository<FlipTheTileStats, Long> {
    // This allows you to find stats by username if needed
}

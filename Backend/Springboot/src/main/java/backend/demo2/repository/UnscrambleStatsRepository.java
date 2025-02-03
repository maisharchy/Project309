package backend.demo2.repository;

import backend.demo2.model.UnscrambleStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnscrambleStatsRepository extends JpaRepository<UnscrambleStats, Integer> {
    // This will allow you to find stats by userId, which is now Integer
}

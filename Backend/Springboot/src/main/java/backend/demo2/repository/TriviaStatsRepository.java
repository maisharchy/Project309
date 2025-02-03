package backend.demo2.repository;


import backend.demo2.model.TriviaStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TriviaStatsRepository extends JpaRepository<TriviaStats, Long> {
}

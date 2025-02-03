package backend.demo2.Trivia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TriviaGameResultRepository extends JpaRepository<TriviaGameResult, Long> {
    // Define query methods here
}

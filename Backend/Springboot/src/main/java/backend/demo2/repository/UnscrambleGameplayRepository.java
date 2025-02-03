package backend.demo2.repository;



import backend.demo2.model.UnscrambleGameplay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnscrambleGameplayRepository extends JpaRepository<UnscrambleGameplay, Long> {
    UnscrambleGameplay findByUserIdAndSessionId(Integer userId, Long sessionId);
    List<UnscrambleGameplay> findBySessionId(Long sessionId);
}

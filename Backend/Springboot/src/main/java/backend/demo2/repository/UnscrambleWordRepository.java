package backend.demo2.repository;



import backend.demo2.model.UnscrambleWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnscrambleWordRepository extends JpaRepository<UnscrambleWord, Long> {}

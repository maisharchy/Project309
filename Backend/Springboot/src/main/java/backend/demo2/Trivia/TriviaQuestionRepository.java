package backend.demo2.Trivia;

import backend.demo2.Server.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TriviaQuestionRepository extends JpaRepository<TriviaQuestion, Integer> {
    List<TriviaQuestion> findByServer_ServerId(int serverId);
}


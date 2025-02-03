package backend.demo2.Trivia;

import backend.demo2.Server.Server;
import backend.demo2.Server.ServerRepository;
import backend.demo2.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/api/questions")
public class TriviaQuestionController {

    @Autowired
    private TriviaQuestionRepository triviaQuestionRepository;
    @Autowired
    private ServerRepository serverRepository;


    @PostMapping("/question/{serverId}")
    public ResponseEntity<?> createTriviaQuestion(@PathVariable int serverId, @RequestBody TriviaQuestionDto triviaQuestionDto) {
        // Fetch the Server using serverId
        Server server = serverRepository.findByServerId(serverId); // Use your custom method here
        if (server == null) {
            throw new RuntimeException("Server with ID " + serverId + " not found");
        }

        // Create TriviaQuestion and set the server
        TriviaQuestion triviaQuestion = new TriviaQuestion();
        triviaQuestion.setQuestion(triviaQuestionDto.getQuestion());
        triviaQuestion.setCorrectAnswer(triviaQuestionDto.getCorrectAnswer());
        triviaQuestion.setType(triviaQuestionDto.getType());
        triviaQuestion.setServer(server); // Set the server object fetched from the database

        // Save trivia question
        triviaQuestionRepository.save(triviaQuestion);
        return ResponseEntity.ok("Trivia question created successfully");
    }
}

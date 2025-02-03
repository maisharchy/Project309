package backend.demo2.GuessTheNumber;

import backend.demo2.User.User;
import backend.demo2.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GuessNumberService {

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PlayerScoreRepository playerScoreRepository;

    @Autowired
    private UserRepository userRepository;

    private Set<WebSocketSession> allPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private Map<WebSocketSession, String> users = new ConcurrentHashMap<>();
    private Random random = new Random();

    public GameSession startNewGame(int totalRounds) {
        GameSession session = new GameSession();
        session.setTotalRounds(totalRounds);
        session.setCurrentRound(1);
        session.setCurrentNumber(generateRandomNumber());
        session.setActive(true);
        return gameSessionRepository.save(session);
    }

    public void processGuess(String userId, int guess, WebSocketSession session) throws Exception {
        GameSession activeSession = getActiveGameSession();

        if (activeSession == null) {
            session.sendMessage(new TextMessage("No active game session. Please wait for the next game."));
            return;
        }

        int correctNumber = activeSession.getCurrentNumber();

        if (guess == correctNumber) {
            handleCorrectGuess(userId, activeSession, session);
            broadcastMessageToAllPlayers("Player " + userId + " guessed the number correctly!");
        } else {
            String hint = guess < correctNumber ? "HIGHER" : "LOWER";
            session.sendMessage(new TextMessage(hint));
        }
    }

    private void handleCorrectGuess(String userId, GameSession session, WebSocketSession webSocketSession) throws Exception {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        PlayerScore playerScore = playerScoreRepository.findByGameSessionAndUser(session, user);
        if (playerScore == null) {
            playerScore = new PlayerScore();
            playerScore.setUser(user);
            playerScore.setGameSession(session);
            playerScore.setScore(0);
        }
        playerScore.setScore(playerScore.getScore() + 10);
        playerScoreRepository.save(playerScore);

        webSocketSession.sendMessage(new TextMessage("Congratulations! You guessed the number."));

        if (session.getCurrentRound() < session.getTotalRounds()) {
            session.setCurrentRound(session.getCurrentRound() + 1);
            session.setCurrentNumber(generateRandomNumber());
            gameSessionRepository.save(session);

            broadcastMessageToAllPlayers("New round started! Guess the new number.");
        } else {
            session.setActive(false);
            gameSessionRepository.save(session);

            broadcastMessageToAllPlayers("Game over! Displaying final scores.");
            displayScores(session);
        }
    }

    private int generateRandomNumber() {
        return random.nextInt(100) + 1; // Random number between 1 and 100
    }

    private void broadcastMessageToAllPlayers(String message) throws IOException {
        for (WebSocketSession player : allPlayers) {
            if (player.isOpen()) {
                player.sendMessage(new TextMessage(message));
            }
        }
    }

    private void displayScores(GameSession session) throws IOException {
        List<PlayerScore> scores = playerScoreRepository.findByGameSession(session);
        StringBuilder scoreMessage = new StringBuilder("Game Over! Displaying Final Scores:\n");

        PlayerScore highestScorer = scores.stream()
                .max(Comparator.comparingInt(PlayerScore::getScore))
                .orElse(null);

        for (PlayerScore score : scores) {
            scoreMessage.append("Player ")
                    .append(score.getUser().getUsername())
                    .append(": ")
                    .append(score.getScore())
                    .append("\n");
        }

        if (highestScorer != null) {
            scoreMessage.append("\nHighest Scorer: ")
                    .append(highestScorer.getUser().getUsername())
                    .append(" with ")
                    .append(highestScorer.getScore())
                    .append(" points!");
        }
        scoreMessage.append("Thank you for playing Guess The Number! The game is over.");

        broadcastMessageToAllPlayers(scoreMessage.toString());
        disconnectAllPlayers();
    }

    private void disconnectAllPlayers() throws IOException {
        for (WebSocketSession player : new ArrayList<>(allPlayers)) { // Avoid ConcurrentModificationException
            if (player.isOpen()) {
                //player.sendMessage(new TextMessage("Thank you for playing! The game is over."));
                player.close(); // Close the WebSocket connection
            }
        }
        users.clear();
        allPlayers.clear(); // Clear the player set
    }

    public GameSession getActiveGameSession() {
        return gameSessionRepository.findByIsActiveTrue();
    }

    public void addPlayer(WebSocketSession session) {
        allPlayers.add(session);
    }

    public void removePlayer(WebSocketSession session) {
        allPlayers.remove(session);
        users.remove(session);
    }
}

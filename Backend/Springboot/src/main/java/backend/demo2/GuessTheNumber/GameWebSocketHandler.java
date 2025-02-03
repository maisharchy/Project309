package backend.demo2.GuessTheNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
//new
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private GuessNumberService guessNumberService;

    private Map<WebSocketSession, String> users = new ConcurrentHashMap<>();
    private Set<WebSocketSession> allPlayers = new HashSet<>();  // Track all players in the session

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String uriPath = session.getUri().getPath();
        String[] pathParts = uriPath.split("/");

        if (pathParts.length > 2) {
            String userId = pathParts[2];
            users.put(session, userId);
            allPlayers.add(session);

            session.sendMessage(new TextMessage("Welcome, Player " + userId + "!"));

            if (allPlayers.size() == 1) {
                startNewGame(); // Automatically start a game when the first player joins
            } else {
                GameSession activeSession = guessNumberService.getActiveGameSession();
                if (activeSession != null) {
                    session.sendMessage(new TextMessage("Current Round: " + activeSession.getCurrentRound()));
                    session.sendMessage(new TextMessage("Please guess a number between 1 and 100."));
                }
            }
        } else {
            session.sendMessage(new TextMessage("Invalid WebSocket URL. User ID is missing."));
            session.close();
        }
        guessNumberService.addPlayer(session);
    }



    private void startNewGame() throws IOException {
        if (allPlayers.isEmpty()) {
            // No players available to start a new game
            return;
        }

        GameSession gameSession = guessNumberService.startNewGame(3);  // Start a game with 3 rounds

        // Broadcast start message to all players
        broadcastMessageToAllPlayers("Game has started! Please guess a number between 1 and 100.");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String userId = users.get(session);

        try {
            int guess = Integer.parseInt(payload.trim());
            guessNumberService.processGuess(userId, guess, session);
        } catch (NumberFormatException e) {
            session.sendMessage(new TextMessage("Invalid guess! Please send a number between 1 and 100."));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        users.remove(session);
        allPlayers.remove(session);  // Remove player from the allPlayers set
        guessNumberService.removePlayer(session);
    }

    private void broadcastMessageToAllPlayers(String message) throws IOException {
        for (WebSocketSession player : allPlayers) {
            if (player.isOpen()) {
                player.sendMessage(new TextMessage(message));
            }
        }
    }
    private void endGame() throws IOException {
        for (WebSocketSession player : allPlayers) {
            if (player.isOpen()) {
                player.sendMessage(new TextMessage("Game has ended. Thank you for playing!"));
                player.close();
            }
        }
        users.clear();
        allPlayers.clear();
    }

}

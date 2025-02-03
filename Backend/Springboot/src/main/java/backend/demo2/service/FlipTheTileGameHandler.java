package backend.demo2.service;

import backend.demo2.User.User;
import backend.demo2.User.UserRepository;
import backend.demo2.model.FlipTheTileGameplay;
import backend.demo2.model.Tile;
import backend.demo2.repository.FlipTheTileGameplayRepository;
import backend.demo2.Server.ServerRepository;
import backend.demo2.Server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FlipTheTileGameHandler extends TextWebSocketHandler {
    private static final Map<WebSocketSession, User> users = new ConcurrentHashMap<>();
    private final List<Tile> tiles = new ArrayList<>();
    private final Map<WebSocketSession, Tile> lastFlippedTile = new ConcurrentHashMap<>();
    private final Map<User, Double> userScores = new HashMap<>();
    private final Map<User, Long> userFinishTimes = new HashMap<>();
    private boolean gameStarted = false;
    private int matchedPairs = 0;
    private long gameStartTime;
    private static final int TILE_PAIR_COUNT = 10;
    private final List<String> colors = Arrays.asList("red", "orange", "yellow", "green", "blue", "purple", "pink", "brown", "black", "teal");
    private Long sessionId = System.currentTimeMillis();
    private int serverId;

    @Autowired
    private FlipTheTileGameplayRepository flipTheTileGameplayRepository;
    @Autowired
    private ServerRepository serverRepository;
    @Autowired
    private UserRepository userRepository;  // Inject UserRepository

    @Autowired
    public FlipTheTileGameHandler(FlipTheTileGameplayRepository flipTheTileGameplayRepository,
                                  ServerRepository serverRepository,
                                  UserRepository userRepository) {
        this.flipTheTileGameplayRepository = flipTheTileGameplayRepository;
        this.serverRepository = serverRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, String> params = getParametersFromSession(session);
        String userId = params.get("userId");
        String serverId = params.get("serverId");
        this.serverId = Integer.parseInt(serverId);

        if (userId != null && serverId != null) {
            User user = fetchUserById(Integer.parseInt(userId)); // Fetch the User object
            if (user != null) {
                users.put(session, user); // Store the full User object
            }

            if (!gameStarted) {
                initializeTiles();
                gameStarted = true;
                gameStartTime = System.currentTimeMillis();
                notifyUsers(null, "Game has started! Flip the tiles!");
            }
        }
    }

    private User fetchUserById(int userId) {
        return userRepository.findById((userId));  // Fetch the User object from the database
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        User user = users.get(session);  // Fetch the User object
        if (payload.startsWith("flip:")) {
            int tileId = Integer.parseInt(payload.substring(5));
            flipTile(tileId, session, user);  // Pass the User object for score update
        }
    }

    private Map<String, String> getParametersFromSession(WebSocketSession session) {
        Map<String, String> params = new HashMap<>();
        String uri = session.getUri().toString();
        String[] parts = uri.split("/");

        if (parts.length >= 3) {
            params.put("userId", parts[parts.length - 2]);
            params.put("serverId", parts[parts.length - 1]);
        }
        return params;
    }

    private void initializeTiles() {
        List<String> selectedColors = new ArrayList<>(colors);
        Collections.shuffle(selectedColors);

        for (int i = 0; i < TILE_PAIR_COUNT; i++) {
            String color = selectedColors.get(i % selectedColors.size());
            tiles.add(new Tile(i, color));
            tiles.add(new Tile(i + TILE_PAIR_COUNT, color));
        }
        shuffleTiles();
    }

    private void shuffleTiles() {
        Collections.shuffle(tiles);
    }

    private void flipTile(int tileId, WebSocketSession session, User user) {
        if (!gameStarted) return;

        Tile flippedTile = getTileById(tileId);
        if (flippedTile != null && flippedTile.isActive() && !flippedTile.isFlipped()) {
            flippedTile.flip();
            notifyUsers(session, "User " + user.getId() + " flipped tile " + tileId);

            Tile previousFlipped = lastFlippedTile.get(session);
            if (previousFlipped == null) {
                lastFlippedTile.put(session, flippedTile);
            } else {
                if (previousFlipped.getColor().equals(flippedTile.getColor())) {
                    matchedPairs++;
                    flippedTile.setActive(false);
                    previousFlipped.setActive(false);
                    notifyUsers(session, "Matched pair: " + flippedTile.getId() + " and " + previousFlipped.getId());

                    updateScore(user, 10.0); // Award points for a match

                    if (matchedPairs == TILE_PAIR_COUNT) {
                        endGame(session, user);
                    }
                } else {
                    handleNonMatchingTiles(previousFlipped, flippedTile, session, user);
                }
                lastFlippedTile.remove(session);
            }
        }
    }

    private void handleNonMatchingTiles(Tile tile1, Tile tile2, WebSocketSession session, User user) {
        notifyUsers(session, "Tiles did not match: " + tile1.getColor() + " and " + tile2.getColor());

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                tile1.flip();
                tile2.flip();
                notifyUsers(session, "Tiles have been flipped back.");
            }
        }, 1000);

        updateScore(user, -1.0); // Deduct points for incorrect match
    }

    private void endGame(WebSocketSession winnerSession, User winner) {
        long timeTaken = System.currentTimeMillis() - gameStartTime;

        // Record the finish time of the user
        userFinishTimes.put(winner, timeTaken);

        //notifyUsers(winnerSession, "Game over! ");
        StringBuilder finalScores = new StringBuilder("Game over! Final Scores:\n");
        userScores.forEach((user, score) ->
                finalScores.append("User ").append(user.getId())
                        .append(": ").append(score).append("\n")
        );

        notifyUsers(winnerSession, finalScores.toString());


        // Show leaderboard after game ends
        //showLeaderboard();
    }

    private void showLeaderboard() {
        // Sort users by their scores in descending order
        List<Map.Entry<User, Double>> sortedUsers = new ArrayList<>(userScores.entrySet());
        sortedUsers.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

        notifyUsers(null, "Leaderboard:");
        for (Map.Entry<User, Double> entry : sortedUsers) {
            User user = entry.getKey();
            double score = entry.getValue();
            notifyUsers(null, user.getId() + " - " + score + " points");
        }
    }

    private void notifyUsers(WebSocketSession session, String message) {
        users.forEach((webSocketSession, user) -> {
            try {
                webSocketSession.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateScore(User user, Double scoreDelta) {
        userScores.put(user, userScores.getOrDefault(user, 0.0) + scoreDelta);
        // Optionally broadcast the updated score to users
        String scoreMessage = "User " + user.getId() + "'s score: " + userScores.get(user);
        notifyUsers(null, scoreMessage);

        // Fetch or create a gameplay record for the user and session
        FlipTheTileGameplay gameplay = flipTheTileGameplayRepository
                .findByUserIdAndSessionId(user.getId().longValue(), sessionId);

        if (gameplay == null) {
            // If no gameplay record exists, create a new one
            gameplay = new FlipTheTileGameplay();
            gameplay.setUser(user);
            gameplay.setSessionId(sessionId);
            gameplay.setScore(scoreDelta.intValue()); // Set the initial score
            gameplay.setServer(getServer()); // Set the server if applicable
        } else {
            // If gameplay record exists, update the score
            gameplay.setScore(gameplay.getScore() + scoreDelta.intValue());
        }

        // Save the gameplay record
        flipTheTileGameplayRepository.save(gameplay);


    }

    private Server getServer() {
        return serverRepository.findById((long) serverId)
                .orElseThrow(() -> new IllegalArgumentException("Server not found"));
    }

    private Tile getTileById(int id) {
        return tiles.stream().filter(tile -> tile.getId() == id).findFirst().orElse(null);
    }

    public void restartGame() {
        this.tiles.clear();
        this.users.clear();
        this.matchedPairs = 0;
        this.gameStarted = false;
        this.userFinishTimes.clear();
        this.userScores.clear();
        initializeTiles();
        notifyUsers(null, "The game has been restarted. Users can join again!");
    }
}



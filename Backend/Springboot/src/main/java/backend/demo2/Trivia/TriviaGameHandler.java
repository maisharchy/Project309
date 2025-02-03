package backend.demo2.Trivia;

import backend.demo2.Server.Server;
import backend.demo2.Server.ServerRepository;
import backend.demo2.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class TriviaGameHandler extends TextWebSocketHandler {
    private static final Map<WebSocketSession, String> users = new ConcurrentHashMap<>();
    private static final Map<WebSocketSession, String> servers = new ConcurrentHashMap<>();
    private TriviaQuestion[] questions;
    private int currentQuestionIndex = 0;
    private static final int MAX_QUESTIONS = 15; // Maximum number of questions to send
    private int questionsSent = 0; // Counter for questions sent
    private boolean isCurrentQuestionAnsweredCorrectly = false; // Tracks if current question has been correctly answered
    private boolean sentfinalmessage = false;
    private final TriviaGameplayRepository triviaGameplayRepository;
    private final TriviaQuestionRepository triviaQuestionRepository;
    private final ServerRepository serverRepository;
    private int serveridofsession;
    private boolean gameActive = false; // Track if the game session is active
    // Temporary variables for highest scorer and score
    private String highestScorer = null;
    private int highestScore = 0;
    private Long sessionId = System.currentTimeMillis(); // Unique session ID based on time
    @Autowired
    private TriviaGameResultRepository triviaGameResultRepository;
    // Map to store userId -> serverId
    private Map<Integer, Integer> userServerMapping = new HashMap<>();

    @Autowired
    public TriviaGameHandler(TriviaQuestionRepository triviaQuestionRepository, TriviaGameplayRepository triviaGameplayRepository, ServerRepository serverRepository) {
        this.triviaQuestionRepository = triviaQuestionRepository;
        this.triviaGameplayRepository = triviaGameplayRepository;
        this.serverRepository = serverRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, String> params = getParametersFromSession(session);
        String userId = params.get("userId");
        String serverId = params.get("serverId");
        //serveridofsession = Integer.parseInt(serverId);

        if (userId != null && serverId != null) {
            users.put(session, userId); // Store the user session with the actual user ID
            servers.put(session, serverId);
            // Store each user's specific serverId
            userServerMapping.put(Integer.parseInt(userId), Integer.parseInt(serverId));
            if (!gameActive) { // Check if a game session is already active
                loadQuestions(); // Load questions if not already loaded
                gameActive = true; // Set game session as active
                currentQuestionIndex = 0; // Reset question index for new game
                questionsSent = 0; // Reset questions sent counter
                sendCurrentQuestion(session); // Send the current question to the newly connected user
            } else {
                // Send the current question to the user if the game is already active
                sendCurrentQuestion(session);
            }
        } else {
            System.err.println("User ID or Server ID is null for the session: " + session.getId());
        }
    }


    private Map<String, String> getParametersFromSession(WebSocketSession session) {
        Map<String, String> params = new HashMap<>();
        String uri = (session.getUri() != null) ? session.getUri().toString() : "";

        // Log the URI for debugging purposes
        System.out.println("Session URI: " + uri);

        if (uri.contains("/trivia/")) {
            // Split URI by '/' and skip empty strings
            String[] pathParts = uri.split("/+");

            // Log pathParts array for debugging
            System.out.println("Path parts: " + Arrays.toString(pathParts));

            // Check if pathParts has the expected number of elements
            if (pathParts.length >= 5) {  // Expect at least 5 segments after splitting
                try {
                    Integer userId = Integer.parseInt(pathParts[pathParts.length - 2]);
                    Integer serverId = Integer.parseInt(pathParts[pathParts.length - 1]);

                    params.put("userId", String.valueOf(userId));
                    params.put("serverId", String.valueOf(serverId));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid user ID or server ID format in URI: " + uri);
                }
            } else {
                System.err.println("Unexpected URI format for WebSocket session: " + uri);
            }
        } else {
            System.err.println("URI does not contain /trivia/: " + uri);
        }

        // Log null check for IDs
        if (!params.containsKey("userId") || !params.containsKey("serverId")) {
            System.err.println("User ID or Server ID is null for the session: " + session.getId());
        }

        return params;
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String response = processAnswer(message.getPayload(), session);

        if (response != null) { // If someone answered correctly
            if (currentQuestionIndex >= MAX_QUESTIONS) {
                displayFinalMessageAndScores(); // Display final message and scores
                if (sentfinalmessage) {
                    endGame();
                }// End the game after displaying the message
            } else {
                // Notify all users of the correct answer
                for (WebSocketSession user : users.keySet()) {
                    user.sendMessage(new TextMessage(response));
                }
                loadNextQuestion(); // Load the next question for everyone
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        users.remove(session); // Remove user session
        servers.remove(session);
    }

    private String processAnswer(String message, WebSocketSession session) throws IOException {
        String[] parts = message.split(":");

        if (parts.length < 2) {
            return "Invalid message format. Please send in the format: userId:answer";
        }

        String userId = parts[0];
        String answer = parts[1];

        // Validate user ID format
        try {
            Long.parseLong(userId);
        } catch (NumberFormatException e) {
            System.err.println("Invalid user ID format: " + userId);
            return "Invalid user ID format. Please send a numeric user ID.";
        }
        if (questions == null || currentQuestionIndex >= questions.length) {
            return "No questions available. Please wait for the game to start.";
        }

        TriviaQuestion currentQuestion = questions[currentQuestionIndex];
        StringBuilder response = new StringBuilder();

        // Check if the answer is correct and if it hasn't been answered correctly yet
        if (answer.equalsIgnoreCase(currentQuestion.getCorrectAnswer()) && !isCurrentQuestionAnsweredCorrectly) {
            isCurrentQuestionAnsweredCorrectly = true; // Mark question as correctly answered
            response.append(userId).append(" answered correctly!");

            updateScores(userId, 10); // Add 10 points to the correct user's score
            response.append(" Correct answer: ").append(currentQuestion.getCorrectAnswer());

            // Proceed to the next question only after a correct answer
            currentQuestionIndex++;
        } else if (!isCurrentQuestionAnsweredCorrectly) {
            response.append(userId).append(" answered incorrectly!");
            updateScores(userId, -5); // Deduct 5 points for incorrect answer
            session.sendMessage(new TextMessage(response.toString())); // Send the feedback message for incorrect answer
            return null; // Return immediately to avoid notifying all users
        }

        // If already answered correctly, simply show the correct answer
        if (isCurrentQuestionAnsweredCorrectly) {
            response.append(" Correct answer: ").append(currentQuestion.getCorrectAnswer());
        }

        return isCurrentQuestionAnsweredCorrectly ? response.toString() : null;
    }

    public void loadQuestions() {
        if (questions == null || questions.length == 0) { // Load questions only if not already loaded
            questions = triviaQuestionRepository.findAll().toArray(new TriviaQuestion[0]);
            Collections.shuffle(Arrays.asList(questions)); // Shuffle questions
        }
    }

    private void sendCurrentQuestion(WebSocketSession session) throws IOException {
        if (questions != null && currentQuestionIndex < questions.length) {
            TriviaQuestion currentQuestion = questions[currentQuestionIndex];
            String questionMessage = "Current question: " + currentQuestion.getQuestion() + " (Type: " + currentQuestion.getType() + ")";
            session.sendMessage(new TextMessage(questionMessage));
        }
    }

    public void loadNextQuestion() throws IOException {
        // Check if we have reached the max number of questions
        if (questionsSent < MAX_QUESTIONS && currentQuestionIndex < questions.length - 1) {
            isCurrentQuestionAnsweredCorrectly = false;
            TriviaQuestion nextQuestion = questions[currentQuestionIndex];
            String questionMessage = "Current question: " + nextQuestion.getQuestion() + " (Type: " + nextQuestion.getType() + ")";

            for (WebSocketSession user : users.keySet()) {
                user.sendMessage(new TextMessage(questionMessage));
            }

            questionsSent++;
        } else {
            endGame(); // End the game after reaching max questions
        }
    }

    private void updateScores(String userId, int points) {
        int userIntId = Integer.parseInt(userId);
        Integer serverId = userServerMapping.get(userIntId);
        TriviaGameplay gameplay = triviaGameplayRepository.findByUserIdAndSessionId(Long.valueOf(userId), sessionId);


        if (gameplay == null) {
            gameplay = new TriviaGameplay();
            gameplay.setUser(new User(Integer.valueOf(userId))); // Create user instance with ID
            gameplay.setScore(0);
            // Fetch server instance and set it
            Server server = serverRepository.findById(Long.valueOf(serverId))
                    .orElseThrow(() -> new IllegalArgumentException("Server not found"));
            gameplay.setServer(server);// Set the server instance
            gameplay.setSessionId(sessionId);
        }
        // Update score and check if it's the highest
        int newScore;
        newScore = highestScore + points;
        gameplay.setScore(gameplay.getScore() + points);
        triviaGameplayRepository.save(gameplay);
        // Update highest scorer and score if the new score is higher
        if (newScore > highestScore) {
            highestScore = newScore;
            highestScorer = userId;
        }
    }

    private Server getCurrentServer() {
        // Implement logic to retrieve the current server
        return serverRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Server not found"));
    }


    private void displayFinalMessageAndScores() throws IOException {
        sentfinalmessage = false;
        StringBuilder finalMessage = new StringBuilder("Game Over! Thanks for playing!\n");

        // Collect scores for the current session and sort by highest score
        List<Map.Entry<String, Integer>> userScores = users.entrySet().stream()
                .map(entry -> {
                    String userId = entry.getValue();
                    try {
                        Long parsedUserId = Long.valueOf(userId);
                        TriviaGameplay gameplay = triviaGameplayRepository.findByUserIdAndSessionId(parsedUserId, sessionId);
                        int score = (gameplay != null) ? gameplay.getScore() : 0;
                        return Map.entry(userId, score);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid user ID format: " + userId);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        // Append the scores and rankings for the current session
        finalMessage.append("Final Scores:\n");
        int rank = 1;
        for (Map.Entry<String, Integer> userScore : userScores) {
            finalMessage.append(rank++).append(". User ID ").append(userScore.getKey())
                    .append(": ").append(userScore.getValue()).append(" points\n");
        }
        // Display highest scorer for the current session
        TriviaGameplay sessionHighestScorer = triviaGameplayRepository.findHighestScorerBySessionId(sessionId);
        if (sessionHighestScorer != null) {
            finalMessage.append("\nHighest Scorer of This Session:\n");
            finalMessage.append("User").append(sessionHighestScorer.getUser().getUsername())
                    .append(": ").append(sessionHighestScorer.getScore()).append(" points\n");
        }
        // Fetch the highest scorer across all games for the server
        TriviaGameplay highestScorerAllTime = triviaGameplayRepository.findTopByOrderByScoreDesc();
        if (highestScorerAllTime != null) {
            finalMessage.append("\nHighest Scorer in All Games:\n");
            finalMessage.append("User ").append(highestScorerAllTime.getUser().getUsername())
                    .append(": ").append(highestScorerAllTime.getScore()).append(" points\n");
        }

        finalMessage.append("Thank you for participating!");
        // Step 2: Calculate total scores for each server
        Map<Long, Integer> serverScores = new HashMap<>();
        List<TriviaGameplay> gameplayEntries = triviaGameplayRepository.findAllBySessionId(sessionId);
        for (TriviaGameplay entry : gameplayEntries) {
            Long serverId = Long.valueOf(entry.getServer().getServerId());
            serverScores.put(serverId, serverScores.getOrDefault(serverId, 0) + entry.getScore());
        }

        // Step 3: Determine the winning server by highest total score
        Map.Entry<Long, Integer> winningServer = serverScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (winningServer != null) {
            finalMessage.append("\nWinning Server for This Session:\n");
            finalMessage.append("Server ID ").append(winningServer.getKey())
                    .append(": ").append(winningServer.getValue()).append(" points\n");

            // Retrieve the Server entity from the database using the winning server ID
            Server winningServerEntity = serverRepository.findById(winningServer.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Server not found with ID: " + winningServer.getKey()));

            // Step 4: Create and save the TriviaGameResult for the winning server
            TriviaGameResult gameResult = new TriviaGameResult();
            gameResult.setServer(winningServerEntity);  // Set the winning server entity
            gameResult.setMultiplayer(isMultiplayer());  // Multiplayer flag based on game type
            gameResult.setTotalScore(winningServer.getValue());  // Total score for the winning server
            gameResult.setSessionId(sessionId);  // Set the session ID
            gameResult.setResult("Winner");  // Mark as "Winner"

            triviaGameResultRepository.save(gameResult);
        } else {
            finalMessage.append("\nNo scores recorded for this session.\n");
        }

        // Step 5: Send the final message to all users

        // Save the game result to the database
        //triviaGameResultRepository.save(gameResult);

        // Send the final message to all users (if needed)
        for (WebSocketSession user : users.keySet()) {
            user.sendMessage(new TextMessage(finalMessage.toString()));
        }

        // End the game after processing the results
        endGame();
    }

    private boolean isMultiplayer() {
        // Check if the users are from different servers or the same server
        return users.values().stream()
                .map(userId -> getCurrentServer())
                .distinct()
                .count() > 1; // If there are different servers, return true (multiplayer)
    }


    private void saveScoresForSession() {
        // After the game ends, save each user's total score for this session as a new record
        for (WebSocketSession session : users.keySet()) {
            String userId = users.get(session);
            TriviaGameplay gameplay = triviaGameplayRepository.findByUserIdAndSessionId(Long.valueOf(userId), sessionId);

            // Here you can store the final score record or do any further processing
            triviaGameplayRepository.save(gameplay);  // Already saved in updateScores, but ensure the final state is persisted
        }
    }


    private void endGame() throws IOException {
        // Do not close the sessions here, just clear necessary variables
        questions = null; // Clear questions to allow for a new game
        currentQuestionIndex = 0; // Reset question index for a new game
        questionsSent = 0; // Reset questions sent counter
        gameActive = false; // Mark game session as inactive

        // Close all sessions and clear the map after the final message is sent
        for (WebSocketSession user : users.keySet().toArray(new WebSocketSession[0])) {
            user.close();
        }
        users.clear();
        servers.clear();
    }
}
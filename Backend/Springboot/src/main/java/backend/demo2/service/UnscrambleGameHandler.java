package backend.demo2.service;


import backend.demo2.Server.Server;
import backend.demo2.Server.ServerRepository;
import backend.demo2.User.User;
import backend.demo2.model.UnscrambleGameplay;
import backend.demo2.model.UnscrambleWord;
import backend.demo2.repository.UnscrambleGameplayRepository;
import backend.demo2.repository.UnscrambleWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UnscrambleGameHandler extends TextWebSocketHandler {
    private static final Map<WebSocketSession, String> users = new ConcurrentHashMap<>();
    private List<UnscrambleWord> wordsPool = new ArrayList<>();
    private final List<String> sessionWords = new ArrayList<>();
    private final Map<String, Integer> scores = new HashMap<>();
    private int currentWordIndex = 0;
    private boolean gameActive = false;
    private Long sessionId = System.currentTimeMillis();
    private int serverId;

    private final UnscrambleWordRepository unscrambleWordRepository;
    private final UnscrambleGameplayRepository unscrambleGameplayRepository;
    private final ServerRepository serverRepository;

    @Autowired
    public UnscrambleGameHandler(UnscrambleWordRepository unscrambleWordRepository,
                                 UnscrambleGameplayRepository unscrambleGameplayRepository,
                                 ServerRepository serverRepository) {
        this.unscrambleWordRepository = unscrambleWordRepository;
        this.unscrambleGameplayRepository = unscrambleGameplayRepository;
        this.serverRepository = serverRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, String> params = getParametersFromSession(session);
        String userId = params.get("userId");
        String serverId = params.get("serverId");
        this.serverId = Integer.parseInt(serverId);

        if (userId != null && serverId != null) {
            users.put(session, userId);

            if (!gameActive) {
                loadWordsForSession();
                gameActive = true;
                sendNextWord();
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String userId = users.get(session);
        String correctWord = sessionWords.get(currentWordIndex);

        if (correctWord.equalsIgnoreCase(payload.trim())) {
            updateScore(userId, 10);
            sendMessageToAll(userId + " answered correctly! Moving to the next word.");
            if (++currentWordIndex < sessionWords.size()) {
                sendNextWord();
            } else {
                endGame();
            }
        } else {
            session.sendMessage(new TextMessage("Incorrect! Try again."));
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

    private void loadWordsForSession() {
        wordsPool = unscrambleWordRepository.findAll();
        Collections.shuffle(wordsPool);
        sessionWords.clear();
        for (int i = 0; i < 10 && i < wordsPool.size(); i++) {
            sessionWords.add(wordsPool.get(i).getWord());
        }
    }

    private void sendNextWord() throws IOException {
        String scrambled = scrambleWord(sessionWords.get(currentWordIndex));
        sendMessageToAll("Unscramble this word: " + scrambled);
    }

    private String scrambleWord(String word) {
        List<Character> chars = new ArrayList<>();
        for (char c : word.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars);
        StringBuilder scrambled = new StringBuilder();
        for (char c : chars) {
            scrambled.append(c);
        }
        return scrambled.toString();
    }

    private void updateScore(String userId, int points) {
        scores.put(userId, scores.getOrDefault(userId, 0) + points);
        UnscrambleGameplay gameplay = unscrambleGameplayRepository
                .findByUserIdAndSessionId(Integer.valueOf(userId), sessionId);

        if (gameplay == null) {
            gameplay = new UnscrambleGameplay(new User(Integer.parseInt(userId)), 0, sessionId, getServer());
        }
        gameplay.setScore(gameplay.getScore() + points);
        unscrambleGameplayRepository.save(gameplay);
    }

    private Server getServer() {
        return serverRepository.findById((long) serverId)
                .orElseThrow(() -> new IllegalArgumentException("Server not found"));
    }

    private void sendMessageToAll(String message) throws IOException {
        for (WebSocketSession session : users.keySet()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    private void endGame() throws IOException {
        sendMessageToAll("Game Over! Scores: " + scores);
        users.clear();
        gameActive = false;
    }
}

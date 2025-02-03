package backend.demo2.config;

import backend.demo2.GuessTheNumber.GameWebSocketHandler;
import backend.demo2.Trivia.TriviaGameHandler;
import backend.demo2.service.FlipTheTileGameHandler;
import backend.demo2.service.UnscrambleGameHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private GameWebSocketHandler gameWebSocketHandler;
    private final TriviaGameHandler triviaGameHandler;
    private final UnscrambleGameHandler unscrambleGameHandler;
    private final FlipTheTileGameHandler flipTheTileGameHandler;

    @Autowired
    public WebSocketConfig(UnscrambleGameHandler unscrambleGameHandler, FlipTheTileGameHandler flipTheTileGameHandler,TriviaGameHandler triviaGameHandler, GameWebSocketHandler gameWebSocketHandler) {
        this.unscrambleGameHandler = unscrambleGameHandler;
        this.flipTheTileGameHandler = flipTheTileGameHandler;
        this.triviaGameHandler = triviaGameHandler;
        this.gameWebSocketHandler = gameWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Configuring WebSocket handler and allowed origins
        registry.addHandler(flipTheTileGameHandler, "/flipthetile/{userId}/{serverId}").setAllowedOrigins("*");
        registry.addHandler(unscrambleGameHandler, "/unscramble/{userId}/{serverId}").setAllowedOrigins("*");
        registry.addHandler(triviaGameHandler, "/trivia/{userId}/{serverId}").setAllowedOrigins("*");
        registry.addHandler(gameWebSocketHandler, "/guess-number/{userId}").setAllowedOrigins("*");

    }
}

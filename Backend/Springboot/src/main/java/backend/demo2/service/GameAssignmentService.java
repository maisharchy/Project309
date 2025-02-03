package backend.demo2.service;

import backend.demo2.model.Game;
import backend.demo2.model.ServerGame;
import backend.demo2.repository.ServerGameRepository;
import backend.demo2.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameAssignmentService {

    @Autowired
    private ServerGameRepository serverGameRepository;

    @Autowired
    private GameService gameService;

    // Retrieve games assigned to a server
    public List<ServerGame> getGamesForServer(Long serverId) {
        return serverGameRepository.findByServerId(serverId);
    }

    // Assign a game to a server
    public ServerGame assignGameToServer(Long serverId, Long gameId) {
        ServerGame serverGame = new ServerGame();
        serverGame.setServerId(serverId);
        serverGame.setGameId(gameId);
        return serverGameRepository.save(serverGame);
    }

    // Assign default games to a server
    public List<ServerGame> assignDefaultGamesToServer(Long serverId) {
        List<Game> defaultGames = gameService.getAllGames();
        List<ServerGame> serverGames = new ArrayList<>();

        for (Game game : defaultGames) {
            ServerGame serverGame = new ServerGame();
            serverGame.setServerId(serverId);
            serverGame.setGameId(game.getId());
            serverGames.add(serverGameRepository.save(serverGame));
        }

        return serverGames;
    }

    // Remove a specific game from a server
    public boolean removeGameFromServer(Long serverId, Long gameId) {
        ServerGame serverGame = serverGameRepository.findByServerIdAndGameId(serverId, gameId).orElse(null);
        if (serverGame != null) {
            serverGameRepository.delete(serverGame);
            return true;
        }
        return false; // Game not found for the given server
    }
}

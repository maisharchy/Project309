package backend.demo2.service;

import backend.demo2.model.Game;
import backend.demo2.model.ServerGame;
import backend.demo2.repository.GameRepository;
import backend.demo2.repository.ServerGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ServerGameRepository serverGameRepository;

    // Retrieve all games by serverId
    //public List<Game> getGamesByServerId(Long serverId) {
     //   return gameRepository.findByServerId(serverId); // Assuming findByServerId is defined in the repository
   // }

    // Retrieve all games
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    // Update assignment status
    public Game updateAssignmentStatus(Long gameId, String assignmentStatus) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            game.setAssignmentStatus(assignmentStatus);
            return gameRepository.save(game);
        }
        return null;
    }

    // Add a new game
    public Game addGame(Game game) {
        return gameRepository.save(game);
    }

    // Delete a game by gameId
    public void deleteGame(Long gameId) {
        gameRepository.deleteById(gameId);
    }

    // Update game title and description
    public Game updateGameDetails(Long gameId, String title, String description) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            game.setTitle(title);
            game.setDescription(description);
            return gameRepository.save(game);
        }
        return null;
    }

    // Assign default games to a server by creating new entries in the server_game table
    public List<ServerGame> assignDefaultGamesToServer(Long serverId) {
        List<Game> defaultGames = gameRepository.findAll();  // Assuming this fetches the 4 available games

        for (Game game : defaultGames) {
            ServerGame serverGame = new ServerGame();
            serverGame.setServerId(serverId);
            serverGame.setGameId(game.getId());
            serverGameRepository.save(serverGame); // Assign each game to the server
        }

        return serverGameRepository.findByServerId(serverId); // Return all games for the server
    }
}

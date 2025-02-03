package backend.demo2.controller;

import backend.demo2.model.Game;
import backend.demo2.model.ServerGame;
import backend.demo2.service.GameService;
import backend.demo2.service.GameAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameAssignmentService gameAssignmentService;

    // Get all available games
    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    // Get all games for a specific server
   // @GetMapping("/{serverId}")
   // public List<Game> getGamesByServerId(@PathVariable Long serverId) {
    //    return gameService.getGamesByServerId(serverId);
   // }
    /*

    // Assign a game to a server
    @PostMapping("/{serverId}/{gameId}/assign")
    public ResponseEntity<ServerGame> assignGame(@PathVariable Long serverId, @PathVariable Long gameId) {
        ServerGame assignedGame = gameAssignmentService.assignGameToServer(serverId, gameId);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignedGame);
    }

    // Assign default games to a server when a player joins
    @PostMapping("/{serverId}/assign-default-games")
    public ResponseEntity<List<ServerGame>> assignDefaultGamesToServer(@PathVariable Long serverId) {
        List<ServerGame> serverGames = gameAssignmentService.assignDefaultGamesToServer(serverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(serverGames);
    }
    // Update game details (title and description)
    @PutMapping("/{gameId}")
    public ResponseEntity<Game> updateGameDetails(@PathVariable Long gameId, @RequestBody Game updatedGame) {
        Game game = gameService.updateGameDetails(gameId, updatedGame.getTitle(), updatedGame.getDescription());
        if (game == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(game);
    }

    // Delete a game assignment from a specific server
    @DeleteMapping("/server/{serverId}/{gameId}")
    public ResponseEntity<Void> deleteGameFromServer(@PathVariable Long serverId, @PathVariable Long gameId) {
        gameAssignmentService.removeGameFromServer(serverId, gameId);
        return ResponseEntity.noContent().build();
    }*/

}


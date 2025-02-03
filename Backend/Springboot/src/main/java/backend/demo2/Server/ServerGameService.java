package backend.demo2.Server;

import backend.demo2.Server.ServerMembership;

import java.util.List;

public class ServerGameService {

    public void createTeams(List<ServerMembership> memberships) {
        int numPlayers = memberships.size();

        if (numPlayers % 2 == 0) {
            // Multiplayer scenario: Split equally
            List<ServerMembership> team1 = memberships.subList(0, numPlayers / 2);
            List<ServerMembership> team2 = memberships.subList(numPlayers / 2, numPlayers);
            // Logic to handle team scores and display
        } else {
            // Odd number of players: Last player plays single-player
            List<ServerMembership> team1 = memberships.subList(0, numPlayers / 2);
            List<ServerMembership> team2 = memberships.subList(numPlayers / 2, numPlayers - 1);
            ServerMembership singlePlayer = memberships.get(numPlayers - 1);
            // Logic to handle single player and team scores
        }
    }
}

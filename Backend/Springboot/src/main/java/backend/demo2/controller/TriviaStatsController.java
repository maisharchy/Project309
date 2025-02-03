package backend.demo2.controller;


import backend.demo2.model.TriviaStats;
import backend.demo2.service.TriviaStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TriviaStatsController {

    private final TriviaStatsService statsService;

    public TriviaStatsController(TriviaStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/trivia/stats")
    public List<TriviaStats> getAllStats() {
        statsService.populateStatsFromGameplay(); // Populate stats table
        return statsService.getAllStats();        // Fetch all stats
    }
}

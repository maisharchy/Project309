package backend.demo2.controller;

import backend.demo2.model.GuessTheNumberStats;
import backend.demo2.service.GuessTheNumberStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GuessTheNumberStatsController {

    private final GuessTheNumberStatsService statsService;

    public GuessTheNumberStatsController(GuessTheNumberStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/guess-the-number/stats")
    public List<GuessTheNumberStats> getAllStats() {
        statsService.populateStatsFromGameplay(); // Populate stats table
        return statsService.getAllStats();        // Fetch all stats
    }
}

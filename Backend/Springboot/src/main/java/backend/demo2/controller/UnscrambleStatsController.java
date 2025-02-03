package backend.demo2.controller;

//import backend.demo2.model.TriviaStats;
import backend.demo2.service.TriviaStatsService;
import backend.demo2.service.UnscrambleStatsService;
import backend.demo2.model.UnscrambleStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UnscrambleStatsController {

    private final UnscrambleStatsService statsService;

    public UnscrambleStatsController(UnscrambleStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/unscramble/stats")
    public List<UnscrambleStats> getAllStats() {
        statsService.populateStatsFromGameplay(); // Populate stats table
        return statsService.getAllStats();        // Fetch all stats
    }
}


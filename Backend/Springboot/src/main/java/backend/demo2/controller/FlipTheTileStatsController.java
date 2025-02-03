package backend.demo2.controller;

import backend.demo2.service.FlipTheTileStatsService;
import backend.demo2.model.FlipTheTileStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FlipTheTileStatsController {

    private final FlipTheTileStatsService statsService;

    @Autowired
    public FlipTheTileStatsController(FlipTheTileStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/flipthetile/stats")
    public List<FlipTheTileStats> getAllStats() {
        statsService.populateStatsFromGameplay(); // Populate stats table
        return statsService.getAllStats();        // Fetch all stats
    }
}

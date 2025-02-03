package backend.demo2;

import backend.demo2.controller.FlipTheTileStatsController;
import backend.demo2.controller.GuessTheNumberStatsController;
import backend.demo2.controller.TriviaStatsController;
import backend.demo2.controller.UnscrambleStatsController;
import backend.demo2.model.FlipTheTileStats;
import backend.demo2.model.GuessTheNumberStats;
import backend.demo2.model.TriviaStats;
import backend.demo2.model.UnscrambleStats;
import backend.demo2.service.FlipTheTileStatsService;
import backend.demo2.service.GuessTheNumberStatsService;
import backend.demo2.service.TriviaStatsService;
import backend.demo2.service.UnscrambleStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShinyStatsControllerTest {

    @Mock
    private FlipTheTileStatsService fstatsService;

    @InjectMocks
    private FlipTheTileStatsController fstatsController;

    @Mock
    private GuessTheNumberStatsService gstatsService;

    @InjectMocks
    private GuessTheNumberStatsController gstatsController;

    @Mock
    private TriviaStatsService tstatsService;

    @InjectMocks
    private TriviaStatsController tstatsController;

    @Mock
    private UnscrambleStatsService ustatsService;

    @InjectMocks
    private UnscrambleStatsController ustatsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllStatsFlipTheTile() {
        // Arrange
        FlipTheTileStats stat1 = new FlipTheTileStats("player1", 10L, 100, 75.5);
        FlipTheTileStats stat2 = new FlipTheTileStats("player2", 5L, 80, 65.0);
        List<FlipTheTileStats> expectedStats = Arrays.asList(stat1, stat2);

        when(fstatsService.getAllStats()).thenReturn(expectedStats);

        // Act
        List<FlipTheTileStats> actualStats = fstatsController.getAllStats();

        // Assert
        assertEquals(expectedStats, actualStats, "The fetched stats should match the expected stats.");
        verify(fstatsService).getAllStats(); // Ensure getAllStats was called
    }

    @Test
    void getAllStatsGuessTheNumber() {
        // Arrange
        // Arrange
        GuessTheNumberStats stat1 = new GuessTheNumberStats("player1", 10L, 100, 75.5);
        GuessTheNumberStats stat2 = new GuessTheNumberStats("player2", 5L, 80, 65.0);
        List<GuessTheNumberStats> expectedStats = Arrays.asList(stat1, stat2);

        when(gstatsService.getAllStats()).thenReturn(expectedStats);

        // Act
        List<GuessTheNumberStats> actualStats = gstatsController.getAllStats();

        // Assert
        assertEquals(expectedStats, actualStats, "The fetched stats should match the expected stats.");
        verify(gstatsService).getAllStats(); // Ensure getAllStats was called
    }

    @Test
    void getAllStatsTrivia() {
        // Arrange
        TriviaStats stat1 = new TriviaStats("player1", 10L, 100, 75.5);
        TriviaStats stat2 = new TriviaStats("player2", 5L, 80, 65.0);
        List<TriviaStats> expectedStats = Arrays.asList(stat1, stat2);

        when(tstatsService.getAllStats()).thenReturn(expectedStats);

        // Act
        List<TriviaStats> actualStats = tstatsController.getAllStats();

        // Assert
        assertEquals(expectedStats, actualStats, "The fetched stats should match the expected stats.");
        verify(tstatsService).getAllStats(); // Ensure getAllStats was called
    }

    @Test
    void getAllStatsUnscramble() {
        // Arrange
        UnscrambleStats stat1 = new UnscrambleStats("player1", 10L, 100, 75.5);
        UnscrambleStats stat2 = new UnscrambleStats("player2", 5L, 80, 65.0);
        List<UnscrambleStats> expectedStats = Arrays.asList(stat1, stat2);

        when(ustatsService.getAllStats()).thenReturn(expectedStats);

        // Act
        List<UnscrambleStats> actualStats = ustatsController.getAllStats();

        // Assert
        assertEquals(expectedStats, actualStats, "The fetched stats should match the expected stats.");
        verify(ustatsService).getAllStats(); // Ensure getAllStats was called
    }
}

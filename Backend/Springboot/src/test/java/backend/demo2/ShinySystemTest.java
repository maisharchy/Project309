package backend.demo2;

import backend.demo2.model.SignUpUser;
import backend.demo2.model.UnscrambleStats;
import backend.demo2.repository.SignUpUserRepository;
import backend.demo2.repository.UnscrambleStatsRepository;
import backend.demo2.service.UnscrambleStatsService;
import backend.demo2.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShinySystemTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private SignUpUserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testSuccessfulSignup() {
        // Arrange
        SignUpUser newUser = new SignUpUser();
        newUser.setUsername("uniqueUser");
        newUser.setPassword("securePassword");

        when(userRepository.findByUsername("uniqueUser")).thenReturn(null); // No existing user

        // Act
        boolean result = userService.registerUser(newUser);

        // Assert
        assertTrue(result, "User should be registered successfully.");
        verify(userRepository, times(1)).save(newUser); // Verify user is saved
    }

    @Test
    void testSignupWithDuplicateUsername() {
        // Arrange
        SignUpUser existingUser = new SignUpUser();
        existingUser.setUsername("duplicateUser");

        SignUpUser newUser = new SignUpUser();
        newUser.setUsername("duplicateUser");

        when(userRepository.findByUsername("duplicateUser")).thenReturn(existingUser); // Existing user

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(newUser);
        });

        assertEquals("Username already exists.", exception.getMessage());
        verify(userRepository, never()).save(newUser); // Ensure duplicate isn't saved
    }

    @Test
    void testSignupWithMissingUsername() {
        // Arrange
        SignUpUser newUser = new SignUpUser();
        newUser.setUsername(null); // Missing username
        newUser.setPassword("securePassword");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(newUser);
        });

        assertEquals("Username cannot be null.", exception.getMessage());
        verify(userRepository, never()).save(newUser); // Ensure invalid user isn't saved
    }

    @InjectMocks
    private UnscrambleStatsService statsService;

    @Mock
    private UnscrambleStatsRepository statsRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;



    @Test
    void getAllStats_Success() {
        // Arrange
        UnscrambleStats stat1 = new UnscrambleStats("player1", 10L, 100, 75.5);
        UnscrambleStats stat2 = new UnscrambleStats("player2", 5L, 80, 65.0);
        List<UnscrambleStats> expectedStats = Arrays.asList(stat1, stat2);

        when(statsRepository.findAll()).thenReturn(expectedStats);

        // Act
        List<UnscrambleStats> actualStats = statsService.getAllStats();

        // Assert
        assertEquals(expectedStats, actualStats, "The fetched stats should match the expected stats.");
        verify(statsRepository).findAll(); // Ensure findAll is called
    }
}

package backend.demo2;


import backend.demo2.controller.UserController;
import backend.demo2.model.SignUpUser;
import backend.demo2.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringJUnitConfig
public class ShinyUserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testSignup_Success() {
        SignUpUser user = new SignUpUser();
        user.setUsername("testuser");
        user.setPassword("newpassword");
        when(userService.registerUser(user)).thenReturn(true);

        ResponseEntity<String> response = userController.signup(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully.", response.getBody());
    }


    @Test
    void testGetUser_Success() {
        String username = "testuser";
        SignUpUser user = new SignUpUser();
        user.setUsername("testuser");
        user.setPassword("newpassword");
        when(userService.getUserByUsername(username)).thenReturn(user);

        ResponseEntity<SignUpUser> response = userController.getUser(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUser_NotFound() {
        String username = "testuser";
        when(userService.getUserByUsername(username)).thenReturn(null);

        ResponseEntity<SignUpUser> response = userController.getUser(username);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testLogout_Success() {
        String username = "testuser";
        when(userService.logoutUser(username)).thenReturn(true);

        ResponseEntity<String> response = userController.logout(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out successfully.", response.getBody());
    }

    @Test
    void testLogout_Failure() {
        String username = "testuser";
        when(userService.logoutUser(username)).thenReturn(false);

        ResponseEntity<String> response = userController.logout(username);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Logout failed. User may already be logged out.", response.getBody());
    }

    @Test
    void testUpdateUserProfile_Success() {
        String username = "testuser";
        SignUpUser updatedUser = new SignUpUser();
        updatedUser.setUsername("testuser");
        updatedUser.setPassword("newpassword");
        when(userService.updateUserProfile(username, updatedUser)).thenReturn(true);

        ResponseEntity<String> response = userController.updateUserProfile(username, updatedUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User profile updated successfully.", response.getBody());
    }

    @Test
    void testUpdateUserProfile_NotFound() {
        String username = "testuser";
        SignUpUser updatedUser = new SignUpUser();

        updatedUser.setUsername("testuser");
        updatedUser.setPassword("newpassword");
        when(userService.updateUserProfile(username, updatedUser)).thenReturn(false);

        ResponseEntity<String> response = userController.updateUserProfile(username, updatedUser);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found or username already exists.", response.getBody());
    }
}


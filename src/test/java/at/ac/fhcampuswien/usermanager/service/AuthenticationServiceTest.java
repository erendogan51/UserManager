package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.AuthToken;
import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import usermanager.v1.model.CreateUser;
import usermanager.v1.model.User;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest extends ServiceTestConfig {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthTokenService authTokenService;
    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private BCryptPasswordEncoder encoder;
    private AuthenticationService authenticationService;
    private EasyRandom easyRandom;

    @BeforeEach
    void setUp() {
        this.userService = new UserService(this.userRepository);
        this.authenticationService = new AuthenticationService(userService, authTokenService, authenticationManager, encoder);
        this.easyRandom = new EasyRandom();
    }

    @Test
    @DisplayName("add user correctly")
    void addUser_success() {
        // arrange
        CreateUser expected = easyRandom.nextObject(CreateUser.class);
        String username = "test";
        expected.setUsername(username);
        when(userService.existsByUsername(username)).thenReturn(false);

        // act
        User actual = authenticationService.addUser(expected);

        // assert
        assertEquals(expected.getUsername(), actual.getUsername());
    }

    @Test
    @DisplayName("add user conflict")
    void addUser_conflict() {
        // arrange
        CreateUser expected = easyRandom.nextObject(CreateUser.class);
        String username = "test";
        expected.setUsername(username);
        when(userService.existsByUsername(username)).thenReturn(true);
        // act & assert
        assertThrows(ResponseStatusException.class, () -> authenticationService.addUser(expected));
    }
    @Test
    @DisplayName("login user not found")
    void loginUser_notFound() {
        // arrange
        CreateUser expected = easyRandom.nextObject(CreateUser.class);
        String username = "test";
        String password = "im-secured";
        expected.setUsername(username);
        when(userRepository.findUsersByUsername(username)).thenReturn(null);

        // act & assert
        assertThrows(ResponseStatusException.class, () -> authenticationService.loginUser(username,password));
    }


    @Test
    @DisplayName("login user already logged in")
    void loginUser_alreadyLoggedIn() {
        // arrange
        UserEntity expected = easyRandom.nextObject(UserEntity.class);
        AuthToken token = easyRandom.nextObject(AuthToken.class);
        String username = "test";
        String password = "im-secured";
        expected.setUsername(username);
        expected.setLoggedIn(true);
        when(userRepository.findUsersByUsername(username)).thenReturn(expected);
        when(authTokenService.retrieveToken(expected)).thenReturn(token);

        // act
        String actual = authenticationService.loginUser(username, password);

        // assert
        assertEquals("You are already logged in. Your token was: " + token.getToken(), actual);
    }

    @Test
    @DisplayName("login user blocked")
    void loginUser_blocked() {
        // arrange
        UserEntity expected = easyRandom.nextObject(UserEntity.class);
        AuthToken token = easyRandom.nextObject(AuthToken.class);
        String username = "test";
        String password = "im-secured";
        expected.setUsername(username);
        expected.setLoggedIn(false);
        expected.setBlockedUntil(Instant.MAX);
        when(userRepository.findUsersByUsername(username)).thenReturn(expected);
        when(authTokenService.retrieveToken(expected)).thenReturn(null);

        // act & assert
        assertThrows(ResponseStatusException.class, () -> authenticationService.loginUser(username,password));
    }

    @Test
    @DisplayName("logout user unauthorized")
    void logoutUser_unauthorized() {
        // arrange
        UserEntity expected = easyRandom.nextObject(UserEntity.class);
        String username = "test";
        expected.setUsername(username);
        expected.setLoggedIn(true);
        expected.setBlockedUntil(Instant.MAX);
        when(userRepository.findUsersByUsername(username)).thenReturn(expected);
        // act & assert
        assertThrows(ResponseStatusException.class, () -> authenticationService.logoutUser(username));
    }

    @Test
    void updatePassword() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void resetLoginAttempt() {
    }
}
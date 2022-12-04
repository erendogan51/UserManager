package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import at.ac.fhcampuswien.usermanager.security.ErrorResponseException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());
    private static final String INVALID_CREDS_MSG = "Username or password is invalid.";
    private static final String NOT_LOGGED_IN = "You are not logged in.";

    private final UserService userService;
    private final AuthTokenService authTokenService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder encoder;

    public AuthenticationService(UserService userService, AuthTokenService authTokenService, AuthenticationManager authenticationManager, BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.authTokenService = authTokenService;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
    }

    public usermanager.v1.model.User addUser(usermanager.v1.model.CreateUser user) {
        var existingUser = userService.getUserEntityByName(user.getUsername());
        if (existingUser != null) {
            throw new ErrorResponseException(HttpStatus.CONFLICT, "The user name is already taken");
        }

        var userEntity = toUserEntity(user);
        userEntity.setLastActivity(Instant.now());
        userService.saveUser(userEntity);

        return userService.toUser(userEntity);
    }

    public String loginUser(String username, String password) {
        var user = userService.getUserEntityByName(username);
        if (user == null) {
            throw new ErrorResponseException(HttpStatus.NOT_FOUND, INVALID_CREDS_MSG);
        }

        handleActivity(user);

        if (user.isLoggedIn()) {
            return "You are already logged in. Your token was: " + authTokenService.retrieveToken(user).getToken();
        }

        if (user.getBlockedUntil() != null && user.getBlockedUntil().isAfter(Instant.now())) {
            throw new ErrorResponseException(HttpStatus.UNAUTHORIZED, "Login for this user is blocked until: " + user.getBlockedUntil());
        }
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        } catch (Exception e) {
            decreaseLoginAttempt(user);
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, INVALID_CREDS_MSG);
        }
        if (authentication == null) {
            decreaseLoginAttempt(user);
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, INVALID_CREDS_MSG);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        resetLoginAttempt(user);

        return "Your token is: " + authTokenService.generateTokenAndSave(user).getToken() + "\n" + "Press the 'Authorize' button and paste your token there.";
    }

    public void logoutUser(String username) {
        UserEntity user = getLoggedInUser();

        if (!user.isLoggedIn()) {
            throw new ErrorResponseException(HttpStatus.UNAUTHORIZED, NOT_LOGGED_IN);
        }

        if (user.getUsername().equals(username)) {
            logoutUser(user);
            return;
        }

        throw new ErrorResponseException(HttpStatus.BAD_REQUEST, INVALID_CREDS_MSG);
    }

    public String updatePassword(String username, usermanager.v1.model.NewPassword newPassword) {
        UserEntity user = getLoggedInUser();
        handleActivity(user);

        if (!user.getUsername().equals(username)) {
            throw new ErrorResponseException(HttpStatus.UNAUTHORIZED, "You are not allowed to change the password of this user.");
        }

        if (!newPassword.getNewPassword().equals(newPassword.getNewPasswordConfirmation())) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "Passwords do not match.");
        }

        userService.updatePassword(username, encoder.encode(newPassword.getNewPassword()));
        logoutUser(username);

        return "Password changed. Please login again and retrieve a new token.";
    }

    public void deleteUser(String username, String password) {
        var user = getLoggedInUser();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        } catch (Exception e) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "Password is incorrect or user does not exist");
        }

        SecurityContextHolder.clearContext();
        authTokenService.removeToken(user);
        userService.deleteUser(username);
    }

    private void logoutUser(UserEntity user) {
        SecurityContextHolder.clearContext();
        userService.logoutUser(user);
        authTokenService.removeToken(user);
    }

    private UserEntity getLoggedInUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            throw new ErrorResponseException(HttpStatus.UNAUTHORIZED, NOT_LOGGED_IN);
        }
        return (UserEntity) authentication.getPrincipal();
    }

    private UserEntity toUserEntity(usermanager.v1.model.CreateUser user) {
        var userEntity = new UserEntity();
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(encoder.encode(user.getPassword()));
        userEntity.setLoginCounter(3L);
        return userEntity;
    }

    private void decreaseLoginAttempt(UserEntity user) {
        if (user.getLoginCounter() == 0 && user.getBlockedUntil() == null) {
            user.setBlockedUntil(Instant.now().plus(1, ChronoUnit.MINUTES));
            userService.saveUser(user);
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "Login for this user is blocked for 1 minute");
        }

        user.setLoginCounter(user.getLoginCounter() - 1);
        userService.saveUser(user);
        logger.warning("False login Attempt! User: " + user.getUsername() + " Attempt Left: " + user.getLoginCounter());
    }

    protected void resetLoginAttempt(UserEntity userEntity) {
        userEntity.setLoginCounter(3L);
        userEntity.setBlockedUntil(null);
        userEntity.setLoggedIn(true);
        userService.saveUser(userEntity);
    }

    protected Instant handleActivity(UserEntity user) {
        if (Duration.between(user.getLastActivity(), Instant.now()).getSeconds() >= 120) {
            logoutUser(user);
        }
        return userService.saveActivity(user).getLastActivity();
    }
}

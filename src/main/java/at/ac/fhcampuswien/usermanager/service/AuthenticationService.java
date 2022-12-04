package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
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
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthenticationService {
    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());
    private static final String INVALID_CREDS_MSG = "Username or password is invalid.";

    private final UserService userService;
    private final AuthTokenService authTokenService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder encoder;

    public AuthenticationService(
            UserService userService,
            AuthTokenService authTokenService,
            AuthenticationManager authenticationManager,
            BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.authTokenService = authTokenService;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
    }

    public usermanager.v1.model.User addUser(usermanager.v1.model.CreateUser user) {
        var existingUser = userService.getUserEntityByName(user.getUsername());
        if (existingUser != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "The user name is already taken");
        }

        var userEntity = toUserEntity(user);
        userService.addUser(userEntity);
        authTokenService.generateTokenAndSave(userEntity);

        return userService.toUser(userEntity);
    }

    public String loginUser(String username, String password) {
        var user = userService.getUserEntityByName(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, INVALID_CREDS_MSG);
        }

        if (user.isLoggedIn()) {
            return "You are already logged in.";
        }

        if (user.getBlockedUntil() != null && user.getBlockedUntil().isAfter(Instant.now())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Login for this user is blocked until: " + user.getBlockedUntil());
        }

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password));
        if (authentication == null) {
            decreaseLoginAttempt(user);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_CREDS_MSG);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var auth =
                (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        resetLoginAttempt(user);

        return "user :" + auth.getUsername() + " logged in";
    }

    public void logoutUser(String username) {
        var auth =
                (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, INVALID_CREDS_MSG);
        }

        if (!auth.isLoggedIn()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_CREDS_MSG);
        }

        if (auth.getUsername().equals(username)) {
            SecurityContextHolder.clearContext();
            userService.logoutUser(username);
            return;
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_CREDS_MSG);
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
            userService.addUser(user);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Login for this user is blocked for 1 minute");
        }

        user.setLoginCounter(user.getLoginCounter() - 1);
        userService.addUser(user);
        logger.warning(
                "False login Attempt! User: "
                        + user.getUsername()
                        + " Attempt Left: "
                        + user.getLoginCounter());
    }

    protected void resetLoginAttempt(UserEntity userEntity) {
        userEntity.setLoginCounter(3L);
        userEntity.setBlockedUntil(null);
        userEntity.setLoggedIn(true);
        userService.addUser(userEntity);
    }
}

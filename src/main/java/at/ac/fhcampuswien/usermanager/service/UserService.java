package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import usermanager.v1.model.CreateUser;
import usermanager.v1.model.User;

@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final UserRepository userRepository;
    private boolean loginBlocked = false;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    public User addUser(CreateUser user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "given username is already taken");
        }
        var userEntity = userRepository.save(toUserEntity(user));
        return toUser(userEntity);
    }

    public String loginUser(String username, String password) {
        var user = userRepository.findUsersByUsername(username);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Username or password is invalid.");
        }

        if (user.getBlockedUntil() != null && user.getBlockedUntil().isAfter(Instant.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Login for this user is blocked until: " + user.getBlockedUntil());
        }

        if (validateCredentials(user, password)) {
            resetLoginAttempt(user);
            return "logged in!";
        }

        decreaseLoginAttempt(user);

        return "Username or password is invalid.";
    }

    private boolean validateCredentials(UserEntity user, String password) {
        if (user == null) {
            return false;
        }
        return encoder().matches(password, user.getPassword());
    }

    public User getUserByName(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return toUser(userRepository.findUsersByUsername(username));
    }

    private UserEntity toUserEntity(CreateUser user) {
        var userEntity = new UserEntity();
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(encoder().encode(user.getPassword()));
        userEntity.setLoginCounter(3L);
        return userEntity;
    }

    private User toUser(UserEntity userEntity) {
        return new User()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .username(userEntity.getUsername())
                .password(userEntity.getPassword());
    }

    private void decreaseLoginAttempt(UserEntity user) {
        if (user.getLoginCounter() == 0 && user.getBlockedUntil() == null) {
            user.setBlockedUntil(Instant.now().plus(1, ChronoUnit.MINUTES));
            userRepository.save(user);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Login for this user is blocked for 1 minute");
        }

        user.setLoginCounter(user.getLoginCounter() - 1);
        userRepository.save(user);
        logger.warning(
                "False login Attempt! User: "
                        + user.getUsername()
                        + " Attempt Left: "
                        + user.getLoginCounter());
    }

    private void resetLoginAttempt(UserEntity userEntity) {
        userEntity.setLoginCounter(3L);
        userEntity.setBlockedUntil(null);
        userRepository.save(userEntity);
    }
}

package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import usermanager.v1.model.CreateUser;
import usermanager.v1.model.User;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

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

        if (loginBlocked) {
            logger.warning("Login is blocked, try again later.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is blocked, try again later.");
        }

        if (ValidateCridentials(username, password)) {
            var userEntity = userRepository.findUsersByUsername(username);
            resetLoginAttempt(userEntity);
            return "logged in!";
        }

        if (userRepository.existsByUsername(username)) {
            var userEntity = userRepository.findUsersByUsername(username);
            decreaseLoginAttempt(userEntity);

            if (hasNoMoreLoginAttempts(userEntity)) {
                disableLoginMethod();
                Timer timer = new Timer();

                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        resetLoginAttempt(userEntity);
                        reEnableLoginMethod();
                    }
                };

                int delay60Seconds = 1000 * 60;
                timer.schedule(tt, delay60Seconds);
            }
        }
        return "Username or Password are invalid.";
    }

    private boolean ValidateCridentials(String username, String password) {

        boolean userExists = userRepository.existsByUsername(username);
        if (!userExists) {
            return false;
        }

        boolean passwordCorrect = encoder().matches(password, userRepository.findUsersByUsername(username).getPassword());
        return passwordCorrect;
    }


    private boolean hasNoMoreLoginAttempts(UserEntity userEntity) {
        return userEntity.getLoginCounter() == 0;
    }

    public User getUserByName(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found");
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

    private void decreaseLoginAttempt(UserEntity userEntity) {
        userEntity.setLoginCounter(userEntity.getLoginCounter() - 1);
        userRepository.save(userEntity);
        logger.warning("False login Attempt! User: " + userEntity.getUsername() + " Attempt Left: " + userEntity.getLoginCounter());
    }

    private void resetLoginAttempt(UserEntity userEntity) {
        userEntity.setLoginCounter(3L);
        userRepository.save(userEntity);
    }

    private void disableLoginMethod() {
        loginBlocked = true;
    }

    private void reEnableLoginMethod() {
        logger.warning("re enabled");
        loginBlocked = false;
    }
}

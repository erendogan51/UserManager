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

import java.util.logging.Logger;

@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final UserRepository userRepository;

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
        if (!userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password incorrect! Retries left: ");
        } else {
            var userEntity = userRepository.findUsersByUsername(username);
            checkLoginAttempt(userEntity);
            decreaseLoginAttempt(userEntity);
            if (!encoder().matches(password, userRepository.findUsersByUsername(username).getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password incorrect! Retries left: ");
            }
            resetLoginAttempt(userEntity);
            return "logged in!";
        }

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

    private boolean checkLoginAttempt(UserEntity userEntity) {
        if (userEntity.getLoginCounter() <= 0) {
            // TODO: timer for 60sec for the first time
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "User is blocked");
        } else {
            return true;
        }
    }
}

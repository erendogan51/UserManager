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

@Service
public class UserService {
    private final UserRepository userRepository;

    private static int loginCounter = 3;

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
        // FIXME: when user does not exists > NullPointer
        boolean isUserExists = userRepository.existsByUsername(username);
        boolean isPasswordMatched = encoder().matches(password, userRepository.findUsersByUsername(username).getPassword());

        if (!isUserExists || !isPasswordMatched) {
            loginCounter--;
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password incorrect! Retries left: " + loginCounter);
        }
        loginCounter = 3;
        return "logged in!";
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

    private boolean isBlocked() {
        return loginCounter == 0;
    }

}

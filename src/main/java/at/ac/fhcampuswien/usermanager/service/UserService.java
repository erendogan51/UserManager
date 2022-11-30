package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.User;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import usermanager.v1.model.NewUser;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    public User addUser(NewUser newUser) {
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "given username is already taken");
        }
        var user = toUser(newUser);
        userRepository.save(user);
        return user;
    }

    public String loginUser(String username, String password) {
        boolean isPasswordMatches = encoder().matches(password, userRepository.findUsersBy(username).getPassword());
        if (isPasswordMatches) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Username or password incorrect");
        }
        return "logged in!";
    }

    private User toUser(NewUser newUser) {
        var user = new User();
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setUsername(newUser.getUsername());
        user.setPassword(encoder().encode(newUser.getPassword()));

        return user;
    }

    private usermanager.v1.model.NewUser toNewUser(User user) {
        return new NewUser()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername());
    }

    public User getUserByName(String username) {
        return null;
    }
}

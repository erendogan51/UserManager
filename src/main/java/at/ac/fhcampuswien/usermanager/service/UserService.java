package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.User;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import org.springframework.context.annotation.Bean;
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public NewUser addUser(usermanager.v1.model.NewUser user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "given username is already taken");
        }

        return toNewUser(userRepository.save(toUser(user)));
    }

    public void loginUser(String username, String password) {}

    private User toUser(NewUser newUser) {
        var user = new User();
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());

        return user;
    }

    private usermanager.v1.model.NewUser toNewUser(User user) {
        return new NewUser()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername());
    }

    public User getUserByName(String username) {

        return null;
    }
}

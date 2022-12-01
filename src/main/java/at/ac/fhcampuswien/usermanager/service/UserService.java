package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.UserDto;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import usermanager.v1.model.User;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    public User addUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "given username is already taken");
        }
        userRepository.save(toUserDto(user));
        return user;
    }

    public String loginUser(String username, String password) {
        boolean isPasswordMatches = encoder().matches(password, userRepository.findUsersByUsername(username).getPassword());

        if (!isPasswordMatches) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or password incorrect");
        }
        return "logged in!";
    }

    private UserDto toUserDto(User user) {
        var userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(encoder().encode(user.getPassword()));
        return userDto;
    }

    private User toUser(UserDto userDto) {
        return new User().firstName(userDto.getFirstName()).lastName(userDto.getLastName()).password(userDto.getPassword()).username(userDto.getPassword());
    }

    public User getUserByName(String username) {
        return null;
    }
}

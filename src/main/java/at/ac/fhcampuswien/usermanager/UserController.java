package at.ac.fhcampuswien.usermanager;

import at.ac.fhcampuswien.usermanager.entity.User;
import at.ac.fhcampuswien.usermanager.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping(path = "/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(User user) {
        validateUser(user);

        return userService.addUser(user);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user cannot be null");
        }

        if (user.getUserName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username cannot be null");
        }

        if (user.getFirstName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "first name cannot be null");
        }

        if (user.getSurName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "surname cannot be null");
        }

        if (user.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password cannot be null");
        }
    }
}

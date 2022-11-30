package at.ac.fhcampuswien.usermanager.controller;

import at.ac.fhcampuswien.usermanager.entity.User;
import at.ac.fhcampuswien.usermanager.service.UserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import usermanager.v1.model.NewUser;

@Controller
@RequestMapping(path = "/user")
public class UserController implements usermanager.v1.api.UserApi {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<User> createUser(NewUser newUser) {
        validateUser(newUser);

        return ResponseEntity.ok(userService.addUser(newUser));
    }

    @Override
    public ResponseEntity<NewUser> createUsersWithListInput(List<NewUser> newUser) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteUser(String username) {
        return null;
    }

    @Override
    public ResponseEntity<User> getUserByName(String username) {

        return ResponseEntity.ok(userService.getUserByName(username));
    }

    @Override
    public ResponseEntity<String> loginUser(String username, String password) {
        return ResponseEntity.ok(userService.loginUser(username, password));
    }

    @Override
    public ResponseEntity<Void> logoutUser() {
        return null;
    }

    @Override
    public ResponseEntity<Void> updateUser(String username, NewUser newUser) {
        return null;
    }

    private void validateUser(NewUser user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user cannot be null");
        }

        if (user.getUsername() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username cannot be null");
        }

        if (user.getFirstName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "first name cannot be null");
        }

        if (user.getLastName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "surname cannot be null");
        }

        if (user.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password cannot be null");
        }
    }
}

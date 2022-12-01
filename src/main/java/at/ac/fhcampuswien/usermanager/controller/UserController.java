package at.ac.fhcampuswien.usermanager.controller;

import at.ac.fhcampuswien.usermanager.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import usermanager.v1.api.UserApi;
import usermanager.v1.model.CreateUser;
import usermanager.v1.model.User;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(path = "/user")
public class UserController implements UserApi {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<User> createUser(CreateUser user) {
        validateUser(user);

        return ResponseEntity.ok(userService.addUser(user));
    }

    @Override
    public ResponseEntity<User> createUsersWithListInput(@Valid List<CreateUser> UserDto) {
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
    public ResponseEntity<Void> updateUser(String username, CreateUser user) {
        return null;
    }

    private void validateUser(CreateUser createUser) {
        if (createUser == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user cannot be null");
        }

        if (createUser.getUsername() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username cannot be null");
        }

        if (createUser.getFirstName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "first name cannot be null");
        }

        if (createUser.getLastName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "surname cannot be null");
        }

        if (createUser.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password cannot be null");
        }
    }
}

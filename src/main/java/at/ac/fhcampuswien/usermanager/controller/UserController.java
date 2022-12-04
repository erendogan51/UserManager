package at.ac.fhcampuswien.usermanager.controller;

import at.ac.fhcampuswien.usermanager.service.AuthenticationService;
import at.ac.fhcampuswien.usermanager.service.UserService;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;
import usermanager.v1.api.UserApi;
import usermanager.v1.model.CreateUser;
import usermanager.v1.model.User;

@Controller
public class UserController implements UserApi {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @Override
    public ResponseEntity<User> createUser(CreateUser user) {
        validateUser(user);

        return ResponseEntity.ok(authenticationService.addUser(user));
    }

    @Override
    public ResponseEntity<List<User>> createUsersWithListInput(@Valid List<CreateUser> users) {
        var rsp = new ArrayList<User>();
        for (var u : users) {
            rsp.add(authenticationService.addUser(u));
        }

        return ResponseEntity.ok(rsp);
    }

    @Override
    public ResponseEntity<Void> deleteUser(String username) {
        validateUsername(username);
        return null;
    }

    @Override
    public ResponseEntity<User> getUserByName(String username) {
        return ResponseEntity.ok(userService.getUserByName(username));
    }

    @Override
    public ResponseEntity<String> loginUser(String username, String password) {
        validateUsername(username);
        validatePassword(password);

        return ResponseEntity.ok(authenticationService.loginUser(username, password));
    }

    @Override
    public ResponseEntity<Void> logoutUser(String username) {
        validateUsername(username);

        authenticationService.logoutUser(username);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> updateUser(String username, CreateUser user) {
        return null;
    }

    @Override
    public ResponseEntity<String> updatePassword(String username, String newPassword) {
        return ResponseEntity.ok(authenticationService.updatePassword(username, newPassword));
    }

    private void validateUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is invalid");
        }
    }

    private void validatePassword(String password) {
        if (StringUtils.isBlank(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is invalid");
        }
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

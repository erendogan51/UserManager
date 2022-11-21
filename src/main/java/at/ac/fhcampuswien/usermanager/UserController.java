package at.ac.fhcampuswien.usermanager;

import at.ac.fhcampuswien.usermanager.service.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
}

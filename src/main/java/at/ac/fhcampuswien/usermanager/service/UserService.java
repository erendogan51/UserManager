package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.User;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "given username is already taken");
        }

        return userRepository.save(user);
    }

    public void loginUser(String username, String password){
        
    }
}

package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import usermanager.v1.model.User;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected UserEntity addUser(UserEntity user) {
        return userRepository.save(user);
    }

    public User getUserByName(String username) {
        var user = userRepository.findUsersByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return toUser(user);
    }

    protected UserEntity getUserEntityByName(String username) {
        return userRepository.findUsersByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findUsersByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found.");
        }

        return user;
    }

    protected User toUser(UserEntity userEntity) {
        return new User()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .username(userEntity.getUsername());
    }

    protected void logoutUser(String user) {
        userRepository.updateLogin(user, false);
    }
}

package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import usermanager.v1.model.User;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    protected UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    public UserEntity getUserEntityByName(String username) {
        var user = userRepository.findUsersByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return userRepository.findUsersByUsername(username);
    }

    /** The UserDetailsService interface by Spring Security is used to retrieve user-related data */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userDetails = userRepository.findUsersByUsername(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("User not found.");
        }
        return userDetails;
    }

    public User toUser(UserEntity userEntity) {
        return new User()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .lastActivity(Date.from(userEntity.getLastActivity()));
    }

    protected void logoutUser(UserEntity user) {
        user.setLoggedIn(false);
        userRepository.save(user);
    }

    public void updatePassword(String username, String encodedPassword) {
        var user = getUserEntityByName(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
        }

        user.setPassword(encodedPassword);
        user.setLoggedIn(false);
        saveUser(user);
    }

    protected void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    protected UserEntity saveActivity(UserEntity user) {
        user.setLastActivity(Instant.now());
        return saveUser(user);
    }
}

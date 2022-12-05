package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Autowired
    UserService userService;

    UserRepository userRepository;

    public UserServiceTest(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    //@Test
    void saveUser() {
        EasyRandom generator = new EasyRandom();
        UserEntity given = generator.nextObject(UserEntity.class);

        userService.saveUser(given);
        var expected = userRepository.findUsersByUsername(given.getUsername());

        assertEquals(given, expected);
    }

    @Test
    void getUserByName() {
    }

    @Test
    void getUserEntityByName() {
    }

    @Test
    void loadUserByUsername() {
    }

    @Test
    void toUser() {
    }

    @Test
    void logoutUser() {
    }

    @Test
    void updatePassword() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void saveActivity() {
    }
}
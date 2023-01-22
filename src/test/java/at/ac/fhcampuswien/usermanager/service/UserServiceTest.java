package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;


class UserServiceTest extends ServiceTestConfig{
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Save User correctly")
    void saveUser_success() {
        UserEntity given = new UserEntity();
        given.setUsername("test");

        userService.saveUser(given);
        var expected = userRepository.findUsersByUsername(given.getUsername());

        assertEquals(given.getUsername(), expected.getUsername());
    }

    @Test
    @DisplayName("get User correctly")
    void getUser_success() {
        UserEntity entity = new UserEntity();
        entity.setUsername("joemama");
        entity.setLastActivity(Instant.now());

        userRepository.save(entity);

        var user = userService.getUserByName("joemama");

        assertNotNull(user);
    }

    @Test
    @DisplayName("get User NOT_FOUND")
    void getUser_notFound() {
        UserEntity entity = new UserEntity();
        entity.setUsername("joemama");
        entity.setLastActivity(Instant.now());

        userRepository.save(entity);

        var user = userService.getUserByName("notExist");

        // TODO: correct assert
        assertNotNull(user);
    }
}
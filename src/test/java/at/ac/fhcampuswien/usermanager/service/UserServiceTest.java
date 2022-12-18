package at.ac.fhcampuswien.usermanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class UserServiceTest extends ServiceTestConfig{
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void test_saveUser() {
        UserEntity given = new UserEntity();
        given.setUsername("test");

        userService.saveUser(given);
        var expected = userRepository.findUsersByUsername(given.getUsername());

        assertEquals(given.getUsername(), expected.getUsername());
    }

}
package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import usermanager.v1.model.User;

import static org.mockito.Mockito.verify;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


class UserServiceTest extends ServiceTestConfig{
    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private EasyRandom easyRandom;
    @BeforeEach
    void setUp() {
        this.userService = new UserService(this.userRepository);
        this.easyRandom = new EasyRandom();
    }

    @Test
    @DisplayName("Save User correctly")
    void saveUser_success() {
        // arrange
        String username = "test";
        UserEntity user = easyRandom.nextObject(UserEntity.class);
        user.setUsername(username);

        // act
        when(userService.saveUser(user)).thenReturn(user);
        var result = userService.saveUser(user);
        // assert
        assertEquals(result.getUsername(), user.getUsername());
    }

    @Test
    @DisplayName("get User correctly")
    void getUser_success() {
        // arrange
        UserEntity user = easyRandom.nextObject(UserEntity.class);
        String username = "test";
        user.setUsername(username);

        // act
        when(userRepository.findUsersByUsername(username)).thenReturn(user);
        var result = userService.getUserByName(username);

        // assert
        assertEquals(result.getUsername(), user.getUsername());
    }

    @Test
    @DisplayName("get User NOT_FOUND")
    void getUser_notFound() {
        // arrange
        UserEntity user = easyRandom.nextObject(UserEntity.class);
        String username = "test";
        user.setUsername(username);

        // act
        when(userRepository.findUsersByUsername(username)).thenReturn(null);
        var result = userService.getUserByName(username);

        // assert

    }
}
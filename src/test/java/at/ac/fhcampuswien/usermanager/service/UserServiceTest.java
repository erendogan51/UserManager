package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import usermanager.v1.model.User;

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
        UserEntity userEntity = easyRandom.nextObject(UserEntity.class);
        userEntity.setUsername("test");

        // act
        when(userService.saveUser(userEntity)).thenReturn(userEntity);

        // assert
        assertEquals("test", userEntity.getUsername());
    }

    //@Test
    @DisplayName("get User correctly")
    void getUser_success() {
        // arrange
        User user = easyRandom.nextObject(User.class);
        user.setUsername("test");

        // act
        when(userService.getUserByName("test")).thenReturn(user);

        // assert
        //assertEquals(user, );
    }

    //@Test
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
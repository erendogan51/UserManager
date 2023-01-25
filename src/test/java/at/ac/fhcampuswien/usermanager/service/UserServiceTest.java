package at.ac.fhcampuswien.usermanager.service;

import at.ac.fhcampuswien.usermanager.entity.UserEntity;
import at.ac.fhcampuswien.usermanager.repository.UserRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;
import usermanager.v1.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


class UserServiceTest extends ServiceTestConfig {
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
        UserEntity expected = easyRandom.nextObject(UserEntity.class);
        expected.setUsername(username);
        when(userService.saveUser(expected)).thenReturn(expected);

        // act
        UserEntity actual = userService.saveUser(expected);

        // assert
        assertEquals(expected.getUsername(), actual.getUsername());
    }

    @Test
    @DisplayName("get User correctly")
    void getUserEntityByName_success() {
        // arrange
        UserEntity expected = easyRandom.nextObject(UserEntity.class);
        String username = "test";
        expected.setUsername(username);
        when(userRepository.findUsersByUsername(username)).thenReturn(expected);

        // act
        UserEntity actual = userService.getUserEntityByName(username);

        // assert
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getId(), actual.getId());
    }

    @Test
    @DisplayName("get User NOT_FOUND")
    void getUserEntityByName_notFound() {
        // arrange
        UserEntity user = easyRandom.nextObject(UserEntity.class);
        String username = "test";
        user.setUsername(username);
        when(userRepository.findUsersByUsername(username)).thenReturn(null);

        // act & assert
        assertThrows(ResponseStatusException.class, () -> userService.getUserEntityByName(username));
    }


    @Test
    @DisplayName("get User correctly")
    void loadUserByUsername_success() {
        // arrange
        UserEntity expected = easyRandom.nextObject(UserEntity.class);
        String username = "test";
        expected.setUsername(username);
        when(userRepository.findUsersByUsername(username)).thenReturn(expected);

        // act
        UserDetails actual = userService.loadUserByUsername(username);

        // assert
        assertEquals(actual.getUsername(), expected.getUsername());
        assertEquals(actual.getPassword(), expected.getPassword());
    }

    @Test
    @DisplayName("get User NOT_FOUND")
    void loadUserByUsername_notFound() {
        // arrange
        UserEntity user = easyRandom.nextObject(UserEntity.class);
        String username = "test";
        user.setUsername(username);
        when(userRepository.findUsersByUsername(username)).thenReturn(null);

        // act & assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    @DisplayName("toUser mapping")
    void toUser_test() {
        // arrange
        UserEntity expected = easyRandom.nextObject(UserEntity.class);

        // act
        User actual = userService.toUser(expected);

        // assert
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertNotEquals(expected.getLastActivity(), actual.getLastActivity());

    }

    @Test
    @DisplayName("Log out success")
    void logoutUser() {
        // arrange
        UserEntity expected = easyRandom.nextObject(UserEntity.class);

        // act
        userService.logoutUser(expected);

        // assert
        assertEquals(expected.isLoggedIn(), false);
    }


    @Test
    @DisplayName("update Password success")
    void updatePassword_success() {
        // arrange
        UserEntity expected = easyRandom.nextObject(UserEntity.class);
        String username = "test";
        String password = "someStrongPassword";
        expected.setUsername(username);

        when(userRepository.findUsersByUsername(username)).thenReturn(expected);
        // act
        userService.updatePassword(username, password);

        // assert
        assertEquals(expected.isLoggedIn(), false);
        assertEquals(expected.getPassword(), password);
    }

    @Test
    @DisplayName("update Password, user not Found")
    void updatePassword_notFound() {
        // arrange
        UserEntity expected = easyRandom.nextObject(UserEntity.class);
        String username = "test";
        String password = "someStrongPassword";
        expected.setUsername(username);
        when(userRepository.findUsersByUsername(username)).thenReturn(null);

        // act & assert
        assertThrows(ResponseStatusException.class, () -> userService.updatePassword(username, password));
    }


    @Test
    @DisplayName("delete iser success")
    void deleteUser_success() {
        // arrange
        UserEntity expected = easyRandom.nextObject(UserEntity.class);
        String username = "test";
        expected.setUsername(username);

        // act
        userService.saveUser(expected);
        userService.deleteUser(username);

        // assert
        assertEquals(false, userRepository.existsByUsername(username));
    }

    @Test
    @DisplayName("save user activity success")
    void saveActivity_success() {
        // arrange
        UserEntity expected = easyRandom.nextObject(UserEntity.class);
        String username = "test";
        expected.setUsername(username);
        when(userService.saveUser(expected)).thenReturn(expected);
        // act
        UserEntity actual = userService.saveActivity(expected);

        // assert
        assertEquals(expected.getLastActivity(), actual.getLastActivity());
    }
}
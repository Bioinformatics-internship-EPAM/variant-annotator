package ru.spbstu.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.spbstu.model.User;
import ru.spbstu.repository.UserRepository;

import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;


class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void init() throws IllegalAccessException {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername() {
        given(userRepository.findByUsername("test")).willReturn(new User()
                    .setUsername("test")
                    .setPassword("password"));
        UserDetails userInfo =  userService.loadUserByUsername("test");
        Assertions.assertThat(userInfo.getUsername())
                .isEqualTo("test");
        Assertions.assertThat(userInfo.getPassword())
                .isEqualTo("password");
    }

    @Test
    void loadUserByNotExitedUsername() {
        given(userRepository.findByUsername("test")).willReturn(null);
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("test");
        });
        String expectedMessage = "User not found";
        String actualMessage = exception.getMessage();
        Assertions.assertThat(actualMessage.equals(expectedMessage));
    }

    @Test
    void findUserById() {
    }

    @Test
    void allUsers() {
    }

    @Test
    void saveUser() {
        String username = "test";
        String password = "123";
        User user = new User()
                .setUsername(username)
                .setPassword(password);
        given(userRepository.findByUsername(username)).willReturn(null);
        given(userRepository.save(user)).willReturn(user);
        given(bCryptPasswordEncoder.encode(password)).willReturn("$2a$10$a7OsO0RhFaKziKginvn1.ON95coPosmBE0InUuZB1OLmDzps6t6lu");
        Assertions.assertThat(userService.saveUser(user)).isEqualTo(true);
    }

    @Test
    void deleteUser() {
    }
}
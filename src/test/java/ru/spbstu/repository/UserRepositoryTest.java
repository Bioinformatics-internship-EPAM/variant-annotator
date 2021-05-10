package ru.spbstu.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import ru.spbstu.model.User;
import java.util.Optional;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void simpleInsert() {
        User user = userRepository.save(new User()
                .setUsername("test")
                .setPassword("password"));

        Optional<User> userInformation = userRepository.findById(1L);
        Assertions.assertThat(userInformation)
                .isPresent();
        Assertions.assertThat(userInformation.get().getPassword())
                .isEqualTo(user.getPassword());
    }

    @Test
    void violateUniqueConstraint() {
        userRepository.save(new User()
                .setUsername("test")
                .setPassword("password1"));
        Assertions.assertThatThrownBy(() -> userRepository.save(new User()
                .setUsername("test")
                .setPassword("password1")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findByUsername() {
        User user = userRepository.save(new User()
                .setUsername("test")
                .setPassword("password"));

        Optional<User> userInformation = userRepository.findByUsername("test");
        Assertions.assertThat(userInformation.get().getPassword())
                .isEqualTo(user.getPassword());
    }
}

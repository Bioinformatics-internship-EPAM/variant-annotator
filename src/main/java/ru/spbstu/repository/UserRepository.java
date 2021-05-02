package ru.spbstu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spbstu.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
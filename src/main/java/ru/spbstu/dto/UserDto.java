package ru.spbstu.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.spbstu.model.Role;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {
    @EqualsAndHashCode.Exclude
    private Long id;

    private String username;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private String password;

    @EqualsAndHashCode.Exclude
    private Set<Role> roles = new HashSet<>();
}

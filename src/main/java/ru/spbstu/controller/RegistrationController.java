package ru.spbstu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.spbstu.dto.UserDto;
import ru.spbstu.model.User;
import ru.spbstu.repository.UserRepository;
import ru.spbstu.service.UserService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    private final UserRepository userRepository;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "signup_form";
    }

    @PostMapping("/process_register")
    public String processRegister(@ModelAttribute("user") final UserDto userDto, Model model) {
        User user = new User(userDto);
        Optional<User> userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB.isPresent()) {
            model.addAttribute("usernameError", "An account for that username already exists");
            return "signup_form";
        } else {
            userService.saveUser(user);
            return "register_success";
        }
    }
}

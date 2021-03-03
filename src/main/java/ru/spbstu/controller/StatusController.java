package ru.spbstu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.dto.Status;

@RestController
public class StatusController {
    
    @GetMapping("/status")
    public Status status() {
        return Status.OK;
    }
}

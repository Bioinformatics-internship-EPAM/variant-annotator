package ru.spbstu.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.spbstu.dto.Status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatusController.class)
class StatusControllerTest {

    private static final String STATUS_URL = "/status";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testStatusOK() throws Exception {
        mockMvc.perform(get(STATUS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.OK.getValue()));
    }
}

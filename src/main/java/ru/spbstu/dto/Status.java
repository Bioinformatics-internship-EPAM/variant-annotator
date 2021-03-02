package ru.spbstu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Status {
    OK("ok");

    @JsonProperty("status")
    private final String value;

    public String getValue() {
        return value;
    }

    Status(final String value) {
        this.value = value;
    }
}

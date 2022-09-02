package ru.itsjava.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class Message {
    private User user;
    private final String text;
}

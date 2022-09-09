package ru.itsjava.domain;

import lombok.Data;

@Data
public class Message {
    private final String user;
    private final String text;
    private final int id;


    public Message(String user, String text, int id) {
        this.user = user;
        this.text = text;
        this.id = id;
    }
}

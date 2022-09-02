package ru.itsjava.domain;

import lombok.Getter;

@Getter
public class User {
    private final String name;
    private final String password;
    private final int id;

    public User(String name, String password, int id) {
        this.name = name;
        this.password = password;
        this.id = id;
    }

}

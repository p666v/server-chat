package ru.itsjava.dao;

public interface MessageDao {
    void writeMessage(String name, String message, int user_id);
}

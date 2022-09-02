package ru.itsjava.dao;

import ru.itsjava.domain.Message;

public interface MessageDao {
    void writeMessage(String name, String message, int user_id);
}

package ru.itsjava.dao;

import lombok.AllArgsConstructor;
import ru.itsjava.domain.Message;
import ru.itsjava.domain.User;
import ru.itsjava.utils.Props;

import java.sql.*;

@AllArgsConstructor
public class MessageDaoImpl implements MessageDao{
    private final Props props;


    @Override
    public void writeMessage(String name, String message, int user_id) {

        try (Connection connection = DriverManager.getConnection(
                props.getValue("db.url"),
                props.getValue("db.login"),
                props.getValue("db.password"));
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "insert into schema_itsjava.messages_from_users (name, message, user_id) values (?, ?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, message);
            preparedStatement.setInt(3, user_id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

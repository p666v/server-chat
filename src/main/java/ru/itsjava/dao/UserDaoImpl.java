package ru.itsjava.dao;

import lombok.AllArgsConstructor;
import ru.itsjava.domain.User;
import ru.itsjava.utils.Props;

import java.sql.*;

@AllArgsConstructor
public class UserDaoImpl implements UserDao {
    private final Props props;


    @Override
    public User findByNameAndPassword(String name, String password) {
        try (Connection connection = DriverManager.getConnection(
                props.getValue("db.url"),
                props.getValue("db.login"),
                props.getValue("db.password"));
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select count(*) cnt from schema_itsjava.Users_chat where name = ? and password = ?");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
//            int id = resultSet.getInt("id");
            int userCount = resultSet.getInt("cnt");

            PreparedStatement preparedStatement2 = connection.prepareStatement(
                    "select id from schema_itsjava.Users_chat where name = ? and password = ?");
            preparedStatement2.setString(1, name);
            preparedStatement2.setString(2, password);
            ResultSet resultSet2 = preparedStatement2.executeQuery();
            resultSet2.next();
            int id = resultSet2.getInt("id");


            if (userCount == 1) {
                return new User(name, password, id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new UserNotFoundException("User not found");

    }


    @Override
    public void createUser(String name, String password) {
        try (Connection connection = DriverManager.getConnection(
                props.getValue("db.url"),
                props.getValue("db.login"),
                props.getValue("db.password"));
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into schema_itsjava.Users_chat (name, password) values (?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();

            if (findByNameAndPassword(name, password) != null) {
                System.out.println("!!!ПОЛЬЗОВАТЕЛЬ ЗАГРУЖЕН В БАЗУ ДАННЫХ!!!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


}

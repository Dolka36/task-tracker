package com.dolka36.tasktracker.dao;

import com.dolka36.tasktracker.model.User;
import com.dolka36.tasktracker.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    private static final String SAVE_SQL = """
            INSERT INTO users (name, email)
            VALUES (?, ?);
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT id, name, email
            FROM users
            WHERE id = ?;
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id, name, email
            FROM users;
            """;

    public User save(User user) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     SAVE_SQL,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
            }

            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении пользователя в БД", e);
        }
    }

    public Optional<User> findById(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = new User(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email")
                );
                return Optional.of(user);
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске пользователя с id = " + id, e);
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email")
                );
                users.add(user);
            }

            return users;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка всех пользователей", e);
        }
    }
}
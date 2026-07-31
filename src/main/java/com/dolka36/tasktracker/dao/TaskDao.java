package com.dolka36.tasktracker.dao;

import com.dolka36.tasktracker.model.Task;
import com.dolka36.tasktracker.model.TaskStatus;
import com.dolka36.tasktracker.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDao {

    // SQL-запрос для вставки строки в таблицу tasks
    private static final String SAVE_SQL = """
            INSERT INTO tasks (title, description, status, user_id)
            VALUES (?, ?, ?, ?);
            """;

    // SQL-запрос для поиска по id
    private static final String FIND_BY_ID_SQL = """
            SELECT id, title, description, status, user_id
            FROM tasks
            WHERE id = ?;
            """;

    // SQL-запрос для получения всех задач
    private static final String FIND_ALL_SQL = """
            SELECT id, title, description, status, user_id
            FROM tasks;
            """;

    // SQL-запрос для обновления задачи
    private static final String UPDATE_SQL = """
            UPDATE tasks
            SET title = ?,
                description = ?,
                status = ?,
                user_id = ?
            WHERE id = ?;
            """;

    // SQL-запрос для удаления задачи по id
    private static final String DELETE_SQL = """
            DELETE FROM tasks
            WHERE id = ?;
            """;

    public Task save(Task task) {
        // 1. Открываем соединение и готовим PreparedStatement
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     SAVE_SQL,
                     Statement.RETURN_GENERATED_KEYS // Просим БД вернуть сгенерированный ID
             )) {

            // 2. Заполняем параметры '?' по порядку (нумерация с 1)
            preparedStatement.setString(1, task.getTitle());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setString(3, task.getStatus().name());

            // 3. Обработка null для user_id
            if (task.getUserId() != null) {
                preparedStatement.setLong(4, task.getUserId());
            } else {
                // Если id пользователя не задан, передаем в базу SQL NULL
                preparedStatement.setNull(4, Types.BIGINT);
            }

            // 4. Выполняем команду на изменение базы
            preparedStatement.executeUpdate();

            // 5. Извлекаем сгенерированный ID и записываем его в объект task
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                task.setId(generatedKeys.getLong(1));
            }

            return task;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении задачи в БД", e);
        }
    }

    public Optional<Task> findById(Long id) {
        // 1. Открываем соединение и подготавливаем запрос
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            // 2. Подставляем id вместо '?'
            preparedStatement.setLong(1, id);

            // 3. Для чтения данных (SELECT) выказываем executeQuery()
            ResultSet resultSet = preparedStatement.executeQuery();

            // 4. Проверяем, есть ли запись в ответе от базы
            if (resultSet.next()) {
                // Достаём значения из колонок по их названиям
                Task task = new Task(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        TaskStatus.valueOf(resultSet.getString("status")),
                        resultSet.getObject("user_id", Long.class) // безопасно читает Long, возвращая null если в БД NULL
                );
                return Optional.of(task); // Оборачиваем найденную задачу в Optional
            }

            // Если resultSet.next() вернул false — запись с таким id не найдена
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске задачи с id = " + id, e);
        }
    }

    public List<Task> findAll() {
        // 1. Создаём пустой список для сбора задач
        List<Task> tasks = new ArrayList<>();

        // 2. Открываем соединение и готовим запрос
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            // 3. Выполняем SELECT
            ResultSet resultSet = preparedStatement.executeQuery();

            // 4. Используем WHILE вместо IF!
            while (resultSet.next()) {
                Task task = new Task(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        TaskStatus.valueOf(resultSet.getString("status")),
                        resultSet.getObject("user_id", Long.class)
                );
                // Добавляем созданный объект в наш список
                tasks.add(task);
            }

            return tasks;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка всех задач", e);
        }
    }

    public boolean update(Task task) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            // 1. Заполняем новые значения полей
            preparedStatement.setString(1, task.getTitle());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setString(3, task.getStatus().name());;

            if (task.getUserId() != null) {
                preparedStatement.setLong(4, task.getUserId());
            } else {
                preparedStatement.setNull(4, Types.BIGINT);
            }

            // 2. Обязательно указываем ID обновляемой задачи для условия WHERE
            preparedStatement.setLong(5, task.getId());

            // 3. Выполняем UPDATE
            int updatedRows = preparedStatement.executeUpdate();

            // 4. Если база изменила хотя бы 1 строку, метод вернёт true
            return updatedRows > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении задачи с id = " + task.getId(), e);
        }
    }

    public boolean deleteById(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {

            // 1. Подставляем id удаляемой задачи вместо '?'
            preparedStatement.setLong(1, id);

            // 2. Выполняем DELETE
            int updatedRows = preparedStatement.executeUpdate();

            // 3. Если база удалила хотя бы 1 строку — вернётся true
            return updatedRows > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении задачи с id = " + id, e);
        }
    }
}
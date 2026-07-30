package com.dolka36.tasktracker;

import com.dolka36.tasktracker.util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Открываем соединение в try-with-resources
        try (Connection connection = ConnectionManager.open()) {

            // Проверяем статус подключения
            boolean isValid = connection.isValid(2); // таймаут в секундах

            System.out.println("=== Проверка подключения к БД ===");
            System.out.println("Соединение установлено успешно!");
            System.out.println("Статус соединения (isValid): " + isValid);

        } catch (SQLException e) {
            System.err.println("Не удалось подключиться к базе данных!");
            System.err.println("Причина: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
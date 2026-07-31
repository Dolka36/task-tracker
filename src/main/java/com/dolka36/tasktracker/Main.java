package com.dolka36.tasktracker;

import com.dolka36.tasktracker.console.TaskConsoleUI;
import com.dolka36.tasktracker.dao.TaskDao;
import com.dolka36.tasktracker.dao.UserDao;
import com.dolka36.tasktracker.service.TaskService;
import com.dolka36.tasktracker.service.UserService;

public class Main {

    public static void main(String[] args) {
        // 1. Инициализируем DAO
        TaskDao taskDao = new TaskDao();
        UserDao userDao = new UserDao();

        // 2. Инициализируем сервисы
        TaskService taskService = new TaskService(taskDao, userDao);
        UserService userService = new UserService(userDao);

        // 3. Запускаем UI
        TaskConsoleUI consoleUI = new TaskConsoleUI(taskService, userService);
        consoleUI.start();
    }
}
package com.dolka36.tasktracker.console;

import com.dolka36.tasktracker.model.Task;
import com.dolka36.tasktracker.model.TaskStatus;
import com.dolka36.tasktracker.model.User;
import com.dolka36.tasktracker.service.TaskService;
import com.dolka36.tasktracker.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class TaskConsoleUI {

    private final TaskService taskService;
    private final UserService userService;
    private final Scanner scanner;

    public TaskConsoleUI(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== ДОБРО ПОЖАЛОВАТЬ В TASK TRACKER ===");

        while (true) {
            printMenu();
            System.out.print("Выберите действие: ");

            String command = scanner.nextLine().trim();

            switch (command) {
                case "1" -> showAllTasks();
                case "2" -> findTaskById();
                case "3" -> createTask();
                case "4" -> updateTaskStatus();
                case "5" -> deleteTask();
                case "6" -> showAllUsers();
                case "7" -> createUser();
                case "0" -> {
                    System.out.println("Выход из программы. Хорошего дня!");
                    return;
                }
                default -> System.out.println("Неизвестная команда! Попробуйте снова.\n");
            }
        }
    }

    private void printMenu() {
        System.out.println("""
            
            ----------------------------
            1. Показать все задачи
            2. Найти задачу по ID
            3. Создать новую задачу
            4. Обновить статус задачи
            5. Удалить задачу
            6. Показать всех пользователей
            7. Создать пользователя
            0. Выход
            ----------------------------
            """);
    }

    private void showAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("\nСписок задач пуст!");
        } else {
            System.out.println("\n--- СПИСОК ЗАДАЧ ---");
            for (Task task : tasks) {
                System.out.println(task);
            }
        }
    }

    private void findTaskById() {
        System.out.print("Введите ID задачи: ");
        try {
            Long id = Long.parseLong(scanner.nextLine().trim());
            Optional<Task> taskOptional = taskService.getTaskById(id);

            if (taskOptional.isPresent()) {
                System.out.println("\nНайденная задача: " + taskOptional.get());
            } else {
                System.out.println("\nЗадача с ID " + id + " не найдена!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nОшибка: ID должен быть числом!");
        }
    }

    private void createTask() {
        System.out.print("Введите название задачи: ");
        String title = scanner.nextLine();

        System.out.print("Введите описание задачи: ");
        String description = scanner.nextLine();

        System.out.print("Введите ID пользователя (или нажмите Enter, чтобы пропустить): ");
        String userIdStr = scanner.nextLine().trim();

        Long userId = null;
        if (!userIdStr.isEmpty()) {
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ID пользователя! Создаём задачу без пользователя.");
            }
        }

        try {
            Task createdTask = taskService.createTask(title, description, userId);
            System.out.println("\nУспешно создана задача с ID: " + createdTask.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("\nОшибка при создании: " + e.getMessage());
        }
    }

    private void updateTaskStatus() {
        System.out.print("Введите ID задачи для изменения статуса: ");
        try {
            Long id = Long.parseLong(scanner.nextLine().trim());

            System.out.print("Введите новый статус (NEW, IN_PROGRESS, DONE): ");
            String statusInput = scanner.nextLine();

            TaskStatus newStatus = TaskStatus.fromString(statusInput);

            boolean updated = taskService.updateStatus(id, newStatus);
            if (updated) {
                System.out.println("\nСтатус задачи успешно обновлён!");
            } else {
                System.out.println("\nЗадача с ID " + id + " не найдена!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nОшибка: ID должен быть числом!");
        } catch (IllegalArgumentException e) {
            System.out.println("\nОшибка: " + e.getMessage());
        }
    }

    private void deleteTask() {
        System.out.print("Введите ID задачи для удаления: ");
        try {
            Long id = Long.parseLong(scanner.nextLine().trim());

            boolean deleted = taskService.deleteTask(id);
            if (deleted) {
                System.out.println("\nЗадача успешно удалена!");
            } else {
                System.out.println("\nЗадача с ID " + id + " не найдена!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nОшибка: ID должен быть числом!");
        }
    }

    private void showAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("\nСписок пользователей пуст!");
        } else {
            System.out.println("\n--- СПИСОК ПОЛЬЗОВАТЕЛЕЙ ---");
            for (User user : users) {
                System.out.println(user);
            }
        }
    }

    private void createUser() {
        System.out.print("Введите имя пользователя: ");
        String name = scanner.nextLine();

        System.out.print("Введите email пользователя: ");
        String email = scanner.nextLine();

        try {
            User createdUser = userService.createUser(name, email);
            System.out.println("\nУспешно создан пользователь с ID: " + createdUser.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("\nОшибка при создании пользователя: " + e.getMessage());
        }
    }
}
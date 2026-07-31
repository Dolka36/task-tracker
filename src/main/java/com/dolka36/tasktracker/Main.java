package com.dolka36.tasktracker;

import com.dolka36.tasktracker.dao.TaskDao;
import com.dolka36.tasktracker.model.Task;
import com.dolka36.tasktracker.service.TaskService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // 1. Инициализируем зависимости (DAO -> Service)
        TaskDao taskDao = new TaskDao();
        TaskService taskService = new TaskService(taskDao);

        // 2. Инструмент для чтения ввода пользователя
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== ДОБРО ПОЖАЛОВАТЬ В TASK TRACKER ===");

        // 3. Бесконечный цикл работы меню
        while (true) {
            printMenu();
            System.out.print("Выберите действие: ");

            String command = scanner.nextLine().trim();

            switch (command) {
                case "1" -> showAllTasks(taskService);
                case "2" -> findTaskById(scanner, taskService);
                case "3" -> createTask(scanner, taskService);
                case "4" -> updateTaskStatus(scanner, taskService);
                case "5" -> deleteTask(scanner, taskService);
                case "0" -> {
                    System.out.println("Выход из программы. Хорошего дня!");
                    return; // Завершает работу метода main()
                }
                default -> System.out.println("Неизвестная команда! Попробуйте снова.\n");
            }
        }
    }

    private static void printMenu() {
        System.out.println("""
                
                ----------------------------
                1. Показать все задачи
                2. Найти задачу по ID
                3. Создать новую задачу
                4. Обновить статус задачи
                5. Удалить задачу
                0. Выход
                ----------------------------
                """);
    }

    // 1. Вывод всех задач
    private static void showAllTasks(TaskService taskService) {
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

    // 2. Поиск задачи по ID
    private static void findTaskById(Scanner scanner, TaskService taskService) {
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

    // 3. Создание задачи
    private static void createTask(Scanner scanner, TaskService taskService) {
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

    // 4. Обновление статуса задачи
    private static void updateTaskStatus(Scanner scanner, TaskService taskService) {
        System.out.print("Введите ID задачи для изменения статуса: ");
        try {
            Long id = Long.parseLong(scanner.nextLine().trim());

            System.out.print("Введите новый статус (например, NEW, IN_PROGRESS, DONE): ");
            String newStatus = scanner.nextLine().trim();

            boolean updated = taskService.updateStatus(id, newStatus);
            if (updated) {
                System.out.println("\nСтатус задачи успешно обновлён!");
            } else {
                System.out.println("\nЗадача с ID " + id + " не найдена!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nОшибка: ID должен быть числом!");
        }
    }

    // 5. Удаление задачи
    private static void deleteTask(Scanner scanner, TaskService taskService) {
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
}
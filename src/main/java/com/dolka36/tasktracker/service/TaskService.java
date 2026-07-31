package com.dolka36.tasktracker.service;

import com.dolka36.tasktracker.dao.TaskDao;
import com.dolka36.tasktracker.dao.UserDao;
import com.dolka36.tasktracker.model.Task;
import com.dolka36.tasktracker.model.TaskStatus;

import java.util.List;
import java.util.Optional;

public class TaskService {
    private final TaskDao taskDao;
    private final UserDao userDao; // <-- Добавляем UserDao

    public TaskService(TaskDao taskDao, UserDao userDao) {
        this.taskDao = taskDao;
        this.userDao = userDao;
    }

    public Task createTask(String title, String description, Long userId) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Название задачи не может быть пустым");
        }

        // Проверяем, существует ли пользователь в БД
        if (userId != null) {
            if (userDao.findById(userId).isEmpty()) {
                throw new IllegalArgumentException("Пользователь с ID " + userId + " не существует!");
            }
        }

        Task task = new Task(title, description, TaskStatus.NEW);
        task.setUserId(userId);

        return taskDao.save(task);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskDao.findById(id);
    }

    public List<Task> getAllTasks() {
        return taskDao.findAll();
    }

    public boolean updateStatus(Long id, TaskStatus newStatus) {
        Optional<Task> taskOptional = taskDao.findById(id);

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setStatus(newStatus);
            return taskDao.update(task);
        }

        return false;
    }

    public boolean deleteTask(Long id) {
        return taskDao.deleteById(id);
    }
}
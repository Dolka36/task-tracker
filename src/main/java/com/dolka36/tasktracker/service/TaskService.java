package com.dolka36.tasktracker.service;

import com.dolka36.tasktracker.dao.TaskDao;
import com.dolka36.tasktracker.model.Task;

import java.util.List;
import java.util.Optional;

public class TaskService {
    private final TaskDao taskDao;

    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public Task createTask(String title, String description, Long userId) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Название задачи не может быть пустым");
        }
        Task task = new Task(title, description, "NEW");
        task.setUserId(userId); // привязываем userId (может быть и null)

        return taskDao.save(task);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskDao.findById(id);
    }

    public List<Task> getAllTasks() {
        return taskDao.findAll();
    }

    public boolean updateStatus(Long id, String newStatus) {
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

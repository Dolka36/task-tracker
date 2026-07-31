package com.dolka36.tasktracker.service;

import com.dolka36.tasktracker.dao.UserDao;
import com.dolka36.tasktracker.model.User;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым!");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email пользователя не может быть пустым!");
        }

        User user = new User(name.trim(), email.trim());
        return userDao.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }
}
package com.dolka36.tasktracker.service;

import com.dolka36.tasktracker.dao.UserDao;
import com.dolka36.tasktracker.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Успешное создание пользователя")
    void createUser_Success() {
        // Arrange
        String name = "Иван";
        String email = "ivan@mail.com";
        User savedUser = new User(1L, name, email);

        when(userDao.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.createUser(name, email);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Иван", result.getName());
        assertEquals("ivan@mail.com", result.getEmail());

        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Выброс исключения, если имя пользователя пустое")
    void createUser_ShouldThrowException_WhenNameIsEmpty() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser("   ", "ivan@mail.com")
        );

        assertEquals("Имя пользователя не может быть пустым!", exception.getMessage());
        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("Выброс исключения, если email пользователя пустой")
    void createUser_ShouldThrowException_WhenEmailIsEmpty() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser("Иван", "")
        );

        assertEquals("Email пользователя не может быть пустым!", exception.getMessage());
        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("Поиск пользователя по ID — найден")
    void getUserById_Success() {
        // Arrange
        Long userId = 1L;
        User user = new User(userId, "Анна", "anna@mail.com");

        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.getUserById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Анна", result.get().getName());
        verify(userDao, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Поиск пользователя по ID — не найден")
    void getUserById_NotFound() {
        // Arrange
        Long userId = 999L;
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(userId);

        // Assert
        assertTrue(result.isEmpty());
        verify(userDao, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Получение списка всех пользователей")
    void getAllUsers_Success() {
        // Arrange
        List<User> users = List.of(
                new User(1L, "Иван", "ivan@mail.com"),
                new User(2L, "Мария", "maria@mail.com")
        );
        when(userDao.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        verify(userDao, times(1)).findAll();
    }
}
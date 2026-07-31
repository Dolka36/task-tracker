package com.dolka36.tasktracker.service;

import com.dolka36.tasktracker.dao.TaskDao;
import com.dolka36.tasktracker.dao.UserDao;
import com.dolka36.tasktracker.model.Task;
import com.dolka36.tasktracker.model.TaskStatus;
import com.dolka36.tasktracker.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Подключаем интеграцию Mockito с JUnit 5
class TaskServiceTest {

    @Mock
    private TaskDao taskDao; // Заглушка для TaskDao

    @Mock
    private UserDao userDao; // Заглушка для UserDao

    @InjectMocks
    private TaskService taskService; // Внедряем моки в проверяемый сервис

    @Test
    @DisplayName("Успешное создание задачи, если пользователь существует")
    void createTask_Success_WhenUserExists() {
        // 1. Arrange (Подготовка данных)
        Long userId = 1L;
        User mockUser = new User(userId, "Иван", "ivan@mail.com");
        Task expectedTask = new Task("Купить хлеб", "В магазине у дома", TaskStatus.NEW);
        expectedTask.setId(10L);
        expectedTask.setUserId(userId);

        // Настраиваем поведение заглушек (Mocks)
        when(userDao.findById(userId)).thenReturn(Optional.of(mockUser));
        when(taskDao.save(any(Task.class))).thenReturn(expectedTask);

        // 2. Act (Выполнение метода)
        Task result = taskService.createTask("Купить хлеб", "В магазине у дома", userId);

        // 3. Assert (Проверка результатов)
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Купить хлеб", result.getTitle());
        assertEquals(TaskStatus.NEW, result.getStatus());

        // Проверяем, что методы DAO вызвались ровно 1 раз
        verify(userDao, times(1)).findById(userId);
        verify(taskDao, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Выброс исключения при попытке создать задачу с пустым названием")
    void createTask_ShouldThrowException_WhenTitleIsEmpty() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask("   ", "Описание", null)
        );

        assertEquals("Название задачи не может быть пустым", exception.getMessage());
        // Убеждаемся, что обращение к БД даже не происходило
        verifyNoInteractions(taskDao);
    }

    @Test
    @DisplayName("Выброс исключения, если указанного userId не существует в БД")
    void createTask_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        Long nonExistentUserId = 999L;
        when(userDao.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask("Задача", "Описание", nonExistentUserId)
        );

        assertEquals("Пользователь с ID " + nonExistentUserId + " не существует!", exception.getMessage());
        // Сохранение в БД не должно вызываться
        verify(taskDao, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Успешное обновление статуса задачи")
    void updateStatus_Success() {
        // Arrange
        Long taskId = 1L;
        Task existingTask = new Task("Задача", "Описание", TaskStatus.NEW);
        existingTask.setId(taskId);

        when(taskDao.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskDao.update(existingTask)).thenReturn(true);

        // Act
        boolean isUpdated = taskService.updateStatus(taskId, TaskStatus.IN_PROGRESS);

        // Assert
        assertTrue(isUpdated);
        assertEquals(TaskStatus.IN_PROGRESS, existingTask.getStatus());
        verify(taskDao, times(1)).update(existingTask);
    }

    @Test
    @DisplayName("Возврат false при обновлении статуса несуществующей задачи")
    void updateStatus_ShouldReturnFalse_WhenTaskNotFound() {
        // Arrange
        Long taskId = 99L;
        when(taskDao.findById(taskId)).thenReturn(Optional.empty());

        // Act
        boolean isUpdated = taskService.updateStatus(taskId, TaskStatus.DONE);

        // Assert
        assertFalse(isUpdated);
        verify(taskDao, never()).update(any(Task.class));
    }

    @Test
    @DisplayName("Успешное удаление задачи")
    void deleteTask_Success() {
        // Arrange
        Long taskId = 1L;
        when(taskDao.deleteById(taskId)).thenReturn(true);

        // Act
        boolean isDeleted = taskService.deleteTask(taskId);

        // Assert
        assertTrue(isDeleted);
        verify(taskDao, times(1)).deleteById(taskId);
    }
}
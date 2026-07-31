package com.dolka36.tasktracker.model;

public enum TaskStatus {
    NEW,
    IN_PROGRESS,
    DONE;

    public static TaskStatus fromString(String statusStr) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.name().equalsIgnoreCase(statusStr.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Недопустимый статус: " + statusStr);
    }
}
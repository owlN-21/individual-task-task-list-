package com.example.tasklist.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.io.Serializable;
import java.time.LocalDate;

public class TaskModel implements Serializable {
    private final String description;
    private final LocalDate date;
    private final TaskType type;  // Гарантированно не null
    private boolean completed;
    private LocalDate endDate;

    // Конструктор для разовых задач
    public TaskModel(String description, LocalDate date, TaskType type, LocalDate endDate) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }

        this.description = description;
        this.date = date;
        this.type = type;
        this.endDate = type == TaskType.ONE_TIME ? null : endDate; // endDate только для повторяющихся задач
        this.completed = false;
    }

    // Геттеры
    public TaskType getType() {
        return type; // Гарантированно не null
    }
    // Геттеры
    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }



    public boolean isCompleted() {
        return completed;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
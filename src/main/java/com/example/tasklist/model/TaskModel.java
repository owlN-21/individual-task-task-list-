package com.example.tasklist.model;

import java.io.Serializable;
import java.time.LocalDate;
public class TaskModel implements Serializable {
    private final String description;
    private final LocalDate date;
    private boolean completed;

    public TaskModel(String description, LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.description = description;
        this.date = date;
        this.completed = false;
    }

    // Геттеры и сеттеры
    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
package com.example.tasklist.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class TaskModel implements Serializable {
    private final String description;
    private final LocalDate date;
    private boolean completed;
    private TaskType type; // Тип задачи: разовая, ежедневная, еженедельная
    private LocalDate endDate; // Для повторяющихся задач - дата окончания

    public enum TaskType {
        ONE_TIME, DAILY, WEEKLY
    }


    public TaskModel(String description, LocalDate date, TaskType type) {
        this(description, date, type, null);
    }

    public TaskModel(String description, LocalDate date, TaskType type, LocalDate endDate) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.description = description;
        this.date = date;
        this.completed = false;
        this.type = type;
        this.endDate = endDate;
    }

    // Геттеры
    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return type;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isCompleted() {
        return completed;
    }

    // Сеттер для completed
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

        public boolean isRecurringInstance() {
            return getType() != TaskType.ONE_TIME && !getDate().equals(getOriginalDate());
        }

        public LocalDate getOriginalDate() {
            return getType() == TaskType.ONE_TIME ? getDate() :
                    getDate().with(TemporalAdjusters.previousOrSame(getDate().getDayOfWeek()));
        }


}
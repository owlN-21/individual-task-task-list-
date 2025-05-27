package com.example.tasklist.model;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TaskStorage {
    private final List<TaskModel> tasks = new ArrayList<>();

    public void addTask(TaskModel task) {
        tasks.add(task);
    }

    public List<TaskModel> getTasksForDate(LocalDate date) {
        if (date == null) {
            return Collections.emptyList();
        }
        return tasks.stream()
                .filter(task -> date.equals(task.getDate()))
                .collect(Collectors.toList());
    }

    public List<TaskModel> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public void removeTask(TaskModel task) {
        tasks.remove(task);
    }

//    public void removeTasksForDate(LocalDate date) {
//        tasks.removeIf(task -> task.getDate().equals(date));
//    }



}

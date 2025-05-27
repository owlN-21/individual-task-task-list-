
package com.example.tasklist.controller;

import com.example.tasklist.model.TaskModel;
import com.example.tasklist.model.TaskStorage;
import com.example.tasklist.model.TaskType;
import com.example.tasklist.storage.JsonTaskStorage;
import com.example.tasklist.view.TaskListView;

import java.time.LocalDate;

public class TaskController {
    private final TaskStorage storage;
    private final TaskListView view;

    public TaskController(TaskStorage storage, TaskListView view) {
        this.storage = storage;
        this.view = view;
    }

    public void addTask(TaskModel task) {
        storage.addTask(task);
        view.updateTasks(storage.getTasksForDate(task.getDate()));
    }

    public void showTasksForDate(LocalDate date) {
        view.updateTasks(storage.getTasksForDate(date));
    }

    public void removeTask(TaskModel task) {
        storage.removeTask(task);
        showTasksForDate(task.getDate()); // Обновляем отображение
    }

    public void updateTaskStatus(TaskModel task) {
        storage.saveTasks();
    }
}
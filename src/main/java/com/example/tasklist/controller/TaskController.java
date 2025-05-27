
package com.example.tasklist.controller;

import com.example.tasklist.model.TaskModel;
import com.example.tasklist.model.TaskStorage;
import com.example.tasklist.storage.JsonTaskStorage;
import com.example.tasklist.view.TaskListView;

import java.time.LocalDate;
public class TaskController {
    private final TaskStorage storage;
    private final TaskListView view;

    public TaskController(TaskStorage storage, TaskListView view) {
        this.storage = storage;
        this.view = view;

        // Устанавливаем обработчики
        view.setOnDeleteTask(this::removeTask);
        view.setOnTaskStatusChanged(this::updateTaskStatus);
    }

    public void showTasksForDate(LocalDate date) {
        view.updateTasks(storage.getTasksForDate(date));
    }

    public void addTask(TaskModel task) {
        storage.addTask(task);
        showTasksForDate(task.getDate());
    }

    public void removeTask(TaskModel task) {
        storage.removeTask(task);
        showTasksForDate(task.getDate());
    }

    public void updateTaskStatus(TaskModel task) {
        // Просто сохраняем изменения, так как статус уже обновлен в модели
        storage.saveTasks();
    }
}
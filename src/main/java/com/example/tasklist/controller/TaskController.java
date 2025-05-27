package com.example.tasklist.controller;

import com.example.tasklist.model.TaskModel;
import com.example.tasklist.model.TaskStorage;
import com.example.tasklist.storage.JsonTaskStorage;
import com.example.tasklist.view.TaskListView;

import java.time.LocalDate;

public class TaskController {
    private final TaskStorage storage;
    private final TaskListView view;
    private final JsonTaskStorage jsonStorage;

    public TaskController(TaskStorage storage, TaskListView view, JsonTaskStorage jsonStorage) {
        this.storage = storage;
        this.view = view;
        this.jsonStorage = jsonStorage;
    }

    public void showTasksForDate(LocalDate date) {
        view.updateTasks(storage.getTasksForDate(date));
    }

    public void addTask(TaskModel task) {
        storage.addTask(task);
        view.updateTasks(storage.getTasksForDate(task.getDate()));
        JsonTaskStorage jsonStorage = new JsonTaskStorage();
        jsonStorage.saveTasks(storage.getAllTasks());
    }

    public void removeTask(TaskModel task) {
        storage.removeTask(task);
        view.updateTasks(storage.getTasksForDate(task.getDate()));
        jsonStorage.saveTasks(storage.getAllTasks());
    }


}
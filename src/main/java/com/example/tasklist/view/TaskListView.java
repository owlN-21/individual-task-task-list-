package com.example.tasklist.view;

import com.example.tasklist.model.TaskModel;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.function.Consumer;

public class TaskListView {
    private final ListView<TaskModel> taskList = new ListView<>();



    public TaskListView() {
        configureListView();
        setupContextMenu();
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Удалить");

        deleteItem.setOnAction(e -> {
            TaskModel selectedTask = taskList.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                onDeleteTask.accept(selectedTask);
            }
        });

        contextMenu.getItems().add(deleteItem);
        taskList.setContextMenu(contextMenu);
    }

    private Consumer<TaskModel> onDeleteTask;

    public void setOnDeleteTask(Consumer<TaskModel> handler) {
        this.onDeleteTask = handler;
    }

    private void configureListView() {
        taskList.setCellFactory(lv -> new ListCell<TaskModel>() {
            @Override
            protected void updateItem(TaskModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString()); // Используем реализованный toString()
                }
            }
        });
    }

    public void updateTasks(Collection<TaskModel> tasks) {
        taskList.setCellFactory(lv -> new ListCell<TaskModel>() {
            @Override
            protected void updateItem(TaskModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(""); // Сбрасываем стили
                } else {
                    setText(item.toString());
                    if (item.isCompleted()) {
                        setTextFill(Color.GRAY);
                        setStyle("-fx-strikethrough: true;"); // Зачеркиваем текст
                    } else {
                        setTextFill(Color.BLACK);
                        setStyle("-fx-strikethrough: false;"); // Убираем зачеркивание
                    }
                }
            }
        });
        taskList.getItems().setAll(FXCollections.observableArrayList(tasks));
    }

    public VBox  getView() {
        VBox container = new VBox(5, taskList);
        container.setPadding(new Insets(10));
        return container;
    }


}
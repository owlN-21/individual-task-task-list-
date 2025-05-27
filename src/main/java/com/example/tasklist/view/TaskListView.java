package com.example.tasklist.view;

import com.example.tasklist.model.TaskModel;
import com.example.tasklist.model.TaskType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.Collection;
import java.util.function.Consumer;
public class TaskListView {
    private final ListView<TaskModel> taskList = new ListView<>();

    public TaskListView() {
        configureListView();
        setupContextMenu();
    }

    private void configureListView() {
        taskList.setCellFactory(lv -> new ListCell<TaskModel>() {
            private final CheckBox checkBox = new CheckBox();
            private final HBox container = new HBox(10, checkBox);

            {
                // Обработчик изменения состояния чекбокса
                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    TaskModel item = getItem();
                    if (item != null) {
                        item.setCompleted(newVal);
                        if (onTaskStatusChanged != null) {
                            onTaskStatusChanged.accept(item); // Теперь это вызовет saveTasks()
                        }
                    }
                });
            }

            @Override
            protected void updateItem(TaskModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Формируем текст с информацией о типе задачи
                    String taskText = item.getDescription();
                    if (item.getType() != TaskType.ONE_TIME) {
                        taskText += " (" + item.getType().toString();
                        if (item.getEndDate() != null) {
                            taskText += " до " + item.getEndDate();
                        }
                        taskText += ")";
                    }

                    checkBox.setText(taskText);
                    checkBox.setSelected(item.isCompleted());

                    // Стилизация (как было раньше)
                    if (item.isCompleted()) {
                        checkBox.setStyle("-fx-text-fill: gray; -fx-strikethrough: true;");
                    } else if (item.getDate().isBefore(LocalDate.now())) {
                        checkBox.setStyle("-fx-text-fill: red; -fx-strikethrough: false;");
                    } else {
                        checkBox.setStyle("-fx-text-fill: black; -fx-strikethrough: false;");
                    }

                    setGraphic(container);
                }
            }
        });
    }

    private Consumer<TaskModel> onTaskStatusChanged;
    private Consumer<TaskModel> onDeleteTask;

    public void setOnTaskStatusChanged(Consumer<TaskModel> handler) {
        this.onTaskStatusChanged = handler;
    }

    public void setOnDeleteTask(Consumer<TaskModel> handler) {
        this.onDeleteTask = handler;
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Удалить задачу");

        deleteItem.setOnAction(e -> {
            TaskModel selected = taskList.getSelectionModel().getSelectedItem();
            if (selected != null && onDeleteTask != null) {
                onDeleteTask.accept(selected);
            }
        });

        contextMenu.getItems().add(deleteItem);
        taskList.setContextMenu(contextMenu);
    }

    public void updateTasks(Collection<TaskModel> tasks) {
        taskList.getItems().setAll(FXCollections.observableArrayList(tasks));
    }

    public VBox getView() {
        VBox container = new VBox(5, taskList);
        container.setPadding(new Insets(10));

        // Ограничиваем высоту и включаем прокрутку
        taskList.setPrefHeight(100); // Фиксированная высота
        taskList.setMaxHeight(Control.USE_PREF_SIZE); // Запрещаем растягиваться

        return container;
    }


}
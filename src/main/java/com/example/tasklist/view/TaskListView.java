package com.example.tasklist.view;

import com.example.tasklist.model.TaskModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.function.Consumer;

import static javafx.scene.control.PopupControl.USE_PREF_SIZE;

public class TaskListView {
    private final ListView<TaskModel> taskList = new ListView<>();
    private final ScrollPane scrollPane = new ScrollPane();
    private Consumer<TaskModel> onTaskStatusChanged;

    public TaskListView() {
        configureListView();
        setupContextMenu();
        setupScrollPane();
    }

    private void setupScrollPane() {
        scrollPane.setContent(taskList);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent;");
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Удалить");
        deleteItem.setOnAction(e -> {
            TaskModel selectedTask = taskList.getSelectionModel().getSelectedItem();
            if (selectedTask != null && onDeleteTask != null) {
                onDeleteTask.accept(selectedTask);
            }
        });
        contextMenu.getItems().add(deleteItem);
        taskList.setContextMenu(contextMenu);
    }

    public void setOnTaskStatusChanged(Consumer<TaskModel> handler) {
        this.onTaskStatusChanged = handler;
    }

    private Consumer<TaskModel> onDeleteTask;

    public void setOnDeleteTask(Consumer<TaskModel> handler) {
        this.onDeleteTask = handler;
    }

    private void configureListView() {
        taskList.setCellFactory(lv -> new ListCell<TaskModel>() {
            private final CheckBox checkBox = new CheckBox();
            private final HBox container = new HBox(10, checkBox);
            private final Label label = new Label();
            private final Label typeLabel = new Label();

            {
                container.setAlignment(Pos.CENTER_LEFT);
                container.getChildren().addAll(label, typeLabel);

                // Остальная инициализация без изменений
            }

            @Override
            protected void updateItem(TaskModel task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(task.isCompleted());
                    label.setText(task.getDescription());

                    // Устанавливаем текст и стиль для типа задачи
                    switch (task.getType()) {
                        case DAILY:
                            typeLabel.setText("(ежедневно)");
                            typeLabel.setStyle("-fx-text-fill: blue;");
                            break;
                        case WEEKLY:
                            typeLabel.setText("(еженедельно)");
                            typeLabel.setStyle("-fx-text-fill: green;");
                            break;
                        default:
                            typeLabel.setText("");
                    }

                    if (task.isCompleted()) {
                        label.setStyle("-fx-text-fill: gray; -fx-strikethrough: true;");
                    } else {
                        label.setStyle("-fx-text-fill: black; -fx-strikethrough: false;");
                    }
                    setGraphic(container);
                }
            }
        });
    }

    public void updateTasks(Collection<TaskModel> tasks) {
        taskList.getItems().setAll(FXCollections.observableArrayList(tasks));
    }

    public VBox getView() {
        VBox container = new VBox(5, scrollPane); // Используем ScrollPane вместо taskList
        container.setPadding(new Insets(10));

        // Настройки для правильного растягивания
        scrollPane.setPrefViewportHeight(200);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return container;
    }
}
package com.example.tasklist;
import com.example.tasklist.controller.CalendarController;
import com.example.tasklist.controller.TaskController;
import com.example.tasklist.model.CalendarModel;
import com.example.tasklist.model.TaskModel;
import com.example.tasklist.model.TaskStorage;
import com.example.tasklist.storage.JsonTaskStorage;
import com.example.tasklist.view.CalendarView;
import com.example.tasklist.view.TaskListView;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main extends Application {

    private TaskStorage taskStorage;
    private JsonTaskStorage jsonTaskStorage;
    private TaskController taskController;

    @Override
    public void start(Stage stage) {
        taskStorage = new TaskStorage();
        jsonTaskStorage = new JsonTaskStorage();

        // Загружаем сохранённые задачи из файла
        List<TaskModel> loadedTasks = jsonTaskStorage.loadTasks();
        loadedTasks.forEach(taskStorage::addTask);

        // Интерфейс задач
        TaskListView taskView = new TaskListView();

        // задачи на сегодня
        LocalDate today = LocalDate.now();
        taskView.updateTasks(taskStorage.getTasksForDate(today));

        // Контроллер задач
        taskController = new TaskController(taskStorage, taskView, jsonTaskStorage);

        // Интерфейс календаря
        CalendarModel calendarModel = new CalendarModel();
        CalendarView calendarView = new CalendarView();
        CalendarController calendarController = new CalendarController(calendarModel, calendarView, taskController);



        // Кнопка удаления задач по дате
        taskView.setOnDeleteTask(task -> {
            taskController.removeTask(task);
        });


        // Основной макет
        BorderPane root = new BorderPane();
        root.setTop(calendarView.getTopBar());
        root.setCenter(calendarView.getMainContainer());
        root.setBottom(new VBox(taskView.getView()));

        stage.setScene(new Scene(root, 600, 500));
        stage.setTitle("Task Calendar");
        stage.show();

        // Сохраняем задачи при закрытии
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jsonTaskStorage.saveTasks(taskStorage.getAllTasks());
        }));
    }

    private void showAddTaskDialog() {
        Dialog<TaskModel> dialog = new Dialog<>();
        dialog.setTitle("Новая задача");

        Label descLabel = new Label("Описание:");
        TextField descField = new TextField();
        Label dateLabel = new Label("Дата:");
        DatePicker datePicker = new DatePicker(LocalDate.now());

        VBox content = new VBox(10, descLabel, descField, dateLabel, datePicker);
        dialog.getDialogPane().setContent(content);

        ButtonType okButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == okButton) {
                return new TaskModel(descField.getText(), datePicker.getValue());
            }
            return null;
        });

        Optional<TaskModel> result = dialog.showAndWait();
        result.ifPresent(task -> {
            if (task.getDate() != null) {
                taskStorage.addTask(task);
                taskController.showTasksForDate(task.getDate());
            }
        });
    }

//    private void showDeleteTaskDialog() {
//        Dialog<LocalDate> dialog = new Dialog<>();
//        dialog.setTitle("Удаление задач по дате");
//
//        DatePicker datePicker = new DatePicker(LocalDate.now());
//        dialog.getDialogPane().setContent(datePicker);
//
//        ButtonType deleteButton = new ButtonType("Удалить", ButtonBar.ButtonData.OK_DONE);
//        dialog.getDialogPane().getButtonTypes().addAll(deleteButton, ButtonType.CANCEL);
//
//        dialog.setResultConverter(btn -> {
//            if (btn == deleteButton) {
//                return datePicker.getValue();
//            }
//            return null;
//        });
//
//        dialog.showAndWait().ifPresent(date -> {
//            taskStorage.removeTasksForDate(date);
//            taskController.showTasksForDate(date);
//        });
//    }

    public static void main(String[] args) {
        launch(args);
    }
}

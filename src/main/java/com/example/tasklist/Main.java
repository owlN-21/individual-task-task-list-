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

        stage.setScene(new Scene(root, 600, 650));
        stage.setTitle("Task Calendar");
        stage.show();

        // Сохраняем задачи при закрытии
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jsonTaskStorage.saveTasks(taskStorage.getAllTasks());
        }));

        // Создаем основной контейнер для списка задач
        Region taskListView = taskView.getView();

        // Настраиваем скроллинг в главном контейнере
        VBox bottomContainer = new VBox(10, taskListView);
        bottomContainer.setPadding(new Insets(10));

        // Ключевые настройки для скроллинга:
        VBox.setVgrow(taskListView, Priority.ALWAYS); // Растягиваем список
        bottomContainer.setMinHeight(100); // Минимальная высота

        root.setBottom(bottomContainer);


    }



    public static void main(String[] args) {
        launch(args);
    }
}

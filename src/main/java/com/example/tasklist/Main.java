
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
    @Override
    public void start(Stage stage) {
        TaskStorage taskStorage = new JsonTaskStorage();
        TaskListView taskView = new TaskListView();
        TaskController taskController = new TaskController(taskStorage, taskView);

        // Показываем задачи на сегодня
        taskController.showTasksForDate(LocalDate.now());

        CalendarModel calendarModel = new CalendarModel();
        CalendarView calendarView = new CalendarView();
        CalendarController calendarController = new CalendarController(calendarModel, calendarView, taskController);

        BorderPane root = new BorderPane();
        root.setTop(calendarView.getTopBar());
        root.setCenter(calendarView.getMainContainer());
        root.setBottom(taskView.getView());

        stage.setScene(new Scene(root, 600, 500));
        stage.setTitle("Task Calendar");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
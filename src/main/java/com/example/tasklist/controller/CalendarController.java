package com.example.tasklist.controller;


import com.example.tasklist.model.CalendarModel;
import com.example.tasklist.model.TaskModel;
import com.example.tasklist.view.CalendarView;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.YearMonth;

public class CalendarController  {
    private CalendarModel model;
    private CalendarView view;
    private YearMonth currentYearMonth;
    private final TaskController taskController;


    public CalendarController(CalendarModel model, CalendarView view, TaskController taskController) {
        this.model = model;
        this.view = view;
        this.currentYearMonth = YearMonth.now();
        this.taskController = taskController;

        model.setSelectedDate(LocalDate.now());

        setupEventHandlers();
        updateView();
    }

    private void setupEventHandlers() {


        view.getAddButton().setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Добавить задачу");
            dialog.setHeaderText("Введите описание задачи");
            dialog.setContentText("Задача:");

            dialog.showAndWait().ifPresent(text -> {
                if (!text.trim().isEmpty()) {
                    TaskModel newTask = new TaskModel(text, model.getSelectedDate());
                    taskController.addTask(newTask);  // метод, который добавляет задачу в storage
                }
            });
        });

        // Навигация по месяцам
        ((Button) view.getTopBar().getChildren().get(1)).setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateView();
        });

        ((Button) view.getTopBar().getChildren().get(2)).setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateView();
        });

        ((Button) view.getTopBar().getChildren().get(3)).setOnAction(e -> {
            LocalDate today = LocalDate.now();
            model.setSelectedDate(today);
            currentYearMonth = YearMonth.now();
            updateView();
            taskController.showTasksForDate(today);
        });
    }




    public void handleDayClick(MouseEvent event, LocalDate date) {
        if (date == null) return;

        model.setSelectedDate(date);
        taskController.showTasksForDate(date);
        updateView();
    }

    private void updateView() {
//        model.setSelectedDate(null);
        // Получаем ссылку на GridPane
        GridPane calendarGrid = view.getCalendarGrid();

        // Очищаем существующие ограничения
        calendarGrid.getColumnConstraints().clear();
        calendarGrid.getRowConstraints().clear();

        calendarGrid.setAlignment(Pos.CENTER);


        // 1. Настраиваем столбцы (7 дней недели)
        for (int i = 0; i < 7; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHalignment(HPos.CENTER);
            calendarGrid.getColumnConstraints().add(column);
        }

        // 2. Настраиваем строки (1 строка заголовков + 6 строк для дат)
        for (int i = 0; i < 7; i++) {
            RowConstraints row = new RowConstraints();
            row.setValignment(VPos.CENTER);
            calendarGrid.getRowConstraints().add(row);
        }

        // 3. Обновляем заголовок
        view.updateCalendar(currentYearMonth);

        // 4. Добавляем ячейки с датами
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        int daysInMonth = currentYearMonth.lengthOfMonth();

        int row = 1;
        int col = dayOfWeek - 1;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            boolean isMarked = date.equals(model.getSelectedDate());
            boolean isToday = date.equals(LocalDate.now());

            StackPane dayCell = view.createDayCell(date, isMarked, isToday);
            dayCell.setOnMouseClicked(e -> handleDayClick(e, date));


            calendarGrid.add(dayCell, col, row);
            col++;

            if (col == 7) {
                col = 0;
                row++;
            }
        }

        System.out.println("Columns: " + calendarGrid.getColumnConstraints().size());
        System.out.println("Rows: " + calendarGrid.getRowConstraints().size());

    }
}
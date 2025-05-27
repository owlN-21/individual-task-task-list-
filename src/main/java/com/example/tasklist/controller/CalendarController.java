package com.example.tasklist.controller;


import com.example.tasklist.model.CalendarModel;
import com.example.tasklist.model.TaskModel;
import com.example.tasklist.model.TaskType;
import com.example.tasklist.view.CalendarView;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.YearMonth;

public class CalendarController  {
    private CalendarModel model;
    private CalendarView view;
    private YearMonth currentYearMonth;
    private final TaskController taskController;


    public LocalDate getSelectedDate() {
        return model.getSelectedDate();
    }

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
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            // Элементы формы
            TextField descriptionField = new TextField();
            DatePicker datePicker = new DatePicker(model.getSelectedDate());
            ComboBox<TaskType> typeCombo = new ComboBox<>(FXCollections.observableArrayList(TaskType.values()));
            typeCombo.getSelectionModel().selectFirst();

            DatePicker endDatePicker = new DatePicker();
            CheckBox hasEndDateCheck = new CheckBox("Повторять до:");

            // Настройка видимости endDatePicker
            endDatePicker.setVisible(false);
            hasEndDateCheck.setVisible(false);

            typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                boolean isRecurring = newVal != TaskType.ONE_TIME;
                hasEndDateCheck.setVisible(isRecurring);
                endDatePicker.setVisible(isRecurring && hasEndDateCheck.isSelected());
            });

            hasEndDateCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
                endDatePicker.setVisible(newVal);
            });

            // Размещение элементов
            grid.addRow(0, new Label("Описание:"), descriptionField);
            grid.addRow(1, new Label("Тип задачи:"), typeCombo);
            grid.addRow(2, new Label("Дата:"), datePicker);
            grid.addRow(3, hasEndDateCheck, endDatePicker);

            // Настройка диалога
            Dialog<TaskModel> dialog = new Dialog<>();
            dialog.setTitle("Добавить задачу");
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    String description = descriptionField.getText().trim();
                    if (description.isEmpty()) {
//                        showAlert("Ошибка", "Введите описание задачи");
                        return null;
                    }

                    TaskType type = typeCombo.getValue();
                    LocalDate date = datePicker.getValue();
                    LocalDate endDate = (type != TaskType.ONE_TIME && hasEndDateCheck.isSelected())
                            ? endDatePicker.getValue()
                            : null;

                    return new TaskModel(description, date, type, endDate);
                }
                return null;
            });

            dialog.showAndWait().ifPresent(taskController::addTask);
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
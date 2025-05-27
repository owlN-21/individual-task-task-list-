package com.example.tasklist.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CalendarView {
    private GridPane calendarGrid;
    private Label monthYearLabel;
    private HBox topBar;
    private Button addButton;  // Новая кнопка
    private VBox mainContainer;  // Главный контейнер



    public CalendarView() {

        calendarGrid = new GridPane();
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);
        calendarGrid.setAlignment(Pos.CENTER);


        monthYearLabel = new Label();
        topBar = createTopBar();

        HBox footer = createFooter();

        mainContainer = new VBox(10, topBar, calendarGrid, footer);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(10));

    }

    private void updateMonthYearLabel(YearMonth yearMonth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        String formatted = yearMonth.format(formatter);
        monthYearLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16; -fx-font-weight: bold;");
        monthYearLabel.setText(formatted.substring(0, 1).toUpperCase() + formatted.substring(1));
    }

    private HBox createTopBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);  // Выравнивание по левому краю
        bar.setPadding(new Insets(5, 10, 5, 10));  // Отступы внутри панели
        bar.setStyle(" -fx-border-radius: 5;");  // Стиль фона

        // Кнопки (изначально скрыты)
        Button prevBtn = new Button("<");
        Button nextBtn = new Button(">");
        Button todayBtn = new Button("Today");
        prevBtn.setVisible(false);
        nextBtn.setVisible(false);
        todayBtn.setVisible(false);

        bar.getChildren().addAll( monthYearLabel, prevBtn,   nextBtn, todayBtn);

            // Показываем кнопки при наведении
        bar.setOnMouseEntered(e -> {
            prevBtn.setVisible(true);
            nextBtn.setVisible(true);
            todayBtn.setVisible(true);
        });

        // Скрываем кнопки, когда курсор уходит
        bar.setOnMouseExited(e -> {
            prevBtn.setVisible(false);
            nextBtn.setVisible(false);
            todayBtn.setVisible(false);
        });

        return bar;

    }

    private HBox createFooter(){
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 0, 0, 0));

        // Надпись "Task list" (справа)
        Label taskListLabel = new Label("Task list");
        taskListLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Кнопка "Добавить" (слева)
        addButton = new Button("Add");
        addButton.setStyle("-fx-font-size: 16; -fx-background-color: #4CAF50; -fx-text-fill: white;");


        // пространство между кнопкой и надписью
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        footer.getChildren().addAll( taskListLabel, spacer, addButton);
        return footer;
    }


    public void updateCalendar(YearMonth yearMonth) {
        calendarGrid.getChildren().clear();
        updateMonthYearLabel(yearMonth);


        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < dayNames.length; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.setStyle("-fx-font-weight: bold;");
            calendarGrid.add(dayLabel, i, 0);


        }
    }



    // Геттеры для доступа из контроллера
    public GridPane getCalendarGrid() { return calendarGrid; }
    public HBox getTopBar() { return topBar; }


    public Label getMonthYearLabel() { return monthYearLabel; }
    public VBox getMainContainer() { return mainContainer; }
    public Button getAddButton() { return addButton; }


    public StackPane createDayCell(LocalDate date, boolean isMarked, boolean isToday) {
        StackPane dayCell = new StackPane();
        Circle circ = new Circle(25);
        circ.setStyle("-fx-fill: transparent; -fx-stroke-width: 2;");
        dayCell.setAlignment(Pos.CENTER);

        Label dayLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayLabel.setTextFill(Color.GREEN);

        dayCell.getChildren().addAll(circ, dayLabel);

        if (isToday) {
            circ.setStyle("-fx-fill: #c8f0d5; -fx-stroke: #009900; -fx-stroke-width: 2;");
        }

        if (isMarked) {
            if (isToday) {
                circ.setStyle("-fx-fill: #c8f0d5; -fx-stroke: #009900; -fx-stroke-width: 2;");
            }
            else {
                circ.setStyle("-fx-fill: #c8f0d5; -fx-stroke-width: 2;");
            }
        }

        return dayCell;
    }
}
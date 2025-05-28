package com.example.tasklist.storage;



import com.example.tasklist.model.TaskModel;
import com.example.tasklist.model.TaskStorage;
import com.example.tasklist.model.TaskType;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class JsonTaskStorage implements TaskStorage {
    private final List<TaskModel> tasks = new ArrayList<>();
    private final String FILE_NAME = "tasks.json";
    private final Gson gson;

    public JsonTaskStorage() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        loadTasks();
    }

    @Override
    public void addTask(TaskModel task) {
        if (task.getType() == TaskType.WEEKLY && task.getEndDate() != null) {
            addWeeklyTasks(task);
        } else if (task.getType() == TaskType.DAILY && task.getEndDate() != null) {
            addDailyTasks(task);
        } else {
            addSingleTask(task);
        }
        saveTasks();
    }

    private void addWeeklyTasks(TaskModel template) {
        LocalDate current = template.getDate();
        DayOfWeek targetDayOfWeek = current.getDayOfWeek();

        while (!current.isAfter(template.getEndDate())) {
            if (current.getDayOfWeek() == targetDayOfWeek) {
                TaskModel weeklyTask = new TaskModel(
                        template.getDescription(),
                        current,
                        TaskType.WEEKLY,
                        template.getEndDate()
                );
                addSingleTask(weeklyTask);
            }
            current = current.plusDays(1);
        }
    }

    private void addDailyTasks(TaskModel template) {
        LocalDate current = template.getDate();
        while (!current.isAfter(template.getEndDate())) {
            TaskModel dailyTask = new TaskModel(
                    template.getDescription(),
                    current,
                    TaskType.DAILY,
                    template.getEndDate()
            );
            addSingleTask(dailyTask);
            current = current.plusDays(1);
        }
    }

    private boolean taskExists(TaskModel newTask) {
        return tasks.stream().anyMatch(existing ->
                existing.getDescription().equals(newTask.getDescription()) &&
                        existing.getDate().equals(newTask.getDate()) &&
                        existing.getType() == newTask.getType()
        );
    }

    private void addSingleTask(TaskModel task) {
        if (!taskExists(task)) {
            tasks.add(task);
        }
    }

    @Override
    public List<TaskModel> getTasksForDate(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.getDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    @Override
    public void removeTask(TaskModel task) {
        // Удаляем только конкретную задачу по точному совпадению всех полей
        tasks.removeIf(t ->
                t.getDescription().equals(task.getDescription()) &&
                        t.getDate().equals(task.getDate()) &&
                        t.getType() == task.getType() &&
                        Objects.equals(t.getEndDate(), task.getEndDate()) &&
                        t.isCompleted() == task.isCompleted()
        );
        saveTasks();
    }

    @Override
    public void saveTasks() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(tasks, writer);
            System.out.println("Сохранено задач: " + tasks.size()); // Для отладки
        } catch (IOException e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    public void loadTasks() {
        try (Reader reader = new FileReader(FILE_NAME)) {
            Type taskListType = new TypeToken<List<TaskModel>>(){}.getType();
            List<TaskModel> loaded = gson.fromJson(reader, taskListType);

            if (loaded != null) {
                tasks.clear();
                tasks.addAll(loaded);
                System.out.println("Загружено выполненных задач: " +
                        tasks.stream().filter(TaskModel::isCompleted).count());
            }
        } catch (IOException e) {
            System.out.println("Файл не найден, создаём новый");
        }
    }

    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate date, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(date.toString());
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString());
        }
    }
}
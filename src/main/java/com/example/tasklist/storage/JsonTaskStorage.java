package com.example.tasklist.storage;



import com.example.tasklist.model.TaskModel;
import com.example.tasklist.model.TaskStorage;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class JsonTaskStorage implements TaskStorage {
    private static final String FILE_NAME = "tasks.json";
    private final Gson gson;
    private final List<TaskModel> tasks = new ArrayList<>();

    public JsonTaskStorage() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
        loadTasks();
    }

    @Override
    public void addTask(TaskModel task) {
        tasks.add(task);
        saveTasks();
    }

    @Override
    public List<TaskModel> getTasksForDate(LocalDate date) {
        return tasks.stream()
                .filter(task -> date.equals(task.getDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    @Override
    public void removeTask(TaskModel task) {
        tasks.remove(task);
        saveTasks();
    }

    @Override
    public void saveTasks() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadTasks() {
        if (!Files.exists(Paths.get(FILE_NAME))) return;

        try (Reader reader = new FileReader(FILE_NAME)) {
            Type taskListType = new TypeToken<List<TaskModel>>() {}.getType();
            List<TaskModel> loadedTasks = gson.fromJson(reader, taskListType);
            if (loadedTasks != null) {
                tasks.clear();
                tasks.addAll(loadedTasks);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
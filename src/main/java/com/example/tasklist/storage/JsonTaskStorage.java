package com.example.tasklist.storage;



import com.example.tasklist.model.TaskModel;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class JsonTaskStorage {
    private static final String FILE_NAME = "tasks.json";
    private final Gson gson;

    public void saveTasks(List<TaskModel> tasks) {
        // Фильтруем только оригинальные задачи (не сгенерированные экземпляры)
        List<TaskModel> tasksToSave = tasks.stream()
                .filter(task -> !task.isRecurringInstance())
                .collect(Collectors.toList());

        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(tasksToSave, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<TaskModel> loadTasks() {
        if (!Files.exists(Paths.get(FILE_NAME))) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(FILE_NAME)) {
            Type taskListType = new TypeToken<List<TaskModel>>() {}.getType();
            return gson.fromJson(reader, taskListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Для сериализации LocalDate
    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate date, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(date.toString());
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDate.parse(json.getAsString());
        }
    }


    private static class TaskModelAdapter implements JsonSerializer<TaskModel>, JsonDeserializer<TaskModel> {
        @Override
        public JsonElement serialize(TaskModel task, Type type, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("description", task.getDescription());
            obj.add("date", context.serialize(task.getDate()));
            obj.addProperty("completed", task.isCompleted());
            obj.add("type", context.serialize(task.getType().name()));
            if (task.getEndDate() != null) {
                obj.add("endDate", context.serialize(task.getEndDate()));
            }
            return obj;
        }

        @Override
        public TaskModel deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String description = obj.get("description").getAsString();
            LocalDate date = context.deserialize(obj.get("date"), LocalDate.class);
            boolean completed = obj.get("completed").getAsBoolean();
            TaskModel.TaskType taskType = TaskModel.TaskType.valueOf(obj.get("type").getAsString());

            LocalDate endDate = obj.has("endDate") ?
                    context.deserialize(obj.get("endDate"), LocalDate.class) :
                    null;

            TaskModel task = new TaskModel(description, date, taskType, endDate);
            task.setCompleted(completed);
            return task;
        }
    }



    public JsonTaskStorage() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(TaskModel.class, new TaskModelAdapter())
                .setPrettyPrinting()
                .create();
    }
}

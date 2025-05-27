package com.example.tasklist.storage;



import com.example.tasklist.model.TaskModel;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class JsonTaskStorage {
    private static final String FILE_NAME = "tasks.json";
    private final Gson gson;

    public JsonTaskStorage() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
    }

    public void saveTasks(List<TaskModel> tasks) {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(tasks, writer);
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


}

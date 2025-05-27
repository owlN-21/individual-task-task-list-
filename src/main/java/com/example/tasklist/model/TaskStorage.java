package com.example.tasklist.model;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TaskStorage {
    private final List<TaskModel> tasks = new ArrayList<>();

    public void addTask(TaskModel task) {
        // Добавляем только оригинальную задачу
        tasks.add(task);
    }

    public List<TaskModel> getTasksForDate(LocalDate date) {
        if (date == null) {
            return Collections.emptyList();
        }

        return tasks.stream()
                .flatMap(task -> generateTaskInstances(task).stream())
                .filter(task -> date.equals(task.getDate()))
                .collect(Collectors.toList());
    }

    private List<TaskModel> generateTaskInstances(TaskModel originalTask) {
        List<TaskModel> instances = new ArrayList<>();
        instances.add(originalTask); // Всегда добавляем оригинальную задачу

        if (originalTask.getType() != TaskModel.TaskType.ONE_TIME) {
            LocalDate currentDate = originalTask.getDate();
            LocalDate endDate = originalTask.getEndDate() != null ?
                    originalTask.getEndDate() :
                    LocalDate.now().plusYears(1); // Дефолтный срок - 1 год

            // Для ежедневных - начинаем со следующего дня
            if (originalTask.getType() == TaskModel.TaskType.DAILY) {
                currentDate = currentDate.plusDays(1);
            }
            // Для еженедельных - через неделю
            else if (originalTask.getType() == TaskModel.TaskType.WEEKLY) {
                currentDate = currentDate.plusWeeks(1);
            }

            // Генерируем задачи пока не выйдем за endDate
            while (!currentDate.isAfter(endDate)) {
                TaskModel recurringInstance = new TaskModel(
                        originalTask.getDescription(),
                        currentDate,
                        originalTask.getType(),
                        originalTask.getEndDate()
                );
                recurringInstance.setCompleted(originalTask.isCompleted());
                instances.add(recurringInstance);

                // Увеличиваем дату в зависимости от типа задачи
                if (originalTask.getType() == TaskModel.TaskType.DAILY) {
                    currentDate = currentDate.plusDays(1);
                } else {
                    currentDate = currentDate.plusWeeks(1);
                }
            }
        }

        return instances;
    }


    private boolean shouldIncludeDate(TaskModel task, LocalDate date) {
        if (task.getType() == TaskModel.TaskType.DAILY) {
            return true;
        } else if (task.getType() == TaskModel.TaskType.WEEKLY) {
            return date.getDayOfWeek() == task.getDate().getDayOfWeek();
        }
        return false;
    }

    // Остальные методы без изменений
    public List<TaskModel> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public void removeTask(TaskModel task) {
        tasks.remove(task);
    }
}
package com.example.tasklist.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarModel {
    private Set<LocalDate> savedDates = new HashSet<>();
    private LocalDate selectedDate;  // Текущая выбранная дата
    private File dataFile;



    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
    }

    public void toggleDate(LocalDate date) {
        if (savedDates.contains(date)) {
            savedDates.remove(date);
        } else {
            savedDates.add(date);
        }
    }

    private void saveData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(dataFile, savedDates);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadData() {
        ObjectMapper mapper = new ObjectMapper();
        if (dataFile.exists()) {
            try {
                savedDates = mapper.readValue(dataFile, new TypeReference<Set<LocalDate>>() {});
            } catch (IOException e) {
                System.err.println("Error loading data: " + e.getMessage());
            }
        }
    }
}
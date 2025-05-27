module com.example.tasklist {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;


    opens com.example.tasklist.model to com.google.gson;
    opens com.example.tasklist to javafx.fxml;
    exports com.example.tasklist;
}
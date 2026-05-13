/**
 * Modul principal de l'aplicacio CercaEvent.
 */
module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.h2database;
    requires jbcrypt;
    
    opens com.example to javafx.fxml;
    opens com.example.controller to javafx.fxml;
    exports com.example;
    exports com.example.controller;
    exports com.example.model;
}

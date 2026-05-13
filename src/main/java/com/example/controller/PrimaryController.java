package com.example.controller;

import java.io.IOException;

import com.example.App;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;

/**
 * Controlador de la pantalla primaria de prova.
 */
public class PrimaryController {

    // @FXML
    // private void initialize() {
    //     App.stage.setOnCloseRequest((WindowEvent e) -> {
    //         Platform.exit();
    //     });
    // }

    /**
     * Canvia a la pantalla de login.
     *
     * @throws IOException si no es pot carregar el FXML
     */
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("login");
    }
}

package com.example;

import java.io.IOException;

import com.example.db.DatabaseInitializer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punt d'entrada de l'aplicacio JavaFX CercaEvent.
 */
public class App extends Application {

    private static Scene scene;
    public static Stage stage;

    /**
     * Inicialitza l'escena principal i mostra la pantalla de login.
     *
     * @param stage finestra principal de JavaFX
     * @throws IOException si no es pot carregar el FXML inicial
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("login"));
        App.stage = stage;
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    /**
     * Canvia el contingut principal de la finestra a un altre FXML.
     *
     * @param fxml nom del fitxer FXML sense extensio
     * @throws IOException si no es pot carregar el recurs FXML
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    /**
     * Carrega un fitxer FXML del paquet de recursos de l'aplicacio.
     *
     * @param fxml nom del fitxer FXML sense extensio
     * @return node arrel carregat
     * @throws IOException si la carrega falla
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Inicialitza les dades essencials i arrenca l'aplicacio JavaFX.
     *
     * @param args arguments de linia de comandes
     */
    public static void main(String[] args) {
        try {
            DatabaseInitializer.initDadesEssencials();
        } catch (Exception e) {
            System.out.println("ERROR inicialitzant la base de dades: " + e.getMessage());
            e.printStackTrace();
        }

        launch();
    }
}

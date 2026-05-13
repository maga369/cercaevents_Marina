package com.example.controller;

import java.io.IOException;

import com.example.App;
import com.example.serveis.ServeiUsuari;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Controlador de la pantalla d'inici de sessio.
 */
public class LoginController {
    // Camps del formulari d'inici de sessio vinculats amb el fitxer FXML.
    @FXML
    private TextField txtUsuari;
    @FXML
    private TextField txtContrasenya;
    @FXML
    private TextField txtMsg;

    /**
     * Valida les credencials introduides i carrega la pantalla principal si son correctes.
     *
     * @throws IOException si no es pot carregar la pantalla principal
     */
    @FXML
    private void validarUsuari() throws IOException {
        //App.setRoot("secondary");
        try{
            // El servei s'encarrega de comprovar si l'usuari existeix i si la contrasenya coincideix.
            ServeiUsuari.existeixUsuari(txtUsuari.getText(), txtContrasenya.getText());
            System.out.println("Validació correcta, carregant la següent pantalla...");
            App.setRoot("primary");
        }catch(Exception e){
            // Si hi ha un error de validacio, es mostra el missatge a la pantalla.
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            txtMsg.setText(e.getMessage());
            txtMsg.setVisible(true);
        }
    }

    /**
     * Canvia a la pantalla de registre de nous usuaris.
     *
     * @throws IOException si no es pot carregar la pantalla de registre
     */
    @FXML
    private void carregarRegistre() throws IOException {
        App.setRoot("usuari");
    }
}

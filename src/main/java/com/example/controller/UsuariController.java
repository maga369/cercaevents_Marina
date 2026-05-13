package com.example.controller;

import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

import com.example.App;
import com.example.dao.UsuariDAO;
import com.example.model.Usuari;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador de la pantalla de registre d'usuaris.
 */
public class UsuariController {

    @FXML private TextField txtUsuari;
    @FXML private TextField txtNom;
    @FXML private TextField txtCognoms;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;

    /**
     * Configura el comportament inicial de la finestra de registre.
     */
    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) txtUsuari.getScene().getWindow();

            stage.setOnCloseRequest(event -> {
                event.consume();
                obrirLogin();
            });
        });
    }

    /**
     * Torna a la pantalla de login.
     */
    public void obrirLogin() {
        try {
            Stage stage = (Stage) txtUsuari.getScene().getWindow();
            stage.setOnCloseRequest(null);
            App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Valida el formulari, crea l'usuari i el desa a la base de dades.
     */
    @FXML
    private void registrarUsuari() {
        String usuari = txtUsuari.getText().trim();
        String nom = txtNom.getText().trim();
        String cognoms = txtCognoms.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (usuari.isEmpty() || nom.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Tots els camps obligatoris han d'estar plens.");
            return;
        }

        if (!isValidEmail(email)) {
            mostrarAlerta("Error", "El format de l'email no es valid (exemple: usuari@domini.com).");
            return;
        }

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        Usuari nouUsuari = new Usuari(usuari, nom, cognoms, email, passwordHash, "USER");

        try {
            if (UsuariDAO.registrar(nouUsuari)) {
                mostrarAlerta("Exit", "Usuari registrat correctament!");
                netejarCamps();
                App.setRoot("login");
            } else {
                mostrarAlerta("Error", "No s'ha pogut registrar l'usuari.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", e.getMessage());
        }
    }

    /**
     * Comprova si un correu electronic te un format valid.
     *
     * @param email correu que es vol validar
     * @return true si el format es valid
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return email != null && pattern.matcher(email).matches();
    }

    /**
     * Mostra una alerta informativa.
     *
     * @param titol titol de la finestra d'alerta
     * @param missatge contingut de l'alerta
     */
    private void mostrarAlerta(String titol, String missatge) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titol);
        alerta.setHeaderText(null);
        alerta.setContentText(missatge);
        alerta.showAndWait();
    }

    /**
     * Buida tots els camps del formulari de registre.
     */
    private void netejarCamps() {
        txtUsuari.clear();
        txtNom.clear();
        txtCognoms.clear();
        txtEmail.clear();
        txtPassword.clear();
    }
}

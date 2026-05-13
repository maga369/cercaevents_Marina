package com.example.controller;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import com.example.App;
import com.example.dao.EventDAO;
import com.example.model.Event;
import com.example.model.Usuari;
import com.example.serveis.ServeiUsuari;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;

/**
 * Controlador de la pantalla principal d'exploracio d'esdeveniments.
 */
public class MainController {

    private enum VistaActual {
        TOTS, MEUS, INSCRITS
    }

    @FXML private TextField txtUbicacio;
    @FXML private DatePicker dpData;
    @FXML private CheckBox chkPlaces;
    @FXML private Button btnTots;
    @FXML private Button btnMeus;
    @FXML private Button btnInscrits;
    @FXML private Button btnCrearEvent;

    @FXML private TableView<Event> taulaEvents;
    @FXML private TableColumn<Event, String> colTitol;
    @FXML private TableColumn<Event, String> colCategoria;
    @FXML private TableColumn<Event, String> colUbicacio;
    @FXML private TableColumn<Event, String> colData;
    @FXML private TableColumn<Event, String> colHora;
    @FXML private TableColumn<Event, String> colPlaces;

    private final ObservableList<Event> events = FXCollections.observableArrayList();
    private final DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private VistaActual vistaActual = VistaActual.TOTS;

    /**
     * Inicialitza la pantalla principal.
     */
    @FXML
    public void initialize() {
        configurarTaula();
        configurarFiltresAutomatics();
        carregarEvents();
        actualitzarBotonsVista();
    }

    private void configurarTaula() {
        colTitol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitol()));
        colCategoria.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategoria().toUpperCase()));
        colUbicacio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUbicacio()));
        colData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDataEvent().toString()));
        colHora.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHoraEvent().format(horaFormatter)));
        colPlaces.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPlacesDisponibles() + " / " + data.getValue().getAforament()));

        taulaEvents.setItems(events);
        taulaEvents.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                obrirEventSeleccionat();
            }
        });
    }

    private void configurarFiltresAutomatics() {
        txtUbicacio.textProperty().addListener((obs, anterior, actual) -> carregarEvents());
        dpData.valueProperty().addListener((obs, anterior, actual) -> carregarEvents());
        chkPlaces.selectedProperty().addListener((obs, anterior, actual) -> carregarEvents());
    }

    /**
     * Aplica els filtres seleccionats.
     */
    @FXML
    private void aplicarFiltres() {
        carregarEvents();
    }

    /**
     * Restableix els filtres.
     */
    @FXML
    private void netejarFiltres() {
        txtUbicacio.clear();
        dpData.setValue(null);
        chkPlaces.setSelected(false);
        carregarEvents();
    }

    /**
     * Mostra tots els esdeveniments.
     */
    @FXML
    private void mostrarTots() {
        vistaActual = VistaActual.TOTS;
        carregarEvents();
        actualitzarBotonsVista();
    }

    /**
     * Mostra els esdeveniments creats per l'usuari autenticat.
     */
    @FXML
    private void mostrarMeus() {
        vistaActual = VistaActual.MEUS;
        carregarEvents();
        actualitzarBotonsVista();
    }

    /**
     * Mostra els esdeveniments on l'usuari esta inscrit.
     */
    @FXML
    private void mostrarInscrits() {
        vistaActual = VistaActual.INSCRITS;
        carregarEvents();
        actualitzarBotonsVista();
    }

    /**
     * Obre el formulari per crear un esdeveniment nou.
     *
     * @throws IOException si no es pot carregar la pantalla
     */
    @FXML
    private void crearEvent() throws IOException {
        EventController.prepararNou();
        App.setRoot("event");
    }

    private void carregarEvents() {
        Usuari usuari = ServeiUsuari.getUsuariActual();
        Integer usuariId = usuari == null ? null : usuari.getId();
        Integer creadorId = vistaActual == VistaActual.MEUS ? usuariId : null;
        boolean inscrits = vistaActual == VistaActual.INSCRITS;

        events.setAll(EventDAO.llistarFiltrat(
                dpData.getValue(),
                txtUbicacio.getText(),
                chkPlaces.isSelected(),
                inscrits ? usuariId : creadorId,
                inscrits));
    }

    private void actualitzarBotonsVista() {
        btnTots.setDisable(vistaActual == VistaActual.TOTS);
        btnMeus.setDisable(vistaActual == VistaActual.MEUS);
        btnInscrits.setDisable(vistaActual == VistaActual.INSCRITS);
        btnCrearEvent.setVisible(vistaActual == VistaActual.MEUS);
        btnCrearEvent.setManaged(vistaActual == VistaActual.MEUS);
    }

    private void obrirEventSeleccionat() {
        Event event = taulaEvents.getSelectionModel().getSelectedItem();
        if (event == null) {
            return;
        }

        try {
            if (EventDAO.potModificar(event, ServeiUsuari.getUsuariActual())) {
                EventController.prepararEdicio(event.getId());
            } else {
                EventController.prepararConsulta(event.getId());
            }
            App.setRoot("event");
        } catch (IOException e) {
            mostrarAlerta("Error", "No s'ha pogut obrir l'esdeveniment.");
        }
    }

    private void mostrarAlerta(String titol, String missatge) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titol);
        alerta.setHeaderText(null);
        alerta.setContentText(missatge);
        alerta.showAndWait();
    }
}

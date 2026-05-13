package com.example.controller;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import com.example.App;
import com.example.dao.EventDAO;
import com.example.dao.InscripcioDAO;
import com.example.model.Event;
import com.example.model.Usuari;
import com.example.serveis.ServeiUsuari;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controlador de la pantalla de gestio, edicio i consulta d'esdeveniments.
 */
public class EventController {

    private enum Mode {
        NOU, EDICIO, CONSULTA
    }

    private static Mode modeInicial = Mode.NOU;
    private static Integer eventIdInicial;

    @FXML private Label lblTitolPantalla;
    @FXML private TextField txtTitol;
    @FXML private TextArea txtDescripcio;
    @FXML private TextField txtUbicacio;
    @FXML private DatePicker dpData;
    @FXML private TextField txtHora;
    @FXML private Spinner<Integer> spAforament;
    @FXML private Spinner<Integer> spPlaces;
    @FXML private ComboBox<String> cbCategoria;

    @FXML private Label lblCamp1;
    @FXML private Label lblCamp2;
    @FXML private Label lblCamp3;
    @FXML private TextField txtCamp1;
    @FXML private TextField txtCamp2;
    @FXML private TextField txtCamp3;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Button btnInscriure;

    private Mode mode;
    private Event eventActual;

    /**
     * Prepara el formulari per crear un esdeveniment.
     */
    public static void prepararNou() {
        modeInicial = Mode.NOU;
        eventIdInicial = null;
    }

    /**
     * Prepara el formulari per editar un esdeveniment.
     *
     * @param eventId identificador de l'esdeveniment
     */
    public static void prepararEdicio(int eventId) {
        modeInicial = Mode.EDICIO;
        eventIdInicial = eventId;
    }

    /**
     * Prepara el formulari per consultar un esdeveniment sense editar-lo.
     *
     * @param eventId identificador de l'esdeveniment
     */
    public static void prepararConsulta(int eventId) {
        modeInicial = Mode.CONSULTA;
        eventIdInicial = eventId;
    }

    /**
     * Inicialitza el formulari.
     */
    @FXML
    public void initialize() {
        mode = modeInicial;
        configurarControls();

        if (eventIdInicial != null) {
            eventActual = EventDAO.buscarPerId(eventIdInicial);
            carregarEvent(eventActual);
        }

        aplicarMode();
    }

    private void configurarControls() {
        spAforament.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 10));
        spPlaces.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 10));
        spAforament.setEditable(true);
        spPlaces.setEditable(true);

        cbCategoria.setItems(FXCollections.observableArrayList("Esport", "Videojoc", "Trobada"));
        cbCategoria.setValue("Trobada");
        cbCategoria.valueProperty().addListener((obs, anterior, nova) -> actualitzarCampsCategoria(true));
        actualitzarCampsCategoria(false);
    }

    /**
     * Guarda un esdeveniment nou o actualitza l'existent.
     */
    @FXML
    private void guardarEvent() {
        try {
            Event event = llegirFormulari();

            if (mode == Mode.NOU) {
                EventDAO.crear(event);
                mostrarAlerta("Exit", "Esdeveniment creat correctament.");
            } else if (mode == Mode.EDICIO) {
                event.setId(eventActual.getId());
                event.setCreadorId(eventActual.getCreadorId());
                EventDAO.actualitzar(event, ServeiUsuari.getUsuariActual());
                mostrarAlerta("Exit", "Esdeveniment actualitzat correctament.");
            }

            App.setRoot("primary");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    /**
     * Elimina l'esdeveniment actual.
     */
    @FXML
    private void eliminarEvent() {
        if (eventActual == null) {
            return;
        }

        try {
            EventDAO.eliminar(eventActual.getId(), ServeiUsuari.getUsuariActual());
            mostrarAlerta("Exit", "Esdeveniment eliminat correctament.");
            App.setRoot("primary");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    /**
     * Inscriu l'usuari autenticat a l'esdeveniment consultat.
     */
    @FXML
    private void inscriureEvent() {
        Usuari usuari = ServeiUsuari.getUsuariActual();
        if (usuari == null || eventActual == null) {
            return;
        }

        try {
            InscripcioDAO.inscriure(usuari.getId(), eventActual.getId());
            mostrarAlerta("Exit", "Inscripcio feta correctament.");
            App.setRoot("primary");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    /**
     * Torna a la pantalla principal.
     *
     * @throws IOException si no es pot carregar el FXML
     */
    @FXML
    private void sortir() throws IOException {
        App.setRoot("primary");
    }

    private void aplicarMode() {
        boolean consulta = mode == Mode.CONSULTA;
        boolean nou = mode == Mode.NOU;
        boolean potInscriure = potMostrarInscripcio();

        lblTitolPantalla.setText(consulta ? "Consulta d'esdeveniment" : "Gestio d'esdeveniment");
        btnGuardar.setVisible(!consulta);
        btnGuardar.setManaged(!consulta);
        btnEliminar.setVisible(mode == Mode.EDICIO);
        btnEliminar.setManaged(mode == Mode.EDICIO);
        btnInscriure.setVisible(potInscriure);
        btnInscriure.setManaged(potInscriure);
        activarFormulari(!consulta);

        if (nou) {
            btnEliminar.setVisible(false);
            btnEliminar.setManaged(false);
        }
    }

    private boolean potMostrarInscripcio() {
        Usuari usuari = ServeiUsuari.getUsuariActual();
        if (mode != Mode.CONSULTA || usuari == null || eventActual == null) {
            return false;
        }

        return eventActual.getCreadorId() != usuari.getId()
                && eventActual.getPlacesDisponibles() > 0
                && !InscripcioDAO.estaInscrit(usuari.getId(), eventActual.getId());
    }

    private void activarFormulari(boolean actiu) {
        txtTitol.setDisable(!actiu);
        txtDescripcio.setDisable(!actiu);
        txtUbicacio.setDisable(!actiu);
        dpData.setDisable(!actiu);
        txtHora.setDisable(!actiu);
        spAforament.setDisable(!actiu);
        spPlaces.setDisable(!actiu);
        cbCategoria.setDisable(!actiu);
        txtCamp1.setDisable(!actiu);
        txtCamp2.setDisable(!actiu);
        txtCamp3.setDisable(!actiu);
    }

    private void carregarEvent(Event event) {
        if (event == null) {
            return;
        }

        txtTitol.setText(valor(event.getTitol()));
        txtDescripcio.setText(valor(event.getDescripcio()));
        txtUbicacio.setText(valor(event.getUbicacio()));
        dpData.setValue(event.getDataEvent());
        txtHora.setText(event.getHoraEvent().toString());
        spAforament.getValueFactory().setValue(event.getAforament());
        spPlaces.getValueFactory().setValue(event.getPlacesDisponibles());
        cbCategoria.setValue(event.getCategoria());

        if ("Esport".equals(event.getCategoria())) {
            txtCamp1.setText(valor(event.getTipusEsport()));
            txtCamp2.setText(valor(event.getNivell()));
            txtCamp3.setText(valor(event.getMaterialNecessari()));
        } else if ("Videojoc".equals(event.getCategoria())) {
            txtCamp1.setText(valor(event.getJoc()));
            txtCamp2.setText(valor(event.getPlataforma()));
            txtCamp3.setText(valor(event.getModalitat()));
        } else {
            txtCamp1.setText(valor(event.getTema()));
            txtCamp2.setText(valor(event.getTipusTrobada()));
            txtCamp3.setText(event.getEdatMinima() == null ? "" : event.getEdatMinima().toString());
        }
    }

    private Event llegirFormulari() {
        Usuari usuariActual = ServeiUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new IllegalStateException("Cal iniciar sessio per gestionar esdeveniments.");
        }

        String titol = txtTitol.getText().trim();
        String ubicacio = txtUbicacio.getText().trim();
        String categoria = cbCategoria.getValue();

        if (titol.isEmpty() || ubicacio.isEmpty() || dpData.getValue() == null
                || txtHora.getText().trim().isEmpty() || categoria == null) {
            throw new IllegalArgumentException("Tots els camps obligatoris han d'estar plens.");
        }

        int aforament = spAforament.getValue();
        int places = spPlaces.getValue();
        if (places > aforament) {
            throw new IllegalArgumentException("Les places disponibles no poden superar l'aforament.");
        }

        LocalTime hora;
        try {
            hora = LocalTime.parse(txtHora.getText().trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("L'hora ha de tenir el format HH:MM.");
        }

        Event event = new Event();
        event.setTitol(titol);
        event.setDescripcio(txtDescripcio.getText().trim());
        event.setUbicacio(ubicacio);
        event.setDataEvent(dpData.getValue());
        event.setHoraEvent(hora);
        event.setAforament(aforament);
        event.setPlacesDisponibles(places);
        event.setCategoria(categoria);
        event.setCreadorId(usuariActual.getId());
        assignarCampsCategoria(event);

        return event;
    }

    private void assignarCampsCategoria(Event event) {
        String camp1 = txtCamp1.getText().trim();
        String camp2 = txtCamp2.getText().trim();
        String camp3 = txtCamp3.getText().trim();

        if ("Esport".equals(event.getCategoria())) {
            if (camp1.isEmpty() || camp2.isEmpty()) {
                throw new IllegalArgumentException("Tipus d'esport i nivell son obligatoris.");
            }
            event.setTipusEsport(camp1);
            event.setNivell(camp2);
            event.setMaterialNecessari(camp3);
        } else if ("Videojoc".equals(event.getCategoria())) {
            if (camp1.isEmpty() || camp2.isEmpty()) {
                throw new IllegalArgumentException("Joc i plataforma son obligatoris.");
            }
            event.setJoc(camp1);
            event.setPlataforma(camp2);
            event.setModalitat(camp3);
        } else {
            if (camp1.isEmpty() || camp2.isEmpty()) {
                throw new IllegalArgumentException("Tema i tipus de trobada son obligatoris.");
            }
            event.setTema(camp1);
            event.setTipusTrobada(camp2);

            if (!camp3.isEmpty()) {
                int edat;
                try {
                    edat = Integer.parseInt(camp3);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("L'edat minima ha de ser un numero.");
                }

                if (edat < 0) {
                    throw new IllegalArgumentException("L'edat minima no pot ser negativa.");
                }
                event.setEdatMinima(edat);
            }
        }
    }

    private void actualitzarCampsCategoria(boolean netejar) {
        String categoria = cbCategoria.getValue();
        if (netejar) {
            txtCamp1.clear();
            txtCamp2.clear();
            txtCamp3.clear();
        }

        if ("Esport".equals(categoria)) {
            lblCamp1.setText("Tipus esport (*):");
            lblCamp2.setText("Nivell (*):");
            lblCamp3.setText("Material necessari:");
        } else if ("Videojoc".equals(categoria)) {
            lblCamp1.setText("Joc (*):");
            lblCamp2.setText("Plataforma (*):");
            lblCamp3.setText("Modalitat:");
        } else {
            lblCamp1.setText("Tema (*):");
            lblCamp2.setText("Tipus trobada (*):");
            lblCamp3.setText("Edat minima:");
        }
    }

    private String valor(String valor) {
        return valor == null ? "" : valor;
    }

    private void mostrarAlerta(String titol, String missatge) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titol);
        alerta.setHeaderText(null);
        alerta.setContentText(missatge);
        alerta.showAndWait();
    }
}

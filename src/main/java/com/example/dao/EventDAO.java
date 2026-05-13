package com.example.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.db.DBConnection;
import com.example.model.Event;
import com.example.model.Usuari;

/**
 * DAO responsable de les operacions de persistencia dels esdeveniments.
 */
public class EventDAO {

    /**
     * Retorna tots els esdeveniments, juntament amb el nom d'usuari del creador.
     *
     * @return llista d'esdeveniments
     */
    public static List<Event> llistar() {
        return llistarFiltrat(null, null, false, null, false);
    }

    /**
     * Retorna esdeveniments aplicant filtres combinables.
     *
     * @param data data exacta de l'esdeveniment
     * @param ubicacio text de ciutat o ubicacio
     * @param nomesAmbPlaces true si nomes es volen events amb places disponibles
     * @param creadorId creador concret, o null per no filtrar per creador
     * @param nomesInscrit true per veure nomes inscripcions de l'usuari indicat
     * @return llista d'esdeveniments filtrada
     */
    public static List<Event> llistarFiltrat(LocalDate data, String ubicacio, boolean nomesAmbPlaces, Integer creadorId, boolean nomesInscrit) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT e.*, u.usuari AS creador_usuari ");
        sql.append("FROM events e INNER JOIN usuaris u ON e.creador_id = u.id ");

        if (nomesInscrit) {
            sql.append("INNER JOIN inscripcions i ON i.event_id = e.id ");
        }

        sql.append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();

        if (data != null) {
            sql.append("AND e.data_event = ? ");
            params.add(Date.valueOf(data));
        }

        if (ubicacio != null && !ubicacio.trim().isEmpty()) {
            sql.append("AND LOWER(e.ubicacio) LIKE ? ");
            params.add("%" + ubicacio.trim().toLowerCase() + "%");
        }

        if (nomesAmbPlaces) {
            sql.append("AND e.places_disponibles > 0 ");
        }

        if (creadorId != null) {
            if (nomesInscrit) {
                sql.append("AND i.usuari_id = ? ");
            } else {
                sql.append("AND e.creador_id = ? ");
            }
            params.add(creadorId);
        }

        sql.append("ORDER BY e.data_event, e.hora_event, e.id");
        return executarConsulta(sql.toString(), params);
    }

    /**
     * Crea un esdeveniment nou.
     *
     * @param event dades de l'esdeveniment
     * @return true si s'ha inserit correctament
     */
    public static boolean crear(Event event) {
        String sql = "INSERT INTO events ("
                + "titol, descripcio, ubicacio, data_event, hora_event, aforament, places_disponibles, categoria, creador_id, "
                + "tipus_esport, nivell, material_necessari, joc, plataforma, modalitat, tema, tipus_trobada, edat_minima"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            omplirStatement(ps, event, false);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No s'ha pogut crear l'esdeveniment.", e);
        }
    }

    /**
     * Actualitza un esdeveniment si l'usuari te permisos.
     *
     * @param event dades actualitzades
     * @param usuari usuari autenticat
     * @return true si s'ha actualitzat
     */
    public static boolean actualitzar(Event event, Usuari usuari) {
        validarPermis(event.getId(), usuari);

        String sql = "UPDATE events SET "
                + "titol = ?, descripcio = ?, ubicacio = ?, data_event = ?, hora_event = ?, "
                + "aforament = ?, places_disponibles = ?, categoria = ?, creador_id = ?, "
                + "tipus_esport = ?, nivell = ?, material_necessari = ?, "
                + "joc = ?, plataforma = ?, modalitat = ?, "
                + "tema = ?, tipus_trobada = ?, edat_minima = ? "
                + "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            omplirStatement(ps, event, true);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No s'ha pogut actualitzar l'esdeveniment.", e);
        }
    }

    /**
     * Elimina un esdeveniment si l'usuari te permisos.
     *
     * @param eventId identificador de l'esdeveniment
     * @param usuari usuari autenticat
     * @return true si s'ha eliminat
     */
    public static boolean eliminar(int eventId, Usuari usuari) {
        validarPermis(eventId, usuari);

        String sql = "DELETE FROM events WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No s'ha pogut eliminar l'esdeveniment.", e);
        }
    }

    /**
     * Comprova si un usuari pot editar o eliminar un esdeveniment.
     *
     * @param event esdeveniment seleccionat
     * @param usuari usuari autenticat
     * @return true si pot modificar-lo
     */
    public static boolean potModificar(Event event, Usuari usuari) {
        if (event == null || usuari == null) {
            return false;
        }

        return "ADMIN".equals(usuari.getRol()) || event.getCreadorId() == usuari.getId();
    }

    private static void validarPermis(int eventId, Usuari usuari) {
        Event event = buscarPerId(eventId);
        if (!potModificar(event, usuari)) {
            throw new SecurityException("No tens permisos per modificar aquest esdeveniment.");
        }
    }

    public static Event buscarPerId(int eventId) {
        String sql = "SELECT e.*, u.usuari AS creador_usuari "
                + "FROM events e INNER JOIN usuaris u ON e.creador_id = u.id "
                + "WHERE e.id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapEvent(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("No s'ha pogut consultar l'esdeveniment.", e);
        }

        return null;
    }

    private static List<Event> executarConsulta(String sql, List<Object> params) {
        List<Event> events = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(mapEvent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("No s'han pogut carregar els esdeveniments.", e);
        }

        return events;
    }

    private static void omplirStatement(PreparedStatement ps, Event event, boolean inclouIdFinal) throws SQLException {
        ps.setString(1, event.getTitol());
        ps.setString(2, buidANull(event.getDescripcio()));
        ps.setString(3, event.getUbicacio());
        ps.setDate(4, Date.valueOf(event.getDataEvent()));
        ps.setTime(5, Time.valueOf(event.getHoraEvent()));
        ps.setInt(6, event.getAforament());
        ps.setInt(7, event.getPlacesDisponibles());
        ps.setString(8, event.getCategoria());
        ps.setInt(9, event.getCreadorId());
        ps.setString(10, buidANull(event.getTipusEsport()));
        ps.setString(11, buidANull(event.getNivell()));
        ps.setString(12, buidANull(event.getMaterialNecessari()));
        ps.setString(13, buidANull(event.getJoc()));
        ps.setString(14, buidANull(event.getPlataforma()));
        ps.setString(15, buidANull(event.getModalitat()));
        ps.setString(16, buidANull(event.getTema()));
        ps.setString(17, buidANull(event.getTipusTrobada()));

        if (event.getEdatMinima() == null) {
            ps.setObject(18, null);
        } else {
            ps.setInt(18, event.getEdatMinima());
        }

        if (inclouIdFinal) {
            ps.setInt(19, event.getId());
        }
    }

    private static String buidANull(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        return valor.trim();
    }

    private static Event mapEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setTitol(rs.getString("titol"));
        event.setDescripcio(rs.getString("descripcio"));
        event.setUbicacio(rs.getString("ubicacio"));
        event.setDataEvent(rs.getDate("data_event").toLocalDate());
        event.setHoraEvent(rs.getTime("hora_event").toLocalTime());
        event.setAforament(rs.getInt("aforament"));
        event.setPlacesDisponibles(rs.getInt("places_disponibles"));
        event.setCategoria(rs.getString("categoria"));
        event.setCreadorId(rs.getInt("creador_id"));
        event.setCreadorUsuari(rs.getString("creador_usuari"));
        event.setTipusEsport(rs.getString("tipus_esport"));
        event.setNivell(rs.getString("nivell"));
        event.setMaterialNecessari(rs.getString("material_necessari"));
        event.setJoc(rs.getString("joc"));
        event.setPlataforma(rs.getString("plataforma"));
        event.setModalitat(rs.getString("modalitat"));
        event.setTema(rs.getString("tema"));
        event.setTipusTrobada(rs.getString("tipus_trobada"));

        int edatMinima = rs.getInt("edat_minima");
        event.setEdatMinima(rs.wasNull() ? null : edatMinima);

        return event;
    }
}

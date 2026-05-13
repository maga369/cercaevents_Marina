package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.db.DBConnection;

/**
 * DAO responsable de les inscripcions d'usuaris a esdeveniments.
 */
public class InscripcioDAO {

    /**
     * Inscriu un usuari a un esdeveniment.
     *
     * @param usuariId identificador de l'usuari
     * @param eventId identificador de l'esdeveniment
     * @return true si s'ha creat la inscripcio
     */
    public static boolean inscriure(int usuariId, int eventId) {
        if (estaInscrit(usuariId, eventId)) {
            throw new IllegalArgumentException("Ja estas inscrit a aquest esdeveniment.");
        }

        String updatePlacesSql = "UPDATE events SET places_disponibles = places_disponibles - 1 "
                + "WHERE id = ? AND places_disponibles > 0";
        String insertSql = "INSERT INTO inscripcions (usuari_id, event_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement updatePlaces = conn.prepareStatement(updatePlacesSql);
                PreparedStatement insert = conn.prepareStatement(insertSql)) {
                updatePlaces.setInt(1, eventId);
                if (updatePlaces.executeUpdate() == 0) {
                    conn.rollback();
                    throw new IllegalArgumentException("No queden places disponibles.");
                }

                insert.setInt(1, usuariId);
                insert.setInt(2, eventId);
                boolean inscrit = insert.executeUpdate() > 0;
                conn.commit();
                return inscrit;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } catch (RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException("No s'ha pogut fer la inscripcio.", e);
        }
    }

    /**
     * Comprova si un usuari ja esta inscrit a un esdeveniment.
     *
     * @param usuariId identificador de l'usuari
     * @param eventId identificador de l'esdeveniment
     * @return true si ja esta inscrit
     */
    public static boolean estaInscrit(int usuariId, int eventId) {
        String sql = "SELECT COUNT(*) FROM inscripcions WHERE usuari_id = ? AND event_id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuariId);
            ps.setInt(2, eventId);

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No s'ha pogut consultar la inscripcio.", e);
        }
    }
}

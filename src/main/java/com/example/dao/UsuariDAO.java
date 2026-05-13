package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import com.example.db.DBConnection;
import com.example.model.Usuari;

/**
 * DAO responsable de les operacions de persistencia dels usuaris.
 */
public class UsuariDAO {

    /**
     * Registra un nou usuari a la base de dades.
     *
     * @param usuari dades de l'usuari que es vol registrar
     * @return true si la insercio s'ha completat correctament
     */
    public static boolean registrar(Usuari usuari) {
        if (existeixUsuari(usuari.getUsuari())) {
            throw new IllegalArgumentException("El nom d'usuari ja existeix.");
        }

        if (existeixEmail(usuari.getEmail())) {
            throw new IllegalArgumentException("El correu electronic ja existeix.");
        }

        String sql = "INSERT INTO usuaris (usuari, nom, cognoms, email, password_hash, rol) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuari.getUsuari());
            ps.setString(2, usuari.getNom());
            ps.setString(3, usuari.getCognoms());
            ps.setString(4, usuari.getEmail());
            ps.setString(5, usuari.getPasswordHash());
            ps.setString(6, usuari.getRol() == null ? "USER" : usuari.getRol());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("No s'ha pogut registrar l'usuari.", e);
        }
    }

    /**
     * Comprova si ja existeix un nom d'usuari.
     *
     * @param usuari nom d'usuari que es vol comprovar
     * @return true si existeix
     */
    public static boolean existeixUsuari(String usuari) {
        return existeixCamp("usuari", usuari);
    }

    /**
     * Comprova si ja existeix un correu electronic.
     *
     * @param email correu electronic que es vol comprovar
     * @return true si existeix
     */
    public static boolean existeixEmail(String email) {
        return existeixCamp("email", email);
    }

    private static boolean existeixCamp(String camp, String valor) {
        String sql = "SELECT COUNT(*) FROM usuaris WHERE " + camp + " = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, valor);

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("No s'ha pogut comprovar si l'usuari existeix.", e);
        }
    }

    /**
     * Cerca un usuari pel seu nom d'usuari o email i valida la contrasenya amb BCrypt.
     *
     * @param usuari nom d'usuari o correu electronic
     * @param password contrasenya en text pla introduida al login
     * @return usuari autenticat o null si no existeix o la contrasenya no coincideix
     */
    public static Usuari buscarUsuari(String usuari, String password) {
        String sql = "SELECT * FROM usuaris WHERE usuari = ? OR email = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuari);
            ps.setString(2, usuari);

            ResultSet rs = ps.executeQuery();

            if (rs.next() && BCrypt.checkpw(password, rs.getString("password_hash"))) {
                Usuari u = new Usuari();
                u.setId(rs.getInt("id"));
                u.setUsuari(rs.getString("usuari"));
                u.setNom(rs.getString("nom"));
                u.setCognoms(rs.getString("cognoms"));
                u.setEmail(rs.getString("email"));
                u.setRol(rs.getString("rol"));
                u.setDataRegistre(rs.getTimestamp("data_registre"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

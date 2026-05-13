package com.example.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Inicialitza la base de dades de l'aplicacio.
 */
public class DatabaseInitializer {

    private static final String ADMIN_PASSWORD_HASH =
            "$2a$12$UNN2OibGmqXLrpAkVXPp7uV4tQHhLwkWzDk/qJzulCR3jnSlXpae2";

    /**
     * Executa el fitxer schema.sql complet. Aquest metode recrea les taules i dades
     * d'exemple, per tant s'ha d'usar nomes quan es vulgui reiniciar la base de dades.
     *
     * @throws RuntimeException si no es pot llegir o executar l'script
     */
    public static void init() {
        try (
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            InputStream is = DatabaseInitializer.class.getResourceAsStream("schema.sql")
        ) {
            if (is == null) {
                throw new RuntimeException("No s'ha trobat schema.sql");
            }

            String sql = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            for (String part : sql.split(";")) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                    System.out.println("Executat: " + trimmed);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error inicialitzant la base de dades", e);
        }
    }

    /**
     * Crea la taula d'usuaris si no existeix i garanteix que hi hagi l'admin inicial.
     */
    public static void initDadesEssencials() {
        crearTaulaUsuarisSiCal();
        crearTaulaEventsSiCal();
        crearTaulaInscripcionsSiCal();
        crearAdminSiCal();
    }

    /**
     * Mantingut per compatibilitat amb versions anteriors del projecte.
     */
    public static void initUsuarisEssencials() {
        initDadesEssencials();
    }

    private static void crearTaulaUsuarisSiCal() {
        String sql = "CREATE TABLE IF NOT EXISTS usuaris ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "usuari VARCHAR(20) NOT NULL UNIQUE,"
                + "nom VARCHAR(100) NOT NULL,"
                + "cognoms VARCHAR(150),"
                + "email VARCHAR(150) NOT NULL UNIQUE,"
                + "password_hash VARCHAR(255) NOT NULL,"
                + "data_registre TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "rol VARCHAR(12) DEFAULT 'USER',"
                + "CONSTRAINT chk_usuaris_rol CHECK (rol IN ('ADMIN', 'USER'))"
                + ")";

        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException("Error creant la taula d'usuaris", e);
        }
    }

    private static void crearAdminSiCal() {
        String selectSql = "SELECT COUNT(*) FROM usuaris WHERE usuari = ?";
        String insertSql = "INSERT INTO usuaris (usuari, nom, cognoms, email, password_hash, rol) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement select = conn.prepareStatement(selectSql)) {
            select.setString(1, "admin");

            ResultSet rs = select.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }

            try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                insert.setString(1, "admin");
                insert.setString(2, "Administrador");
                insert.setString(3, "Sistema");
                insert.setString(4, "admin@cercaevent.cat");
                insert.setString(5, ADMIN_PASSWORD_HASH);
                insert.setString(6, "ADMIN");
                insert.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creant l'usuari administrador inicial", e);
        }
    }

    private static void crearTaulaEventsSiCal() {
        String sql = "CREATE TABLE IF NOT EXISTS events ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "titol VARCHAR(200) NOT NULL,"
                + "descripcio CLOB,"
                + "ubicacio VARCHAR(150) NOT NULL,"
                + "data_event DATE NOT NULL,"
                + "hora_event TIME NOT NULL,"
                + "aforament INT NOT NULL,"
                + "places_disponibles INT NOT NULL,"
                + "categoria VARCHAR(20) NOT NULL,"
                + "creador_id INT NOT NULL,"
                + "tipus_esport VARCHAR(100),"
                + "nivell VARCHAR(50),"
                + "material_necessari VARCHAR(255),"
                + "joc VARCHAR(100),"
                + "plataforma VARCHAR(100),"
                + "modalitat VARCHAR(100),"
                + "tema VARCHAR(100),"
                + "tipus_trobada VARCHAR(100),"
                + "edat_minima INT,"
                + "CONSTRAINT fk_events_creador FOREIGN KEY (creador_id) REFERENCES usuaris(id) ON DELETE CASCADE,"
                + "CONSTRAINT chk_events_categoria CHECK (categoria IN ('Esport', 'Videojoc', 'Trobada')),"
                + "CONSTRAINT chk_events_aforament CHECK (aforament >= 0),"
                + "CONSTRAINT chk_events_places CHECK (places_disponibles >= 0 AND places_disponibles <= aforament),"
                + "CONSTRAINT chk_events_edat_minima CHECK (edat_minima IS NULL OR edat_minima >= 0)"
                + ")";

        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_events_categoria ON events(categoria)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_events_data_event ON events(data_event)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_events_creador_id ON events(creador_id)");
        } catch (Exception e) {
            throw new RuntimeException("Error creant la taula d'esdeveniments", e);
        }
    }

    private static void crearTaulaInscripcionsSiCal() {
        String sql = "CREATE TABLE IF NOT EXISTS inscripcions ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "usuari_id INT NOT NULL,"
                + "event_id INT NOT NULL,"
                + "data_inscripcio TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,"
                + "CONSTRAINT fk_inscripcions_usuari FOREIGN KEY (usuari_id) REFERENCES usuaris(id) ON DELETE CASCADE,"
                + "CONSTRAINT fk_inscripcions_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,"
                + "CONSTRAINT uq_inscripcio_unique UNIQUE (usuari_id, event_id)"
                + ")";

        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_inscripcions_usuari_id ON inscripcions(usuari_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_inscripcions_event_id ON inscripcions(event_id)");
        } catch (Exception e) {
            throw new RuntimeException("Error creant la taula d'inscripcions", e);
        }
    }
}

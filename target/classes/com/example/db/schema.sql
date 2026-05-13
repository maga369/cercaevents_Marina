-- schema.sql
-- Projecte CercaEvent
-- Base de dades H2
-- Les contrasenyes d'exemple es guarden amb BCrypt.
-- La contrasenya en text pla només apareix en comentaris per facilitar les proves.

DROP TABLE IF EXISTS inscripcions;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS usuaris;

--------------------------------------------------
-- TAULA USUARIS
--------------------------------------------------
CREATE TABLE usuaris (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuari VARCHAR(20) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    cognoms VARCHAR(150),
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    data_registre TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    rol VARCHAR(12) DEFAULT 'USER',
    CONSTRAINT chk_usuaris_rol CHECK (rol IN ('ADMIN', 'USER'))
);

--------------------------------------------------
-- TAULA EVENTS
--------------------------------------------------
CREATE TABLE events (
    id INT AUTO_INCREMENT PRIMARY KEY,

    -- Camps comuns
    titol VARCHAR(200) NOT NULL,
    descripcio CLOB,
    ubicacio VARCHAR(150) NOT NULL,
    data_event DATE NOT NULL,
    hora_event TIME NOT NULL,
    aforament INT NOT NULL,
    places_disponibles INT NOT NULL,
    categoria VARCHAR(20) NOT NULL,
    creador_id INT NOT NULL,

    -- Camps específics Esport
    tipus_esport VARCHAR(100),
    nivell VARCHAR(50),
    material_necessari VARCHAR(255),

    -- Camps específics Videojoc
    joc VARCHAR(100),
    plataforma VARCHAR(100),
    modalitat VARCHAR(100),

    -- Camps específics Trobada
    tema VARCHAR(100),
    tipus_trobada VARCHAR(100),
    edat_minima INT,

    CONSTRAINT fk_events_creador
        FOREIGN KEY (creador_id)
        REFERENCES usuaris(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_events_categoria
        CHECK (categoria IN ('Esport', 'Videojoc', 'Trobada')),

    CONSTRAINT chk_events_aforament
        CHECK (aforament >= 0),

    CONSTRAINT chk_events_places
        CHECK (places_disponibles >= 0 AND places_disponibles <= aforament),

    CONSTRAINT chk_events_edat_minima
        CHECK (edat_minima IS NULL OR edat_minima >= 0)
);

--------------------------------------------------
-- TAULA INSCRIPCIONS
--------------------------------------------------
CREATE TABLE inscripcions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuari_id INT NOT NULL,
    event_id INT NOT NULL,
    data_inscripcio TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_inscripcions_usuari
        FOREIGN KEY (usuari_id)
        REFERENCES usuaris(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_inscripcions_event
        FOREIGN KEY (event_id)
        REFERENCES events(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_inscripcio_unique
        UNIQUE (usuari_id, event_id)
);

--------------------------------------------------
-- ÍNDEXS
--------------------------------------------------
CREATE INDEX idx_usuaris_usuari ON usuaris(usuari);
CREATE INDEX idx_usuaris_email ON usuaris(email);
CREATE INDEX idx_events_categoria ON events(categoria);
CREATE INDEX idx_events_data_event ON events(data_event);
CREATE INDEX idx_events_creador_id ON events(creador_id);
CREATE INDEX idx_inscripcions_usuari_id ON inscripcions(usuari_id);
CREATE INDEX idx_inscripcions_event_id ON inscripcions(event_id);

--------------------------------------------------
-- DADES D'EXEMPLE: USUARIS
--------------------------------------------------
-- password en pla: admin
INSERT INTO usuaris (usuari, nom, cognoms, email, password_hash, rol) VALUES
('admin', 'Administrador', 'Sistema', 'admin@cercaevent.cat', '$2a$12$UNN2OibGmqXLrpAkVXPp7uV4tQHhLwkWzDk/qJzulCR3jnSlXpae2', 'ADMIN');

-- password en pla: marta123
INSERT INTO usuaris (usuari, nom, cognoms, email, password_hash, rol) VALUES
('marta', 'Marta', 'Soler', 'marta@cercaevent.cat', '$2a$12$lGU5VhvnPx1EbeYiXkmqMOZ6AMtZA8xa4cnJn5Xz8CdzO31jq0T7y', 'USER');

-- password en pla: joan123
INSERT INTO usuaris (usuari, nom, cognoms, email, password_hash, rol) VALUES
('joan', 'Joan', 'Serra', 'joan@cercaevent.cat', '$2a$12$KIbBXqJLQrlhbJzA1xflUOOq09jlnLfualH0ldC49la3CKHY0Qy.2', 'USER');

-- password en pla: laura123
INSERT INTO usuaris (usuari, nom, cognoms, email, password_hash, rol) VALUES
('laura', 'Laura', 'Casas', 'laura@cercaevent.cat', '$2a$12$dGsw79qGtICdlzZggMMGxeWNKPfSVUtleKlEHNVgD3XSKGd5GVoIq', 'USER');

-- password en pla: pau123
INSERT INTO usuaris (usuari, nom, cognoms, email, password_hash, rol) VALUES
('pau', 'Pau', 'Ribas', 'pau@cercaevent.cat', '$2a$12$rT9xQiM3SOOmwB1Wmx9gruJ4VJ5OOdppvm7DeR00Ek6.5Om1q3X1W', 'USER');

-- password en pla: anna123
INSERT INTO usuaris (usuari, nom, cognoms, email, password_hash, rol) VALUES
('anna', 'Anna', 'Vila', 'anna@cercaevent.cat', '$2a$12$tqp6lhqlgb9alUEz0gmFRuVNWUbZPc0knxZ6.hNbrWQ4FhplzqHQ2', 'USER');


--------------------------------------------------
-- DADES D'EXEMPLE: EVENTS
--------------------------------------------------

-- ESPORT
INSERT INTO events (
    titol, descripcio, ubicacio, data_event, hora_event,
    aforament, places_disponibles, categoria, creador_id,
    tipus_esport, nivell, material_necessari
) VALUES
(
    'Partit de futbol 7',
    'Partit amistós entre jugadors amateurs.',
    'Girona',
    DATE '2026-05-10',
    TIME '18:30:00',
    14, 12, 'Esport', 2,
    'Futbol', 'Intermedi', 'Botes i canyelleres'
),
(
    'Sessió de running al parc',
    'Entrenament suau en grup per a tots els nivells.',
    'Barcelona',
    DATE '2026-05-12',
    TIME '07:30:00',
    20, 20, 'Esport', 3,
    'Running', 'Tots els nivells', 'Aigua i sabatilles'
),
(
    'Torneig de pàdel',
    'Torneig per parelles amb fase de grups.',
    'Tarragona',
    DATE '2026-05-18',
    TIME '10:00:00',
    16, 13, 'Esport', 4,
    'Pàdel', 'Avançat', 'Pala i roba esportiva'
);

-- VIDEOJOC
INSERT INTO events (
    titol, descripcio, ubicacio, data_event, hora_event,
    aforament, places_disponibles, categoria, creador_id,
    joc, plataforma, modalitat
) VALUES
(
    'Torneig d''EA Sports FC',
    'Competició eliminatòria 1vs1 oberta a tothom.',
    'Lleida',
    DATE '2026-05-20',
    TIME '17:00:00',
    16, 13, 'Videojoc', 3,
    'EA Sports FC 26', 'PlayStation 5', '1vs1'
),
(
    'Nit de Mario Kart',
    'Partides amistoses i classificació final.',
    'Reus',
    DATE '2026-05-22',
    TIME '20:00:00',
    12, 10, 'Videojoc', 5,
    'Mario Kart 8 Deluxe', 'Nintendo Switch', 'Multijugador local'
),
(
    'Customs de League of Legends',
    'Partides 5vs5 entre equips creats a l''instant.',
    'Sabadell',
    DATE '2026-05-24',
    TIME '18:00:00',
    10, 9, 'Videojoc', 2,
    'League of Legends', 'PC', '5vs5'
);

-- TROBADA
INSERT INTO events (
    titol, descripcio, ubicacio, data_event, hora_event,
    aforament, places_disponibles, categoria, creador_id,
    tema, tipus_trobada, edat_minima
) VALUES
(
    'Club de lectura',
    'Debat sobre literatura contemporània.',
    'Girona',
    DATE '2026-05-14',
    TIME '18:00:00',
    25, 22, 'Trobada', 4,
    'Literatura', 'Cultural', 16
),
(
    'Meetup de programació Java',
    'Sessió per compartir dubtes i projectes de JavaFX.',
    'Barcelona',
    DATE '2026-05-25',
    TIME '19:30:00',
    30, 26, 'Trobada', 1,
    'Programació', 'Tecnològica', 18
),
(
    'Intercanvi d''idiomes',
    'Trobada social per practicar anglès i francès.',
    'Manresa',
    DATE '2026-05-28',
    TIME '18:30:00',
    20, 20, 'Trobada', 6,
    'Idiomes', 'Social', 14
);

--------------------------------------------------
-- DADES D'EXEMPLE: INSCRIPCIONS
--------------------------------------------------

-- Event 1 -> 2 inscrits (14 - 2 = 12)
INSERT INTO inscripcions (usuari_id, event_id, data_inscripcio) VALUES
(3, 1, TIMESTAMP '2026-04-20 10:00:00'),
(5, 1, TIMESTAMP '2026-04-20 10:30:00');

-- Event 3 -> 3 inscrits (16 - 3 = 13)
INSERT INTO inscripcions (usuari_id, event_id, data_inscripcio) VALUES
(2, 3, TIMESTAMP '2026-04-21 09:15:00'),
(3, 3, TIMESTAMP '2026-04-21 09:45:00'),
(6, 3, TIMESTAMP '2026-04-21 10:10:00');

-- Event 4 -> 3 inscrits (16 - 3 = 13)
INSERT INTO inscripcions (usuari_id, event_id, data_inscripcio) VALUES
(2, 4, TIMESTAMP '2026-04-22 16:00:00'),
(4, 4, TIMESTAMP '2026-04-22 16:05:00'),
(6, 4, TIMESTAMP '2026-04-22 16:10:00');

-- Event 5 -> 2 inscrits (12 - 2 = 10)
INSERT INTO inscripcions (usuari_id, event_id, data_inscripcio) VALUES
(2, 5, TIMESTAMP '2026-04-23 18:00:00'),
(3, 5, TIMESTAMP '2026-04-23 18:20:00');

-- Event 6 -> 1 inscrit (10 - 1 = 9)
INSERT INTO inscripcions (usuari_id, event_id, data_inscripcio) VALUES
(5, 6, TIMESTAMP '2026-04-24 17:40:00');

-- Event 7 -> 3 inscrits (25 - 3 = 22)
INSERT INTO inscripcions (usuari_id, event_id, data_inscripcio) VALUES
(2, 7, TIMESTAMP '2026-04-25 09:00:00'),
(3, 7, TIMESTAMP '2026-04-25 09:05:00'),
(5, 7, TIMESTAMP '2026-04-25 09:10:00');

-- Event 8 -> 4 inscrits (30 - 4 = 26)
INSERT INTO inscripcions (usuari_id, event_id, data_inscripcio) VALUES
(2, 8, TIMESTAMP '2026-04-26 19:00:00'),
(3, 8, TIMESTAMP '2026-04-26 19:02:00'),
(4, 8, TIMESTAMP '2026-04-26 19:04:00'),
(6, 8, TIMESTAMP '2026-04-26 19:06:00');
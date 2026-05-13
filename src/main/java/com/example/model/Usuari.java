package com.example.model;

import java.sql.Timestamp;

/**
 * Model que representa un usuari registrat a CercaEvent.
 */
public class Usuari {
    private int id;
    private String usuari;
    private String nom;
    private String cognoms;
    private String email;
    private String passwordHash;
    private Timestamp dataRegistre;
    private String rol;

    /**
     * Crea un usuari buit per omplir-lo amb dades de la base de dades o del formulari.
     */
    public Usuari() {}

    /**
     * Crea un usuari nou preparat per registrar-se a la base de dades.
     *
     * @param usuari nom d'usuari per iniciar sessio
     * @param nom nom real de l'usuari
     * @param cognoms cognoms de l'usuari
     * @param email correu electronic de contacte
     * @param passwordHash contrasenya ja encriptada
     * @param rol rol assignat a l'usuari
     */
    public Usuari(String usuari, String nom, String cognoms, String email, String passwordHash, String rol) {
        this.usuari = usuari;
        this.nom = nom;
        this.cognoms = cognoms;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    // Getters i Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsuari() { return usuari; }
    public void setUsuari(String usuari) { this.usuari = usuari; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCognoms() { return cognoms; }
    public void setCognoms(String cognoms) { this.cognoms = cognoms; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Timestamp getDataRegistre() { return dataRegistre; }
    public void setDataRegistre(Timestamp dataRegistre) { this.dataRegistre = dataRegistre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    /**
     * Retorna una representacio curta de l'usuari per depuracio.
     *
     * @return text amb el nom d'usuari i rol
     */
    @Override
    public String toString() {
        return "Usuari{" + "usuari='" + usuari + '\'' + ", rol='" + rol + '\'' + '}';
    }
}

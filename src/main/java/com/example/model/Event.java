package com.example.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Model que representa un esdeveniment de CercaEvent.
 */
public class Event {
    private int id;
    private String titol;
    private String descripcio;
    private String ubicacio;
    private LocalDate dataEvent;
    private LocalTime horaEvent;
    private int aforament;
    private int placesDisponibles;
    private String categoria;
    private int creadorId;
    private String creadorUsuari;

    private String tipusEsport;
    private String nivell;
    private String materialNecessari;

    private String joc;
    private String plataforma;
    private String modalitat;

    private String tema;
    private String tipusTrobada;
    private Integer edatMinima;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitol() { return titol; }
    public void setTitol(String titol) { this.titol = titol; }

    public String getDescripcio() { return descripcio; }
    public void setDescripcio(String descripcio) { this.descripcio = descripcio; }

    public String getUbicacio() { return ubicacio; }
    public void setUbicacio(String ubicacio) { this.ubicacio = ubicacio; }

    public LocalDate getDataEvent() { return dataEvent; }
    public void setDataEvent(LocalDate dataEvent) { this.dataEvent = dataEvent; }

    public LocalTime getHoraEvent() { return horaEvent; }
    public void setHoraEvent(LocalTime horaEvent) { this.horaEvent = horaEvent; }

    public int getAforament() { return aforament; }
    public void setAforament(int aforament) { this.aforament = aforament; }

    public int getPlacesDisponibles() { return placesDisponibles; }
    public void setPlacesDisponibles(int placesDisponibles) { this.placesDisponibles = placesDisponibles; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getCreadorId() { return creadorId; }
    public void setCreadorId(int creadorId) { this.creadorId = creadorId; }

    public String getCreadorUsuari() { return creadorUsuari; }
    public void setCreadorUsuari(String creadorUsuari) { this.creadorUsuari = creadorUsuari; }

    public String getTipusEsport() { return tipusEsport; }
    public void setTipusEsport(String tipusEsport) { this.tipusEsport = tipusEsport; }

    public String getNivell() { return nivell; }
    public void setNivell(String nivell) { this.nivell = nivell; }

    public String getMaterialNecessari() { return materialNecessari; }
    public void setMaterialNecessari(String materialNecessari) { this.materialNecessari = materialNecessari; }

    public String getJoc() { return joc; }
    public void setJoc(String joc) { this.joc = joc; }

    public String getPlataforma() { return plataforma; }
    public void setPlataforma(String plataforma) { this.plataforma = plataforma; }

    public String getModalitat() { return modalitat; }
    public void setModalitat(String modalitat) { this.modalitat = modalitat; }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    public String getTipusTrobada() { return tipusTrobada; }
    public void setTipusTrobada(String tipusTrobada) { this.tipusTrobada = tipusTrobada; }

    public Integer getEdatMinima() { return edatMinima; }
    public void setEdatMinima(Integer edatMinima) { this.edatMinima = edatMinima; }
}

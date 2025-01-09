package com.example.model;
import java.time.LocalDateTime;


public class Evenement {
    private Integer idEvent;
    private String nomEvent;
    private LocalDateTime dateEvent;
    private String description;
    private Integer idUser;

    // Constructeurs
    public Evenement() {}

    public Evenement(String nomEvent, LocalDateTime dateEvent, String description, Integer idUser) {
        this.nomEvent = nomEvent;
        this.dateEvent = dateEvent;
        this.description = description;
        this.idUser = idUser;
    }

    // Getters et setters
    public Integer getIdEvent() { return idEvent; }
    public void setIdEvent(Integer idEvent) { this.idEvent = idEvent; }
    public String getNomEvent() { return nomEvent; }
    public void setNomEvent(String nomEvent) { this.nomEvent = nomEvent; }
    public LocalDateTime getDateEvent() { return dateEvent; }
    public void setDateEvent(LocalDateTime dateEvent) { this.dateEvent = dateEvent; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getIdUser() { return idUser; }
    public void setIdUser(Integer idUser) { this.idUser = idUser; }

    

    @Override
    public String toString() {
        return  nomEvent;
    }
}
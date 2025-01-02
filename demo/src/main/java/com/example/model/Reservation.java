package com.example.model;
import java.time.LocalDateTime;

public class Reservation {
    private Integer id_reservation;
    private Integer id_user;
    private Integer id_event;
    private Integer id_salle;
    private Integer id_terrain;
    private LocalDateTime date_reservation;

    // Constructeurs
    public Reservation() {}

    public Reservation(Integer id_user, Integer id_event, Integer id_salle, 
                       Integer id_terrain, LocalDateTime date_reservation) {
        this.id_user = id_user;
        this.id_event = id_event;
        this.id_salle = id_salle;
        this.id_terrain = id_terrain;
        this.date_reservation = date_reservation;
    }

    // Getters et setters
    public Integer getId_reservation() { return id_reservation; }
    public void setId_reservation(Integer id_reservation) { this.id_reservation = id_reservation; }
    public Integer getId_user() { return id_user; }
    public void setId_user(Integer id_user) { this.id_user = id_user; }
    public Integer getId_event() { return id_event; }
    public void setId_event(Integer id_event) { this.id_event = id_event; }
    public Integer getId_salle() { return id_salle; }
    public void setId_salle(Integer id_salle) { this.id_salle = id_salle; }
    public Integer getId_terrain() { return id_terrain; }
    public void setId_terrain(Integer id_terrain) { this.id_terrain = id_terrain; }
    public LocalDateTime getDate_reservation() { return date_reservation; }
    public void setDate_reservation(LocalDateTime date_reservation) { this.date_reservation = date_reservation; }

    

    @Override
    public String toString() {
        return "reservation{" +
                "id_reservation=" + id_reservation +
                ", id_user=" + id_user +
                ", id_event=" + id_event +
                ", id_salle=" + id_salle +
                ", id_terrain=" + id_terrain +
                ", date_reservation=" + date_reservation +
                '}';
    }
}
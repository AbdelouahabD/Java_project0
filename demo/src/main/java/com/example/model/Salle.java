package com.example.model;


public class Salle {
    private Integer id_salle;
    private String nom_salle;
    private Integer capacite;

    // Constructeurs
    public Salle() {}

    public Salle(String nom_salle, Integer capacite) {
        this.nom_salle = nom_salle;
        this.capacite = capacite;
    }

    // Getters et setters
    public Integer getId_salle() { return id_salle; }
    public void setId_salle(Integer id_salle) { this.id_salle = id_salle; }
    public String getNom_salle() { return nom_salle; }
    public void setNom_salle(String nom_salle) { this.nom_salle = nom_salle; }
    public Integer getCapacite() { return capacite; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }

    
        @Override
        public String toString() {
            return nom_salle;
        }
    }
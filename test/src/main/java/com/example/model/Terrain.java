package com.example.model;


public class Terrain {
    private Integer id_terrain;
    private String nom_terrain;
    private String type;

    // Constructeurs
    public Terrain() {}

    public Terrain(String nom_terrain, String type) {
        this.nom_terrain = nom_terrain;
        this.type = type;
    }

    // Getters et setters
    public Integer getId_terrain() { return id_terrain; }
    public void setId_terrain(Integer id_terrain) { this.id_terrain = id_terrain; }
    public String getNom_terrain() { return nom_terrain; }
    public void setNom_terrain(String nom_terrain) { this.nom_terrain = nom_terrain; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    

    @Override
    public String toString() {
        return  nom_terrain;
    }
}
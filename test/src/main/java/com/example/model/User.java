package com.example.model;


public class User {
    private int idUser;
    private String nom;
    private String prenom;
    private String email;
    private String type;
    private String password;

    // Constructeurs
    public User() {}

    public User(String nom, String prenom, String email, String type,String password) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.type = type;
        this.password=password;
    }

    // Getters et setters
    public Integer getIdUser() { return idUser; }
    public void setIdUser(Integer idUser) { this.idUser = idUser; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    

    @Override
    public String toString() {
        return  nom;
    }
}
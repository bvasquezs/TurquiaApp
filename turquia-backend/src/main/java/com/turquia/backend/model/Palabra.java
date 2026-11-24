package com.turquia.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "palabras")
public class Palabra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String espanol;

    @Column(nullable = false)
    private String turco;

    private String categoria;

    // Constructores
    public Palabra() {}

    public Palabra(String espanol, String turco, String categoria) {
        this.espanol = espanol;
        this.turco = turco;
        this.categoria = categoria;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEspanol() { return espanol; }
    public void setEspanol(String espanol) { this.espanol = espanol; }

    public String getTurco() { return turco; }
    public void setTurco(String turco) { this.turco = turco; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
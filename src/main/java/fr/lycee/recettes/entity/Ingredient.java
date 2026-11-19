package fr.lycee.recettes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 255, message = "Le nom ne doit pas dépasser 255 caractères.")
    @Column(nullable = false, unique = true)
    private String nom;

    @Size(max = 255, message = "L’unité ne doit pas dépasser 255 caractères.")
    private String unite;

    public Ingredient() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }
}

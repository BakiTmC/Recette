package fr.lycee.recettes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Recette {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire.")
    @Size(max = 120, message = "Le titre ne doit pas dépasser 120 caractères.")
    @Column(nullable = false, length = 120)
    private String titre;

    @Column(columnDefinition = "text")
    private String description;

    @Min(value = 1, message = "Le temps doit être d’au moins 1 minute.")
    @Column(nullable = false)
    private int tempsPreparation = 1;

    @Min(value = 1, message = "La difficulté doit être comprise entre 1 et 3.")
    @Max(value = 3, message = "La difficulté doit être comprise entre 1 et 3.")
    @Column(nullable = false)
    private int difficulte = 1;

    @Min(value = 1, message = "Le nombre de personnes doit être au moins égal à 1.")
    @Column(nullable = false)
    private int nombrePersonnes = 4;

    @ManyToMany
    @JoinTable(name = "recette_ingredient",
        joinColumns = @JoinColumn(name = "recette_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"recette_id", "ingredient_id"}))
    private List<Ingredient> ingredients = new ArrayList<>();

    public Recette() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getTempsPreparation() { return tempsPreparation; }
    public void setTempsPreparation(int tempsPreparation) { this.tempsPreparation = tempsPreparation; }
    public int getDifficulte() { return difficulte; }
    public void setDifficulte(int difficulte) { this.difficulte = difficulte; }
    public int getNombrePersonnes() { return nombrePersonnes; }
    public void setNombrePersonnes(int nombrePersonnes) { this.nombrePersonnes = nombrePersonnes; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }
}

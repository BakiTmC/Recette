package fr.lycee.recettes.service;
import fr.lycee.recettes.entity.Ingredient;
import fr.lycee.recettes.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
@Service
@Transactional(readOnly = true)
public class IngredientService {
    private final IngredientRepository repository;
    public IngredientService(IngredientRepository repository) { this.repository = repository; }
    public List<Ingredient> listerTous() { return repository.findAllByOrderByNomAsc(); }
    public boolean nomExiste(String nom) {
        return nom != null && repository.existsByNomIgnoreCase(nom.trim());
    }
    public List<Ingredient> trouverTous(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();
        if (ids.stream().anyMatch(id -> id == null || id < 1)) {
            throw new IllegalArgumentException("La sélection contient un ingrédient invalide.");
        }
        List<Long> uniques = new ArrayList<>(new LinkedHashSet<>(ids));
        List<Ingredient> ingredients = repository.findAllById(uniques);
        if (ingredients.size() != uniques.size()) {
            throw new IllegalArgumentException("Un ingrédient sélectionné n’existe plus.");
        }
        return ingredients;
    }
    @Transactional
    public Ingredient enregistrer(Ingredient ingredient) {
        ingredient.setId(null);
        ingredient.setNom(ingredient.getNom().trim());
        if (ingredient.getUnite() != null) ingredient.setUnite(ingredient.getUnite().trim());
        return repository.saveAndFlush(ingredient);
    }
}

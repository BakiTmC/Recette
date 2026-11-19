package fr.lycee.recettes.service;
import fr.lycee.recettes.entity.Recette;
import fr.lycee.recettes.repository.RecetteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
@Service
@Transactional(readOnly = true)
public class RecetteService {
    private final RecetteRepository repository;
    private final IngredientService ingredientService;
    public RecetteService(RecetteRepository repository, IngredientService ingredientService) {
        this.repository = repository;
        this.ingredientService = ingredientService;
    }
    public List<Recette> rechercher(String q) {
        return q == null || q.isBlank() ? repository.findAllByOrderByTitreAsc()
            : repository.findByTitreContainingIgnoreCaseOrderByTitreAsc(q.trim());
    }
    public Recette trouverParId(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Recette introuvable"));
    }
    @Transactional
    public Recette enregistrer(Recette saisie, List<Long> ingredientIds) {
        // Recharger la recette existante évite de créer une ligne avec un faux id.
        Recette recette = saisie.getId() == null ? new Recette() : trouverParId(saisie.getId());
        recette.setTitre(saisie.getTitre().trim());
        recette.setDescription(saisie.getDescription());
        recette.setTempsPreparation(saisie.getTempsPreparation());
        recette.setDifficulte(saisie.getDifficulte());
        recette.setNombrePersonnes(saisie.getNombrePersonnes());
        recette.setIngredients(ingredientService.trouverTous(ingredientIds));
        return repository.save(recette);
    }
    @Transactional
    public void supprimer(Long id) {
        // Pas de cascade REMOVE : les ingrédients partagés sont conservés.
        repository.delete(trouverParId(id));
    }
}

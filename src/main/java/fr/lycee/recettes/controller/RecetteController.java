package fr.lycee.recettes.controller;
import fr.lycee.recettes.entity.Recette;
import fr.lycee.recettes.entity.Ingredient;
import fr.lycee.recettes.service.RecetteService;
import fr.lycee.recettes.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
@Controller
@RequestMapping("/recettes")
public class RecetteController {
    private final RecetteService service;
    private final IngredientService ingredientService;
    public RecetteController(RecetteService service, IngredientService ingredientService) {
        this.service = service;
        this.ingredientService = ingredientService;
    }
    @InitBinder("recette")
    public void champsAutorises(WebDataBinder binder) {
        binder.setAllowedFields("id", "titre", "description", "tempsPreparation", "difficulte", "nombrePersonnes");
    }
    private void preparerFormulaire(Model model, List<Long> ids) {
        model.addAttribute("tousLesIngredients", ingredientService.listerTous());
        model.addAttribute("idsCoches", ids == null ? List.of() : ids);
    }
    @GetMapping
    public String lister(@RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("recettes", service.rechercher(q));
        model.addAttribute("q", q);
        return "recettes/liste";
    }
    @GetMapping("/nouvelle")
    public String creer(Model model) {
        model.addAttribute("recette", new Recette());
        preparerFormulaire(model, List.of());
        return "recettes/formulaire";
    }
    @GetMapping("/{id}/editer")
    public String editer(@PathVariable Long id, Model model) {
        Recette recette = service.trouverParId(id);
        model.addAttribute("recette", recette);
        preparerFormulaire(model, recette.getIngredients().stream().map(Ingredient::getId).toList());
        return "recettes/formulaire";
    }
    @PostMapping
    public String enregistrer(@Valid @ModelAttribute("recette") Recette recette,
            BindingResult resultat, @RequestParam(required = false) List<Long> ingredientIds,
            Model model, RedirectAttributes flash) {
        if (!resultat.hasErrors()) {
            try {
                service.enregistrer(recette, ingredientIds);
            } catch (IllegalArgumentException e) {
                resultat.rejectValue("ingredients", "selection", e.getMessage());
            }
        }
        if (resultat.hasErrors()) {
            preparerFormulaire(model, ingredientIds);
            return "recettes/formulaire";
        }
        flash.addFlashAttribute("message", "Recette enregistrée avec succès.");
        return "redirect:/recettes";
    }
    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes flash) {
        service.supprimer(id);
        flash.addFlashAttribute("message", "Recette supprimée.");
        return "redirect:/recettes";
    }
}

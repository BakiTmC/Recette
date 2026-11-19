package fr.lycee.recettes.controller;
import fr.lycee.recettes.entity.Ingredient;
import fr.lycee.recettes.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/ingredients")
public class IngredientController {
    private final IngredientService service;
    public IngredientController(IngredientService service) { this.service = service; }
    @InitBinder("ingredient")
    public void champsAutorises(WebDataBinder binder) { binder.setAllowedFields("nom", "unite"); }
    @GetMapping
    public String lister(Model model) {
        model.addAttribute("ingredients", service.listerTous());
        return "ingredients/liste";
    }
    @GetMapping("/nouveau")
    public String formulaire(Model model) {
        model.addAttribute("ingredient", new Ingredient());
        return "ingredients/formulaire";
    }
    @PostMapping
    public String enregistrer(@Valid @ModelAttribute("ingredient") Ingredient ingredient,
            BindingResult resultat, RedirectAttributes flash) {
        if (!resultat.hasFieldErrors("nom") && service.nomExiste(ingredient.getNom())) {
            resultat.rejectValue("nom", "doublon", "Cet ingrédient existe déjà.");
        }
        if (resultat.hasErrors()) return "ingredients/formulaire";
        try {
            service.enregistrer(ingredient);
        } catch (DataIntegrityViolationException e) {
            resultat.rejectValue("nom", "doublon", "Cet ingrédient existe déjà.");
            return "ingredients/formulaire";
        }
        flash.addFlashAttribute("message", "Ingrédient enregistré avec succès.");
        return "redirect:/ingredients";
    }
}

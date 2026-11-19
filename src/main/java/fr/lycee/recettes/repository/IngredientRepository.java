package fr.lycee.recettes.repository;
import fr.lycee.recettes.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findAllByOrderByNomAsc();
    boolean existsByNomIgnoreCase(String nom);
}

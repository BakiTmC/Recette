package fr.lycee.recettes.repository;
import fr.lycee.recettes.entity.Recette;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface RecetteRepository extends JpaRepository<Recette, Long> {
    @EntityGraph(attributePaths = "ingredients")
    List<Recette> findAllByOrderByTitreAsc();
    @EntityGraph(attributePaths = "ingredients")
    List<Recette> findByTitreContainingIgnoreCaseOrderByTitreAsc(String titre);
    @Override
    @EntityGraph(attributePaths = "ingredients")
    Optional<Recette> findById(Long id);
}

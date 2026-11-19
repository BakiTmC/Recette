package fr.lycee.recettes;

import fr.lycee.recettes.entity.Ingredient;
import fr.lycee.recettes.entity.Recette;
import fr.lycee.recettes.repository.IngredientRepository;
import fr.lycee.recettes.repository.RecetteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RecettesApplicationTests {
    @Value("${local.server.port}") int port;
    @Autowired IngredientRepository ingredients;
    @Autowired RecetteRepository recettes;
    HttpClient client;

    @BeforeEach
    void initialiser() {
        recettes.deleteAll();
        ingredients.deleteAll();
        client = HttpClient.newBuilder().cookieHandler(new CookieManager()).build();
    }
    HttpResponse<String> get(String chemin) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + chemin)).GET().build(),
            HttpResponse.BodyHandlers.ofString());
    }
    HttpResponse<String> post(String chemin, String donnees) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + chemin))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(donnees)).build(), HttpResponse.BodyHandlers.ofString());
    }
    Ingredient ingredient(String nom) {
        Ingredient i = new Ingredient(); i.setNom(nom); i.setUnite("g");
        return ingredients.save(i);
    }
    String champs(String titre) {
        return "titre=" + titre + "&tempsPreparation=15&difficulte=1&nombrePersonnes=4";
    }
    @Test void pagesEtFragments() throws Exception {
        assertEquals(302, get("/").statusCode());
        for (String url : new String[]{"/recettes", "/recettes/nouvelle", "/ingredients", "/ingredients/nouveau"}) {
            var reponse = get(url);
            assertEquals(200, reponse.statusCode(), url);
            assertTrue(reponse.body().contains("/css/style.css"));
            assertTrue(reponse.body().contains("<footer"));
        }
        assertEquals(200, get("/css/style.css").statusCode());
        assertEquals(404, get("/recettes/999999/editer").statusCode());
    }
    @Test void ingredientValideVideEtDoublon() throws Exception {
        var vide = post("/ingredients", "nom=++&unite=g");
        assertEquals(200, vide.statusCode());
        assertTrue(vide.body().contains("champ-erreur"));
        assertEquals(0, ingredients.count());
        assertEquals(302, post("/ingredients", "nom=Farine&unite=g").statusCode());
        assertTrue(get("/ingredients").body().contains("role=\"status\""));
        assertFalse(get("/ingredients").body().contains("role=\"status\""));
        var doublon = post("/ingredients", "nom=+farine+&unite=kg");
        assertEquals(200, doublon.statusCode());
        assertTrue(doublon.body().contains("champ-erreur"));
        assertEquals(1, ingredients.count());
    }
    @Test void validationEtConservationDesCases() throws Exception {
        Ingredient i = ingredient("Farine");
        var reponse = post("/recettes", "titre=&tempsPreparation=0&difficulte=4&nombrePersonnes=0&ingredientIds=" + i.getId());
        assertEquals(200, reponse.statusCode());
        assertTrue(reponse.body().contains("champ-erreur"));
        assertTrue(reponse.body().contains("checked=\"checked\""));
        assertTrue(reponse.body().contains("Farine"));
        assertEquals(0, recettes.count());
        assertEquals(200, post("/recettes", "titre=Test&tempsPreparation=abc&difficulte=1&nombrePersonnes=4").statusCode());
        assertEquals(200, post("/recettes", champs("a".repeat(121))).statusCode());
        assertEquals(0, recettes.count());
    }
    @Test void creationModificationRechercheSuppression() throws Exception {
        Ingredient farine = ingredient("Farine"), lait = ingredient("Lait");
        assertEquals(302, post("/recettes", champs("Crepes") + "&ingredientIds=" + farine.getId()
            + "&ingredientIds=" + lait.getId()).statusCode());
        assertTrue(get("/recettes").body().contains("role=\"status\""));
        assertFalse(get("/recettes").body().contains("role=\"status\""));
        Recette recette = recettes.findAllByOrderByTitreAsc().getFirst();
        assertEquals(2, recette.getIngredients().size());
        var edition = get("/recettes/" + recette.getId() + "/editer");
        assertEquals(200, edition.statusCode());
        assertEquals(2, edition.body().split("checked=\"checked\"", -1).length - 1);
        assertTrue(get("/recettes?q=CREP").body().contains("Crepes"));
        assertFalse(get("/recettes?q=introuvable").body().contains("Crepes"));
        assertEquals(302, post("/recettes", champs("Galettes") + "&id=" + recette.getId()).statusCode());
        assertEquals(1, recettes.count());
        assertEquals(0, recettes.findById(recette.getId()).orElseThrow().getIngredients().size());
        // GET ne supprime rien.
        assertEquals(405, get("/recettes/" + recette.getId() + "/supprimer").statusCode());
        assertEquals(1, recettes.count());
        assertEquals(302, post("/recettes/" + recette.getId() + "/supprimer", "").statusCode());
        assertEquals(0, recettes.count());
        assertEquals(2, ingredients.count());
        assertTrue(get("/recettes").body().contains("role=\"status\""));
    }
    @Test void ingredientsInexistantsEtIdsDupliques() throws Exception {
        assertEquals(200, post("/recettes", champs("Test") + "&ingredientIds=999999").statusCode());
        assertEquals(0, recettes.count());
        Ingredient i = ingredient("Farine");
        assertEquals(302, post("/recettes", champs("Test") + "&ingredientIds=" + i.getId()
            + "&ingredientIds=" + i.getId()).statusCode());
        assertEquals(1, recettes.findAllByOrderByTitreAsc().getFirst().getIngredients().size());
    }
}

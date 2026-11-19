# Carnet de recettes - Adel BOUKHRICHEN

## Ouvrir et lancer
1. Décompresser puis ouvrir le dossier `recettes` (celui du `pom.xml`) dans IntelliJ.
2. Sélectionner le JDK 26 dans Project Structure et dans les réglages Maven, puis recharger le projet Maven.
3. Préparer PostgreSQL et configurer la connexion comme ci-dessous.
4. Lancer `RecettesApplication`, ou dans PowerShell :

    .\mvnw.cmd spring-boot:run

5. Ouvrir http://localhost:8081/ et ajouter des ingrédients avant une recette.

Le projet reprend Spring Boot 4.1.1 et Java 26 demandés. Si le poste dispose seulement du JDK 25, installer/sélectionner le JDK 26, ou utiliser `-Djava.version=25` si cette adaptation est acceptée par le professeur (également régler le SDK sur 25 dans IntelliJ).

## Base PostgreSQL sur la VM
Si le compte scolaire possède déjà sa base, utiliser cette base et ces identifiants : il n'est pas nécessaire d'en créer d'autres.
Sinon, un administrateur peut exécuter `database/creation.sql` une seule fois. Il crée `recettesdb`, détenue par `recettesuser` (mot de passe de TP : `recettespass`).

Pour accéder au PostgreSQL de la VM par tunnel SSH, ouvrir un terminal et le laisser ouvert :

    ssh -N -L 5433:localhost:5432 UTILISATEUR_SSH@IP_VM

Remplacer UTILISATEUR_SSH et IP_VM. Le port local 5433 correspond au port 5432 de la VM. Les valeurs par défaut de `application.properties` utilisent ce tunnel. Si un tunnel identique existe déjà, le réutiliser.
Dans la configuration Run d'IntelliJ, ajouter si nécessaire ces variables d'environnement :

    DB_URL=jdbc:postgresql://localhost:5433/recettesdb
    DB_USERNAME=recettesuser
    DB_PASSWORD=recettespass

Ces identifiants sont ceux de PostgreSQL, pas ceux de SSH. Pour une connexion directe déjà autorisée à la VM, DB_URL vaut `jdbc:postgresql://IP_VM:5432/NOM_BASE`.
Ne pas réutiliser cinedb si une base dédiée peut être créée. Aucun script ne supprime les tables existantes au démarrage normal.

## Port
Le diaporama demande 8081 et le Word indique 8080. Le projet utilise 8081. Pour changer : définir `PORT=8080` dans la configuration Run, ou modifier `server.port`.

## Fonctionnalités
- CRUD des recettes, ingrédients multiples, cases pré-cochées en modification.
- Liste et ajout des ingrédients ; refus des noms vides et des doublons (casse ignorée à la saisie).
- Contraintes Jakarta Validation et erreurs à côté des champs, conservation des cases lors d'une erreur.
- Fragments Thymeleaf pour l'en-tête, le menu, le pied de page et le message flash.
- Suppression par POST, confirmation JavaScript et conservation des ingrédients partagés.
- Recherche par titre sans distinction de casse (bonus).
- Aucun ingrédient n'est obligatoire : le cahier des charges n'impose pas de minimum.

## Vérifications et tests
Les tests d'intégration démarrent le serveur sur un port aléatoire avec H2 en mémoire, sans toucher à PostgreSQL :

    .\mvnw.cmd test

Ils couvrent les pages Thymeleaf, les erreurs et doublons, les cases conservées, le CRUD, la recherche et la durée de vie du flash. H2 ne remplace pas la vérification de la base PostgreSQL de la VM.

Pour vérifier sur la VM après démarrage :

    \dt
    \d recette_ingredient
    SELECT * FROM recette;
    SELECT * FROM ingredient;
    SELECT * FROM recette_ingredient;

## Captures à joindre au compte rendu
1. Créer Farine (g), Lait (ml) et Oeuf (pièce).
2. Ajouter « Crêpes », temps 15, difficulté 1, 4 personnes et cocher les trois ingrédients.
3. Capturer la liste après enregistrement : le message flash vert et les trois ingrédients comptés sont visibles.
4. Cliquer Modifier et capturer le formulaire avec les trois cases cochées.
5. Vider le titre et saisir 0 minute, puis Enregistrer. Capturer les erreurs et les cases conservées.
6. Corriger puis enregistrer. Capturer le message « Recette enregistrée avec succès. » (il disparaît au rechargement suivant).

Les formulaires utilisent `novalidate` pour montrer les erreurs côté serveur, comme demandé dans le TP. Cela désactive uniquement les bulles du navigateur ; Jakarta Validation reste active.

## Organisation
`entity` : modèle et contraintes ; `repository` : accès aux données ; `service` : opérations et transactions ; `controller` : pages et formulaires ; `templates` : vues ; `static/css` : mise en forme.

# Guide pour les assistants IA sur ce projet

Ce fichier s'adresse aux assistants IA utilisés dans l'éditeur par les développeurs qui travaillent sur ce dépôt.

## Posture attendue

- Aidez à **comprendre** avant d'aider à produire : expliquez le pourquoi d'une approche avant de montrer du code.
- Procédez par **petites étapes** : une chose à la fois, vérifiée avant de passer à la suivante.
- **Posez des questions** avant de coder quand le besoin est ambigu : quel comportement est attendu ? quel cas limite compte ?
- Ne remplacez pas le raisonnement du développeur : proposez, laissez-le décider et justifier.
- Quand vous générez du code, invitez systématiquement à le relire et à le vérifier : le code généré n'est pas fiable par défaut.

## Ce projet

Application Spring Boot de réservation d'emplacements d'amarrage, architecture en couches classique :

- `controller/` — API REST, ne contient pas de logique métier, échange uniquement des DTO ;
- `service/` — logique métier (tarification dans `BillingService`, réservations dans `ReservationService`, comptes dans `UserService`) ;
- `repository/` — interfaces Spring Data JPA ;
- `entity/` — modèle de persistance, ne traverse jamais un contrôleur ;
- `exception/` — exceptions métier traduites en HTTP par un `@RestControllerAdvice` ;
- `config/` — configuration de sécurité et données de démarrage.

## Stack et versions

- Java 21, Spring Boot 3.3.5 (Web, Data JPA, Security, Validation)
- H2 en base par défaut, Maven pour le build
- `spring-boot-starter-test` disponible (JUnit 5, Mockito, AssertJ, MockMvc)

## Bonnes pratiques propres à cette stack

- Injection **par constructeur**, jamais par champ : c'est ce qui rend une classe testable sans conteneur Spring.
- Distinguez `@Mock` (Mockito pur, pas de contexte Spring, rapide) de `@MockBean` (charge le contexte) : un test de règle de calcul n'a pas besoin de démarrer l'application.
- Validation des entrées par Bean Validation (`@Valid`, `@NotNull`, `@Size`) plutôt que par des `if` en tête de méthode.
- Les exceptions métier sont des classes ; le mapping HTTP se fait dans le `@RestControllerAdvice`, jamais dans un service.
- Attention au chargement paresseux JPA hors transaction (`LazyInitializationException`) : expliquez la cause, ne la contournez pas au hasard.
- Pour `BigDecimal`, comparez avec `compareTo` et non `equals` (l'échelle compte dans `equals`).

## Mises en garde

- **Jamais de secret en dur** dans le code ou dans les fichiers versionnés : clés, jetons et mots de passe d'infrastructure passent par les variables d'environnement (`.env`, non versionné).
- Les données des utilisateurs sont des données personnelles : ne les copiez pas dans des prompts externes, ne les journalisez pas.
- Ne faites pas confiance aveuglément au code généré : vérifiez chaque suggestion contre le comportement réellement attendu, idéalement par un test.

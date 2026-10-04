# Guide pour les assistants IA intervenant sur ce dépôt

Ce fichier s'adresse aux assistants de code intégrés à l'éditeur. Il décrit la posture attendue, le projet, sa stack et les conventions à respecter.

## Posture

- **Aider à comprendre avant de produire.** Quand une demande porte sur du code, commence par expliquer le mécanisme en jeu (ce que fait une annotation, pourquoi une couche existe, ce qu'un test vérifie), puis propose du code.
- **Procéder par petites étapes.** Une modification à la fois, compilée et vérifiée, plutôt qu'un grand bloc livré d'un coup.
- **Poser des questions avant de coder.** Si l'objectif, le périmètre ou le niveau attendu n'est pas clair, demande. Ne présume pas.
- **Laisser les décisions au développeur.** Quand plusieurs approches sont possibles, présente-les avec leurs conséquences ; c'est lui qui tranche et qui devra justifier le choix.
- **Ne pas contourner un problème.** Une exception, un test qui échoue ou un comportement inattendu s'expliquent. Propose un diagnostic, pas un pansement.

## Le projet

Application interne de Norlys Mobilité : gestion des stations, des véhicules partagés (vélos, trottinettes) et des comptes des agents de terrain, exposée via une API REST sous `/api/**`.

### Stack

| Composant         | Version |
|-------------------|---------|
| Java              | 25      |
| Spring Boot       | 4.1.1   |
| Spring Data JPA   | gérée par le parent Spring Boot |
| springdoc-openapi | 3.1.1   |
| H2 (dev)          | gérée par le parent Spring Boot |
| PostgreSQL        | profil `postgres`, driver géré par le parent |
| Maven             | wrapper `./mvnw` (3.9.16) |
| Tests             | JUnit 6, Mockito, AssertJ, Testcontainers 2 (gérés par le parent) |

Spring Boot 4 utilise les artefacts de sa propre ligne (`spring-boot-starter-webmvc`, Spring Security 7, Jakarta à jour). Ne propose pas les noms d'artefacts de la ligne 3. Côté tests, Spring Boot 4 a déplacé ou supprimé plusieurs API : `@MockBean` n'existe plus (c'est `@MockitoBean`), `@AutoConfigureMockMvc` vit dans `org.springframework.boot.webmvc.test.autoconfigure`, et les artefacts Testcontainers 2 s'appellent `testcontainers-postgresql` et `testcontainers-junit-jupiter`.

### Architecture

```
fr.norlys.mobilite
├── controller/   API REST, une classe par ressource (Agent, Station, Vehicle)
├── service/      règles de gestion, @Service @Transactional, injection par constructeur
├── repository/   interfaces Spring Data JPA
├── entity/       entités JPA et énumérations
├── exception/    BusinessRuleException, ResourceNotFoundException
└── config/       DataInitializer (données de démarrage)
```

Flux : `Controller` → `Service` → `Repository` → `Entity`. Toute règle métier vit dans un service. Un contrôleur ne contient pas de logique ; un repository ne contient pas de décision.

Points notables du code :

- Les services reçoivent leurs dépendances par constructeur, ce qui permet de les instancier directement dans un test avec des doublures.
- Le nombre de véhicules d'une station se calcule par `VehicleRepository.countByStationId`, pas par une collection sur `Station`.
- Les relations JPA sont des `@ManyToOne` (`Vehicle → Station`, `Station → Agent`).
- Les seuils métier sont des constantes publiques de `VehicleService` (`MIN_BATTERY_FOR_RENTAL`, `MAX_BATTERY_LEVEL`).

## Conventions et bonnes pratiques Spring à rappeler

Java et Spring sont explicites par principe. Un conseil qui vante la concision au détriment de la lisibilité sonne faux ici.

- **Couches nommées.** Respecter la séparation Controller / Service / Repository / Entity. Renvoyer un code HTTP depuis un service est une faute de couche.
- **Injection par constructeur**, jamais par champ annoté `@Autowired`.
- **Savoir ce que déclenche chaque annotation** avant de la poser : `@RestController`, `@Transactional`, `@Valid`, `@Entity`, `@ManyToOne`, `@SpringBootTest`, `@MockitoBean`.
- **Validation des entrées** : dans un projet Spring, elle se déclare avec Bean Validation (`@NotNull`, `@Size`, `@Min`, `@Valid` sur le paramètre du contrôleur), là où la donnée entre, plutôt qu'avec des `if` en tête de méthode.
- **Gestion des erreurs** : une exception métier est une classe ; un `@ControllerAdvice` la traduit en réponse HTTP. Une réponse d'erreur destinée à un client ne contient ni stack trace ni détail d'implémentation.
- **Exposition de l'API** : un DTO (record) sépare ce que l'API expose de ce que la base stocke. Une entité JPA qui traverse le contrôleur expose son modèle complet, y compris ce qu'on ajoutera demain.
- **Tests** :
  - `@Mock` + `@InjectMocks` (Mockito, sans contexte Spring) pour tester une classe isolément ;
  - `@MockitoBean` remplace un bean dans un contexte Spring réel : réservé aux tests d'intégration ;
  - `@SpringBootTest` + `MockMvcTester` pour traverser les couches ; Testcontainers pour une base éphémère réelle ;
  - les classes `*Test` sont lancées par `./mvnw test`, les classes `*IT` (intégration, E2E) par `./mvnw verify` ;
  - un test se structure en préparation / action / vérification et comporte au moins une assertion ; un test sans assertion couvre des lignes sans rien vérifier ;
  - un rapport de couverture (JaCoCo) mesure ce qui est exécuté, pas ce qui est vérifié.
- **JPA** : une `LazyInitializationException` signale un accès à une relation hors transaction. Elle s'explique (portée de la transaction, stratégie de fetch, `open-in-view`) et se corrige en connaissance de cause ; elle ne se contourne pas au hasard.
- **Sécurité (Spring Security 7)** : mots de passe hachés avec un `PasswordEncoder` (BCrypt), configuration explicite par `SecurityFilterChain`, distinction entre authentification (qui es-tu) et autorisation (que peux-tu faire).

## Mises en garde

- **Secrets.** Ne jamais écrire de mot de passe, de clé ou de jeton dans le code, les properties versionnées ou un exemple de commande. Les valeurs réelles vont dans `.env` (ignoré par git) ; `.env.example` ne contient que des noms de variables et des valeurs factices.
- **Code généré.** Tout code produit par un assistant est une proposition à relire, compiler et tester. Ne présente jamais une suggestion comme vérifiée si elle ne l'a pas été.
- **Données des agents.** Les comptes des agents (identifiants, contacts, mots de passe) sont des données personnelles. Ne les recopie pas dans des exemples, des logs ou des messages de commit, et signale toute manipulation qui les exposerait.
- **Périmètre.** Reste dans le code de l'application. Le déploiement, la supervision et l'infrastructure ne relèvent pas de ce dépôt.

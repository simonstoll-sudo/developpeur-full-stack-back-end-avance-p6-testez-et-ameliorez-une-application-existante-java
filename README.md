# Norlys Mobilité - Gestion de flotte

Application interne de Norlys Mobilité pour la gestion des stations, des véhicules partagés (vélos et trottinettes) et des comptes des agents de terrain. Elle expose une API REST consommée par les outils de terrain et le back-office.

Version courante : `1.4.2`

## Prérequis

- Java 25 (JDK)
- Docker, pour les tests d'intégration (base PostgreSQL éphémère)
- PostgreSQL 16, uniquement pour le profil `postgres` ; le développement local utilise H2 en mémoire

Maven n'a pas besoin d'être installé : le dépôt contient le wrapper `./mvnw` (`mvnw.cmd` sous Windows).

## Installation

```bash
git clone <url-du-depot> norlys-mobilite
cd norlys-mobilite
./mvnw clean install
```

## Lancement

### Développement (H2 en mémoire)

```bash
./mvnw spring-boot:run
```

L'application démarre sur `http://localhost:8080`. La base H2 est recréée à chaque démarrage et un jeu de données de démarrage (agents, stations, véhicules) est inséré automatiquement si elle est vide.

- Swagger UI : `http://localhost:8080/swagger-ui.html`
- Spécification OpenAPI : `http://localhost:8080/v3/api-docs`
- Console H2 : `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:norlys`, utilisateur `sa`, mot de passe vide)

### Profil `postgres`

Copier `.env.example` vers `.env`, renseigner les valeurs, exporter les variables dans l'environnement, puis :

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

Variables attendues :

| Variable      | Rôle                                   | Exemple                                     |
|---------------|----------------------------------------|---------------------------------------------|
| `SERVER_PORT` | Port HTTP (défaut `8080`)              | `8080`                                      |
| `DB_URL`      | URL JDBC de la base PostgreSQL         | `jdbc:postgresql://localhost:5432/norlys`   |
| `DB_USERNAME` | Utilisateur de la base                 | `norlys`                                    |
| `DB_PASSWORD` | Mot de passe de la base                | à renseigner                                |

## Stack technique

| Composant           | Version |
|---------------------|---------|
| Java                | 25      |
| Spring Boot         | 4.1.1   |
| Spring Data JPA     | géré par Spring Boot |
| springdoc-openapi   | 3.1.1   |
| H2                  | géré par Spring Boot |
| PostgreSQL (driver) | géré par Spring Boot |
| Maven (wrapper)     | 3.9.16  |
| JUnit / Mockito / Testcontainers | gérés par Spring Boot |

## Structure du projet

```
src/main/java/fr/norlys/mobilite
├── NorlysMobiliteApplication.java   point d'entrée Spring Boot
├── config/
│   └── DataInitializer.java         jeu de données de démarrage
├── controller/                      API REST (/api/**)
│   ├── AgentController.java
│   ├── StationController.java
│   ├── VehicleController.java
│   └── PasswordChangeRequest.java
├── service/                         règles de gestion
│   ├── AgentService.java
│   ├── StationService.java
│   ├── VehicleService.java
│   └── StationOccupancy.java
├── repository/                      accès aux données (Spring Data JPA)
│   ├── AgentRepository.java
│   ├── StationRepository.java
│   └── VehicleRepository.java
├── entity/                          modèle de persistance
│   ├── Agent.java, AgentRole.java
│   ├── Station.java
│   └── Vehicle.java, VehicleType.java, VehicleStatus.java
└── exception/                       exceptions métier
    ├── BusinessRuleException.java
    └── ResourceNotFoundException.java

src/main/resources
├── application.properties           configuration par défaut (H2)
└── application-postgres.properties  profil postgres
```

L'application suit une architecture en couches : `Controller` → `Service` → `Repository` → `Entity`. Les règles de gestion vivent exclusivement dans les services, qui reçoivent leurs dépendances par constructeur.

## Règles de gestion principales

- Une station a une capacité strictement positive ; elle ne peut pas être réduite sous le nombre de véhicules présents, ni désactivée tant qu'elle contient des véhicules.
- Seul un agent actif ayant le rôle `SUPERVISEUR` peut être désigné responsable d'une station.
- Un véhicule est louable s'il est `AVAILABLE`, rattaché à une station et que sa batterie est au moins à 20 %. En dessous de ce seuil il passe en `LOW_BATTERY`.
- Un véhicule ne peut être affecté qu'à une station active et non pleine ; un véhicule `IN_USE` ou `OUT_OF_SERVICE` ne peut pas être affecté.
- Le retour de location fixe le niveau de batterie et recalcule le statut (`AVAILABLE` ou `LOW_BATTERY`).

## Points d'entrée de l'API

### Agents - `/api/agents`

| Méthode | Chemin              | Description                        |
|---------|---------------------|------------------------------------|
| GET     | `/`                 | Liste des agents                   |
| GET     | `/{id}`             | Détail d'un agent                  |
| POST    | `/`                 | Création d'un agent                |
| PUT     | `/{id}/password`    | Changement de mot de passe         |
| DELETE  | `/{id}`             | Désactivation d'un agent           |

### Stations - `/api/stations`

| Méthode | Chemin                         | Description                              |
|---------|--------------------------------|------------------------------------------|
| GET     | `/`                            | Liste des stations                       |
| GET     | `/{id}`                        | Détail d'une station                     |
| POST    | `/`                            | Création d'une station                   |
| PUT     | `/{id}`                        | Mise à jour (nom, adresse, capacité, coordonnées) |
| PUT     | `/{id}/manager/{agentId}`      | Désignation du responsable               |
| GET     | `/{id}/vehicles`               | Véhicules présents dans la station       |
| GET     | `/{id}/occupancy`              | Occupation (places libres, taux)         |
| DELETE  | `/{id}`                        | Désactivation d'une station              |

### Véhicules - `/api/vehicles`

| Méthode | Chemin                                              | Description                        |
|---------|-----------------------------------------------------|------------------------------------|
| GET     | `/`                                                 | Liste des véhicules                |
| GET     | `/{id}`                                             | Détail d'un véhicule               |
| POST    | `/`                                                 | Ajout d'un véhicule à la flotte    |
| PUT     | `/{id}/station/{stationId}`                         | Affectation à une station          |
| POST    | `/{id}/rental/start`                                | Début de location                  |
| POST    | `/{id}/rental/end?stationId=&batteryLevel=`         | Fin de location                    |
| PUT     | `/{id}/battery?level=`                              | Mise à jour du niveau de batterie  |
| POST    | `/{id}/maintenance`                                 | Envoi en maintenance               |
| POST    | `/{id}/maintenance/return`                          | Retour de maintenance              |
| DELETE  | `/{id}`                                             | Retrait définitif de la flotte     |

La description complète des corps de requête et de réponse est disponible dans Swagger UI.

## Scripts Maven

| Commande                                                      | Effet                                                        |
|---------------------------------------------------------------|--------------------------------------------------------------|
| `./mvnw spring-boot:run`                                      | Lance l'application avec H2                                  |
| `./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres`  | Lance l'application sur PostgreSQL                           |
| `./mvnw test`                                                 | Exécute les tests unitaires                                  |
| `./mvnw verify`                                               | Exécute tous les tests (classes `*IT` comprises) et produit les rapports de couverture dans `target/site/jacoco` et `target/site/jacoco-it` |
| `./mvnw package`                                              | Produit le jar exécutable dans `target/`                     |

## Licence

Logiciel propriétaire, usage interne Norlys Mobilité. Tous droits réservés.

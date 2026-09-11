# Ancrelieu — Application de réservation d'emplacements d'amarrage

Ancrelieu propose la location d'emplacements d'amarrage dans plusieurs ports de plaisance. Cette application gère le catalogue des emplacements, les comptes clients et les réservations : recherche de disponibilité, création, calcul du montant du séjour et annulation avec calcul du remboursement.

## Prérequis

- JDK 21 ou supérieur
- Maven 3.9+

## Installation

```bash
git clone <url-du-depot>
cd reservation
mvn clean install
```

## Lancement

```bash
mvn spring-boot:run
```

L'application démarre sur `http://localhost:8080`. Au premier lancement, un jeu de données de démarrage (utilisateurs, emplacements, réservations) est inséré automatiquement si la base est vide.

## Configuration

Les paramètres se règlent par variables d'environnement (voir `.env.example`). Sans configuration, l'application utilise une base H2 en mémoire.

| Variable | Rôle | Valeur par défaut |
| --- | --- | --- |
| `DB_URL` | URL JDBC de la base de données | `jdbc:h2:mem:ancrelieu` |
| `DB_USERNAME` | Utilisateur de la base | `sa` |
| `DB_PASSWORD` | Mot de passe de la base | *(vide)* |
| `SERVER_PORT` | Port HTTP du serveur | `8080` |

La console H2 est disponible sur `http://localhost:8080/h2-console`.

## Stack technique

- Java 21
- Spring Boot 3.3 (Web, Data JPA, Security, Validation)
- Base de données H2 (par défaut)
- Maven

## Structure du projet

```
src/main/java/com/ancrelieu/reservation/
├── config/          Configuration (sécurité, données de démarrage)
├── controller/      API REST
├── dto/             Objets d'échange de l'API
├── entity/          Entités JPA
├── exception/       Exceptions métier et gestionnaire global
├── repository/      Accès aux données (Spring Data JPA)
└── service/         Logique métier (tarification, réservations, comptes)
```

Le dossier `starter-kit/` contient les documents de travail de l'équipe (plan de test, notes d'audit).

## Points d'entrée de l'API

| Méthode | Route | Description |
| --- | --- | --- |
| POST | `/api/auth/register` | Inscription d'un utilisateur |
| POST | `/api/auth/login` | Connexion |
| GET | `/api/users` | Liste des utilisateurs |
| GET | `/api/users/{id}` | Détail d'un utilisateur |
| GET | `/api/berths` | Liste des emplacements actifs |
| GET | `/api/berths/{id}/availability?start=YYYY-MM-DD&end=YYYY-MM-DD` | Disponibilité d'un emplacement |
| GET | `/api/reservations` | Liste des réservations |
| GET | `/api/reservations/{id}` | Détail d'une réservation |
| GET | `/api/reservations/user/{userId}` | Réservations d'un utilisateur |
| POST | `/api/reservations` | Création d'une réservation |
| POST | `/api/reservations/{id}/cancel` | Annulation d'une réservation |

## Scripts utiles

| Commande | Rôle |
| --- | --- |
| `mvn clean install` | Compilation et packaging |
| `mvn spring-boot:run` | Lancement en local |

## Licence

Propriété d'Ancrelieu. Usage interne uniquement.

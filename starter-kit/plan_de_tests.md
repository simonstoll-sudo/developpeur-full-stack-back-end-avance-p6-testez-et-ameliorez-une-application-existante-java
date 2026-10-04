# Plan de tests - Application de gestion de flotte Norlys Mobilité

## Contexte

Ce plan liste les règles et les parcours à couvrir, et le niveau de test attendu pour chacun. Il ne décrit pas l'écriture des tests.

Deux régressions récentes portaient sur les règles de gestion des véhicules. Un vélo à 12 % de batterie a été proposé à la location, et une station de dix places a accepté un onzième vélo. Ces deux règles sont prioritaires.

## Outillage

Le projet se construit avec le wrapper Maven. Seuls un JDK 25 et Docker sont nécessaires.

```bash
./mvnw test      # tests unitaires seulement, quelques secondes, sans Docker
./mvnw verify    # tous les tests (unitaires, intégration, E2E) et les rapports de couverture
```

Les classes dont le nom finit par `IT` sont des tests d'intégration. `./mvnw test` ne les lance pas, `./mvnw verify` si. Le `pom.xml` contient déjà JaCoCo, Testcontainers et le starter de test MockMvc de Spring Boot 4.

## 1. Tests unitaires

Un test unitaire instancie un service avec ses repositories remplacés par des doublures Mockito, sans contexte Spring et sans base. Chaque règle est couverte par un cas nominal, au moins un cas limite et au moins un cas d'erreur, soit une vingtaine de tests au total.

| Classe | Règles à couvrir |
| --- | --- |
| `VehicleService` | Un véhicule n'est louable (`startRental`) que s'il est `AVAILABLE`, rattaché à une station et chargé à au moins 20 %. Le retour de location (`endRental`) exige une station active et non pleine, et recalcule le statut selon la batterie. La mise à jour de la batterie (`updateBattery`) fait basculer le véhicule entre `AVAILABLE` et `LOW_BATTERY` autour du seuil de 20 %. L'affectation à une station (`assignToStation`) est refusée si la station est désactivée ou pleine, ou si le véhicule est `IN_USE` ou `OUT_OF_SERVICE`. |
| `StationService` | L'occupation (`occupancy`) donne les places libres et le taux d'occupation. Une mise à jour (`update`) ne descend jamais la capacité sous le nombre de véhicules présents. Une station ne peut être désactivée (`deactivate`) que vide. Seul un superviseur actif peut en être responsable (`assignManager`). |
| `AgentService` | À la création (`create`), l'identifiant est obligatoire et unique, l'adresse e-mail est unique, et le rôle vaut `TERRAIN` par défaut. |

Exemple pour la première règle :

```java
@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock VehicleRepository vehicleRepository;
    @Mock StationRepository stationRepository;
    @InjectMocks VehicleService vehicleService;

    @Test
    void startRental_refusesVehicleBelowBatteryThreshold() {
        Vehicle vehicle = new Vehicle();
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setStation(new Station());
        vehicle.setBatteryLevel(19);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.startRental(1L))
                .isInstanceOf(BusinessRuleException.class);
        verify(vehicleRepository, never()).save(any());
    }
}
```

Le seuil de 20 % est une constante publique de `VehicleService`. Les valeurs 19 et 20 sont à tester toutes les deux.

## 2. Tests d'intégration

Un test d'intégration démarre le contexte Spring et traverse contrôleur, service, repository et base, avec `MockMvcTester`. Au moins un parcours tourne contre PostgreSQL, démarré par Testcontainers, car H2 ne gère pas les contraintes comme la base de production.

| Parcours | Requêtes | À vérifier |
| --- | --- | --- |
| P1. Occupation d'une station | `GET /api/stations/{id}/occupancy` | Réponse 200, avec un taux qui correspond aux véhicules présents en base |
| P2. Affectation à une station pleine (PostgreSQL) | `PUT /api/vehicles/{id}/station/{stationId}` | Requête refusée, station inchangée en base |
| P3. Location puis retour | `POST /api/vehicles/{id}/rental/start`, puis `.../rental/end?stationId=&batteryLevel=` | Le véhicule quitte sa station, puis y revient avec le bon statut |
| P4. Batterie faible | `PUT /api/vehicles/{id}/battery?level=15` | Statut `LOW_BATTERY` lu ensuite en base |
| P5. Identifiant en doublon | `POST /api/agents` deux fois avec le même `username` | Seconde requête refusée, un seul agent en base |

L'application ne traduit pas encore les erreurs métier en codes HTTP. Dans MockMvc, une `BusinessRuleException` remonte donc telle quelle jusqu'au test au lieu de produire une réponse. Les tests portent sur ce comportement actuel et seront ajustés avec les corrections du brief sécurité.

Les classes vont dans `src/test/java/fr/norlys/mobilite/integration`, avec le suffixe `IT`. Chaque test prépare ses propres données et ne dépend ni de l'ordre d'exécution ni du jeu de démarrage.

## 3. Scénario end-to-end

Un seul scénario, joué contre l'application démarrée sur un port aléatoire (`@SpringBootTest(webEnvironment = RANDOM_PORT)` et `RestTestClient`), uniquement par l'API HTTP.

1. Création d'une station de capacité 2 ;
2. Ajout d'un vélo chargé à 80 % et affectation à la station ;
3. Lecture de l'occupation, qui indique 1 véhicule et un taux de 0.5 ;
4. Début de location, après lequel le vélo n'est plus dans la station ;
5. Fin de location avec une batterie à 12 %, après laquelle le vélo est en `LOW_BATTERY` et ne peut plus être loué.

Une fois l'authentification en place, le scénario commencera par l'identification d'un agent, sans changement pour les étapes suivantes.

La classe attendue est `ParcoursAgentE2EIT`, dans `src/test/java/fr/norlys/mobilite/e2e`.

## 4. Couverture

`./mvnw verify` produit deux rapports, `target/site/jacoco/index.html` pour les tests unitaires et `target/site/jacoco-it/index.html` pour l'intégration et l'E2E.

- Au moins 80 % des lignes du package `service` couvertes par les seuls tests unitaires ;
- Un commentaire dans le README sur ce que le chiffre couvre, ce qu'il ne couvre pas, et une classe peu couverte, avec la raison.

Un test sans assertion utile ne compte pas, même s'il fait monter le pourcentage.

## 5. Hors périmètre

- La configuration Spring et `DataInitializer` ;
- Les entités prises isolément (pas de test de getters) ;
- Les performances et la charge ;
- La sécurité, dont les tests viendront avec le brief sécurité.

Une règle risquée absente de ce plan est à signaler dans le README.

# Brief sécurité - Application de gestion de flotte Norlys Mobilité

## Contexte

Chaque constat indique où se trouve le problème, le risque et le résultat attendu. Le choix de la correction est libre et se justifie dans le README. Les références renvoient à l'OWASP Top 10, version 2025.

La suite de tests est verte au départ et doit l'être à la fin. Les tests dont le comportement change à cause d'une correction sont adaptés, et le message de commit le signale.

## Constat 1. Les mots de passe des agents sont lisibles

**Où :** `AgentService.create` et `AgentService.changePassword` enregistrent le mot de passe tel qu'il arrive. `DataInitializer` crée les comptes de démonstration avec leurs mots de passe en clair, et ce fichier est versionné.

**Risque :** quiconque accède à la base, à une sauvegarde ou au dépôt connaît les mots de passe de tous les agents, et beaucoup les réutilisent ailleurs.

**Référence :** A04:2025, défaillances cryptographiques.

**Résultat attendu :** plus aucun mot de passe lisible, ni en base, ni dans le code. Un agent se connecte toujours avec son mot de passe, mais personne ne peut le retrouver en lisant la base.

## Constat 2. Personne n'a besoin de s'identifier

**Où :** toute l'API sous `/api/**`, en lecture comme en écriture. L'auditeur a changé le mot de passe d'un superviseur avec `PUT /api/agents/{id}/password` depuis un poste extérieur. La console H2 (`/h2-console`) est elle aussi ouverte.

**Risque :** n'importe qui peut modifier l'état de la flotte ou prendre la main sur un compte.

**Référence :** A07:2025, défaillances d'authentification, et A01:2025, contrôle d'accès défaillant.

**Résultat attendu :** une requête sans identification reçoit un 401, et la même requête avec les identifiants d'un agent actif est acceptée. Un agent désactivé ne peut plus se connecter. Swagger UI est le seul point d'entrée qui reste accessible sans identification. Une authentification HTTP Basic suffit, les jetons ne sont pas dans le périmètre.

## Constat 3. L'API expose les entités JPA telles quelles

**Où :** les trois contrôleurs renvoient et reçoivent directement les entités `Agent`, `Station` et `Vehicle`. `GET /api/agents` renvoie le mot de passe et le téléphone personnel de chaque agent. `GET /api/vehicles/{id}` les renvoie aussi, par ricochet, puisque le véhicule contient sa station, qui contient son responsable. Et `POST /api/agents` accepte un champ `role`, ce qui a permis à l'auditeur de se créer un compte superviseur.

**Risque :** tout champ ajouté demain à une entité sort dans l'API sans que personne ne l'ait décidé, et un client choisit lui-même ses droits.

**Référence :** A01:2025, contrôle d'accès défaillant, et A06:2025, conception non sécurisée.

**Résultat attendu :** l'API n'expose et n'accepte que des champs choisis explicitement. Aucune réponse ne contient de mot de passe ni de téléphone, et le rôle d'un agent ne se fixe pas depuis la requête de création.

## Constat 4. Les entrées sont contrôlées trop tard, ou pas du tout

**Où :** les créations et mises à jour. Sans numéro de série, sans type ou sans nom de station, la requête va jusqu'à la base et échoue sur une contrainte SQL. L'adresse e-mail d'un agent n'est pas vérifiée, et un nom trop long n'est refusé que par la base, avec une erreur SQL. Les contrôles qui existent sont des `if` dispersés dans les services.

**Risque :** des données incohérentes en base, et des règles métier qui reposent sur des valeurs jamais vérifiées.

**Référence :** A06:2025, conception non sécurisée.

**Résultat attendu :** chaque donnée est vérifiée à l'entrée de l'API, avant d'atteindre un service. Cela couvre les champs obligatoires, une batterie entre 0 et 100, une capacité strictement positive, un e-mail valide et un nom de 80 caractères au plus. Une donnée invalide reçoit un 400 avec un message qui nomme le champ.

## Constat 5. Les erreurs racontent l'intérieur de l'application

**Où :** `application.properties` demande d'inclure la classe de l'exception et la trace complète dans chaque réponse d'erreur. `GET /api/stations/999` renvoie un 500 avec toute la pile d'appels. Une station pleine ou un véhicule introuvable produisent aussi un 500, car aucune erreur métier n'est traduite en code HTTP.

**Risque :** une trace d'erreur est une carte de l'application offerte à qui veut l'attaquer, et un client ne peut pas distinguer une erreur de sa part d'une panne du serveur.

**Référence :** A10:2025, mauvaise gestion des conditions exceptionnelles, et A02:2025, mauvaise configuration de sécurité.

**Résultat attendu :** une réponse courte et compréhensible, sans trace technique, avec le bon code (404 pour une ressource introuvable, 409 pour une règle métier violée, 400 pour une entrée invalide, 401 sans identification).

## Ordre et preuves

Les constats se traitent dans l'ordre, les deux premiers étant les plus graves. Chacun donne lieu à une correction, à un test qui prouve que le problème ne peut plus se reproduire, et à une entrée dans le README. Cette entrée décrit ce qui était en place, ce qui l'a remplacé, le risque couvert avec sa référence OWASP, et la façon de le vérifier.

## Hors périmètre

- Les droits par rôle, car un agent identifié peut accéder à tout pour l'instant ;
- Le déploiement, le réseau et la supervision, qui sont le chantier suivant ;
- Toute autre faille, à signaler dans le README si elle est repérée.

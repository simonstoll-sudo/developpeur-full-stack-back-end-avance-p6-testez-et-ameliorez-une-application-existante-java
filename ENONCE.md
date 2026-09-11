:::meta
titre: Testez et améliorez une application existante
parcours: Développeur Full-stack back-end avancé
numero_projet: P6
variation_scenario: standard
nb_missions: 1
stacks_disponibles: Java, Python, .NET
guidage: entierement_guide
duree_supervisee_h: 55
duree_personnelle_h: 55
competence_1: Écrire des tests unitaires
competence_2: Écrire des tests d'intégration et e2e
competence_3: Sécuriser les données d'une application / Mettre en place l'authentification d'une application
:::

# Page d'accueil

## Qu'allez-vous apprendre dans ce projet ?

Vous allez apprendre à écrire des tests automatisés sur une application full-stack existante : tests unitaires sur la logique métier avec isolation des dépendances par des doublures (mocks), tests d'intégration sur les couches connectées (API et persistance), et un test de bout en bout (E2E) sur un parcours utilisateur complet. Vous apprendrez à générer un rapport de couverture de code et, surtout, à le lire de façon critique plutôt que de le prendre pour une mesure absolue de qualité.

Vous mettrez ensuite en pratique les fondamentaux de la sécurité applicative : hachage des mots de passe, mise en place de l'authentification, contrôle des autorisations pour empêcher un utilisateur d'accéder aux données d'un autre. Vous traiterez des vulnérabilités réelles identifiées dans une note d'audit, et vous documenterez vos choix pour qu'un tiers puisse comprendre ce qui a été corrigé et pourquoi.

## En quoi ces compétences sont-elles importantes pour votre carrière ?

Écrire des tests et savoir interpréter un rapport de couverture est une compétence attendue de tout développeur back-end, quelle que soit la stack : c'est ce qui permet de faire évoluer une application sans la casser en silence, et c'est un critère de sélection fréquent en entretien technique.

La sécurisation des données et la mise en place d'une authentification fiable sont des sujets sur lesquels une erreur a un coût direct pour l'entreprise (fuite de données, perte de confiance client, non-conformité RGPD). Savoir identifier une faiblesse, la corriger et l'expliquer clairement à un non-spécialiste est une compétence très valorisée, y compris dans des contextes réglementés.

## Comment allez-vous procéder ?

- **Cours** : Ce projet ne comporte pas de cours associé ; il s'appuie sur les connaissances acquises précédemment (stratégies de tests, couverture de code, débogage, sécurité applicative).
- **Mission 1 : Fiabilisez et sécurisez l'application de réservation** : vous choisirez d'abord la stack technique sur laquelle vous travaillez (Java, Python ou .NET), puis vous écrirez, sur la version correspondante de l'application, des tests unitaires, d'intégration et de bout en bout, vous mesurerez et analyserez la couverture de code obtenue, puis vous sécuriserez le système de comptes et d'accès aux données en documentant les vulnérabilités traitées.

À l'issue de ce projet, vous présenterez les livrables de la mission à un mentor évaluateur lors d'une soutenance. Cela vous permettra de valider les compétences visées par ce projet.

## Prêt à démarrer votre projet ?

Lancez-vous dans la première section « Mission 1 - Fiabilisez et sécurisez l'application de réservation ».

:::encadre type=info
**Votre projet démarre : suivez ces quelques recommandations pour être plus efficace !**

- Coupez dès à présent toutes les sources de distraction : téléphone, messagerie, mails, notifications, etc.
- Évitez les situations de multitâches : n'écoutez pas un podcast ou les informations en travaillant.
- Préparez votre environnement de travail : onglets, documents téléchargés, raccourcis, etc.

Vous avez toutes les cartes en main, c'est parti ! Pour plus de conseils, suivez ce chapitre de cours : Mettez en place votre environnement d'apprentissage.
:::

---

# Mission 1 - Fiabilisez et sécurisez l'application de réservation (Java)

:::encadre type=autoeval
Les critères d'évaluation sont dans le guide mentor du projet.
Cependant, l'étudiant a accès à une **fiche d'autoévaluation** dans la dernière étape de son travail.
Elle contient des critères moins détaillés que les vôtres.
L'étudiant peut l'utiliser comme base de discussion avec vous mais surtout comme checklist pour vérifier qu'il n'a rien oublié.
:::

## Comment allez-vous procéder ?

Cette mission suit un scénario de projet professionnel. Vous pouvez suivre les étapes pour vous aider à réaliser vos livrables.

Avant de démarrer, nous vous conseillons de :

- lire toute la mission et ses documents liés ;
- prendre des notes sur ce que vous avez compris ;
- consulter les étapes pour vous guider ;
- préparer une liste de questions pour votre session de mentorat.

:::encadre type=info
**Quelques conseils de méthodologie de travail :**

**Sur la construction du projet :** Soyez prêt à détailler votre processus de décision. Exemples : pourquoi avoir choisi de ne pas tester telle partie du code ? Pourquoi avoir retenu cet algorithme de hachage plutôt qu'un autre ? Pourquoi ce framework de test plutôt qu'un autre, compte tenu de la stack (Java, Python ou .NET) que vous avez choisie ?

**Sur l'usage de l'IA :** Si vous utilisez des outils d'intelligence artificielle, vous devez être capable d'expliquer comment vous avez vérifié et adapté le contenu produit. L'IA doit être un assistant à la compréhension, pas un substitut à votre raisonnement de test et à votre analyse de sécurité.
:::

## Prêt à mener la mission ?

Votre responsable technique, Farid Belkacem, Lead développeur, vous a assigné un premier chantier : un client important a signalé une facturation erronée sur des réservations annulées, et le DSI de ce client a demandé un audit avant de renouveler le contrat. Vous retrouvez ce matin un e-mail de Farid dans votre messagerie professionnelle.

Votre responsable technique, Farid Belkacem, Lead développeur, vous a assigné un premier chantier : un client important a signalé une facturation erronée sur des réservations annulées, et le DSI de ce client a demandé un audit avant de renouveler le contrat. Vous retrouvez ce matin un e-mail de Farid dans votre messagerie professionnelle.

:::artefact canal=email de="Farid Belkacem" fonction="Lead développeur" objet="Mettons de l'ordre dans les tests avant le renouvellement client"
Bonjour,

Bienvenue chez Ancrelieu ! Avant toute chose : personne, ici, n'a jamais écrit de tests sur l'application de réservation. Ça nous a rattrapés cette semaine avec une erreur de facturation sur des réservations annulées, remontée par un de nos clients. Je veux qu'on répare ça proprement, avec des tests, pas juste un correctif au hasard.

Vous trouverez ci-joint la codebase ainsi qu'un plan de test que j'ai préparé pour vous cadrer.

Voici ce dont j'ai besoin :

1. Prenez la main sur l'application et dites-moi ce qui, dans le code, mérite d'être protégé en priorité par des tests — et ce qui ne vaut pas la peine à ce stade.
2. Verrouillez les règles de calcul et d'annulation : je veux qu'une modification du code me prévienne si elle casse une règle métier.
3. Vérifiez aussi que l'application fonctionne bout en bout, pas seulement morceau par morceau : qu'un client peut réserver du début à la fin sans accroc.
4. Donnez-moi une mesure de ce qui est réellement couvert par vos tests, et dites-moi ce que ce chiffre ne dit pas.
5. Le client nous a signalé une erreur de facturation sur les annulations : si vos tests la retrouvent, corrigez le code, pas le test.

Prenez le temps qu'il faut, mais documentez vos choix : je veux comprendre votre raisonnement, pas juste voir des tests verts.

Bien cordialement,

Farid Belkacem
Lead développeur

**Pièces jointes :** codebase-ancrelieu-reservation-java, codebase-ancrelieu-reservation-python, codebase-ancrelieu-reservation-dotnet, plan-de-test-ancrelieu.pdf
:::

Cette mission est entièrement guidée. Vous pouvez suivre les étapes ci-dessous.

### Étape 1 - Prenez en main l'application et ciblez vos tests

*Choisissez la version de la codebase correspondant à votre stack (Java, Python ou .NET) : les étapes qui suivent restent identiques dans leur logique, seuls les outils et la syntaxe changent.*

**Java** (Spring Boot, JUnit, Mockito) : `[À COMPLÉTER : nom exact du repo/dossier de la codebase Java]`
**Python** (PyTest) : `[À COMPLÉTER : nom exact du repo/dossier de la codebase Python]`
**.NET** (xUnit ou NUnit, WebApplicationFactory) : `[À COMPLÉTER : nom exact du repo/dossier de la codebase .NET]`

**Prérequis :**

- si vous travaillez sur la stack **Java** : avoir cloné le dépôt et installé Java (JDK) et Maven, puis exécuté l'application en local avec succès (`mvn spring-boot:run` ou équivalent) ;
- si vous travaillez sur la stack **Python** : avoir cloné le dépôt, créé un environnement virtuel (venv) et installé les dépendances via pip, puis exécuté l'application en local avec succès ;
- si vous travaillez sur la stack **.NET** : avoir cloné le dépôt et installé le SDK .NET, puis exécuté l'application en local avec succès (`dotnet run`) ;
- avoir lu le plan de test fourni par Farid, dans la version correspondant à la stack choisie ;
- avoir revu les notions de niveaux de test (unitaire, intégration, E2E) et de pyramide de tests.

**Résultats attendus :**

- une cartographie courte des couches de l'application et des règles métier qu'elle porte, en s'appuyant sur la structure propre à la stack retenue (par exemple les packages `controller` / `service` / `repository` en Java Spring Boot, les modules `routers` / `services` / `models` en Python, ou les dossiers `Controllers` / `Services` / `Repositories` en .NET) ;
- une liste priorisée de ce qui sera testé et de ce qui ne le sera pas, avec une justification par le risque métier ;
- pour chaque élément retenu, le niveau de test choisi (unitaire, intégration ou E2E), indiqué explicitement, cohérent avec le niveau de risque justifié et nommé avec le vocabulaire du framework de test de la stack choisie (JUnit et Mockito en Java, PyTest en Python, xUnit ou NUnit en .NET).

**Recommandations :**

- Ne cherchez pas à tout tester : concentrez-vous sur les règles qui ont un impact métier direct (calcul, annulation, accès aux données).
- Appuyez-vous sur le plan de test fourni, mais ne vous interdisez pas de vous en écarter si vous justifiez votre choix.
- *Java/Spring Boot :* repérez dès cette étape les couches `@Service` et `@Repository` qui portent les règles métier, elles orientent ce que vous testerez en priorité avec JUnit et Mockito.
- *Python :* identifiez les modules ou fonctions qui portent la logique métier indépendamment du framework web, c'est ce qui sera testé en priorité avec PyTest.
- *.NET :* repérez les classes de services injectées par dépendance, elles concentrent la logique métier que vous testerez avec xUnit ou NUnit.

**Points de vigilance :**

- Une priorisation absente ou non justifiée rend le reste du travail difficile à évaluer : c'est la première chose que votre mentor vérifiera.

### Étape 2 — Écrivez des tests unitaires sur les règles métier

*Stack Java : JUnit + Mockito — Stack Python : pytest + unittest.mock — Stack .NET : xUnit + Moq*

**Prérequis :**

- disposer de la liste priorisée de l'étape 1.

**Résultats attendus :**

- des tests unitaires sur les règles de calcul et d'annulation, couvrant au moins deux cas limites ;
- l'isolation des dépendances externes de ces règles par des doublures de test, avec l'outil de votre stack :
  - **Java** : Mockito ;
  - **Python** : `unittest.mock` (ou `pytest-mock`) ;
  - **.NET** : Moq ;
- un test qui échoue et met en évidence le défaut de facturation signalé par le client, propre à la codebase de votre stack [À COMPLÉTER : description du défaut planté dans la codebase Java] / [À COMPLÉTER : description du défaut planté dans la codebase Python] / [À COMPLÉTER : description du défaut planté dans la codebase .NET] ;
- la correction du défaut **dans le code applicatif**, le test initial repassant au vert après correction ;
- des tests organisés et nommés de façon à comprendre l'intention sans lire l'implémentation, avec les conventions de votre stack :
  - **Java** : JUnit, méthodes annotées `@Test` nommées par leur intention ;
  - **Python** : PyTest, fonctions `test_...` nommées par leur intention ;
  - **.NET** : xUnit (ou NUnit), méthodes `[Fact]` nommées par leur intention.

**Recommandations :**

- Écrivez d'abord le test qui décrit le comportement attendu, puis constatez son échec avant de corriger : cela prouve que le test est pertinent.
- Nommez vos tests par leur intention plutôt que par leur numéro (*doit refuser une annulation après le délai*, pas *test1*) : en Java, une méthode `doitRefuserAnnulationApresDelai()` ; en Python, une fonction `test_doit_refuser_annulation_apres_delai` ; en .NET, une méthode `DoitRefuserAnnulationApresDelai()`.
- Isolez les dépendances externes avec les doublures propres à votre stack : Mockito en Java, `unittest.mock` ou pytest-mock en Python, Moq en .NET.
- Appuyez-vous sur le framework de test de votre stack pour organiser vos suites : JUnit 5 en Java, PyTest en Python, xUnit en .NET.

**Points de vigilance :**

- Corriger le test plutôt que le code pour le faire passer est une erreur éliminatoire : l'étudiant doit pouvoir expliquer précisément où était le défaut dans le code métier, avec le vocabulaire et les mécanismes propres à sa stack (Java/Spring, Python ou .NET) — la logique de diagnostic prime sur la syntaxe.

### Étape 3 - Écrivez des tests d'intégration et un test de bout en bout, puis mesurez la couverture

*Version Java : JUnit, Spring Boot Test, Testcontainers, JaCoCo — Version Python : PyTest, pytest-cov, Selenium — Version .NET : xUnit ou NUnit, WebApplicationFactory, coverlet, Playwright.*

**Prérequis :**

- disposer des tests unitaires validés de l'étape 2.

**Résultats attendus :**

- au moins deux tests d'intégration sollicitant plusieurs couches connectées (API et persistance), sur un environnement de test isolé de la base de production/développement, avec l'outillage de votre stack :
  - **Java** : `SpringBoot Test` pour monter le contexte applicatif, `Testcontainers` pour une base de données de test réelle en conteneur ;
  - **Python** : `PyTest` avec fixtures d'intégration, `Testcontainers` pour la base de test ;
  - **.NET** : `WebApplicationFactory` couplé à `xUnit` ou `NUnit`, `Testcontainers` pour la base de test ;
- au moins un test de bout en bout (E2E) couvrant un parcours utilisateur complet (par exemple : de la recherche de disponibilité à la confirmation de réservation), avec un framework E2E adapté à votre stack (par exemple `Cypress` ou `Selenium`) ;
- un rapport de couverture de code généré et présent dans le repo, avec la commande permettant de le reproduire, produit par l'outil de couverture de votre stack :
  - **Java** : `JaCoCo` ;
  - **Python** : `pytest-cov` (ou `coverage.py`) ;
  - **.NET** : `Coverlet` ;
- une analyse écrite du rapport : zones couvertes, zones non couvertes assumées, limites de l'indicateur ;
- la confirmation, par les tests, que le défaut de facturation corrigé à l'étape 2 ne réapparaît pas au niveau intégré.

**Recommandations :**

- Utilisez une base de données de test dédiée plutôt que de dépendre d'un état partagé : Testcontainers pour une instance PostgreSQL ou MySQL éphémère en Java, un conteneur de base de données lancé via docker-compose pour l'environnement de test en Python, une `TestServer` .NET couplée à une base en mémoire ou conteneurisée pour la stack C#.
- Un taux de couverture élevé n'est pas une fin en soi : commentez ce qu'il cache (tests qui exécutent le code sans vérifier son comportement, par exemple).

**Points de vigilance :**

- Un rapport de couverture non accompagné d'une lecture critique peut motiver un refus en soutenance : l'étudiant doit être capable de dire ce que le chiffre ne prouve pas.

### Étape 4 - Sécurisez une application : comptes, accès et données

*Selon la stack choisie : Spring Security (Java), Passlib/Flask-Login ou équivalent (Python), ASP.NET Core Identity (.NET).*

**Prérequis :**

- avoir terminé les étapes 2 et 3 ;
- avoir revu les notions fondamentales de sécurité applicative (hachage, authentification, autorisation, protection des données).

**Résultats attendus :**

- des mots de passe hachés avec un algorithme adapté (BCrypt via Spring Security Crypto), jamais stockés ni journalisés en clair ;
- une authentification effective sur les points d'entrée sensibles de l'application, mise en œuvre avec Spring Security (jetons JWT ou session selon votre choix, à justifier) ;
- un contrôle d'autorisation empêchant un utilisateur d'accéder aux réservations ou données d'un autre utilisateur, implémenté via les annotations et règles d'accès de Spring Security ;
- le traitement de chaque faiblesse listée dans la note d'audit reçue de Farid, ou son écartement justifié ;
- l'absence de tout secret (mot de passe, clé, jeton) en dur dans le code versionné.

**Recommandations :**

**Recommandations :**

- reprenez la note d'audit point par point et traitez chaque élément dans l'ordre, en documentant votre avancement ;
- vérifiez que vos changements d'authentification n'ont pas cassé les tests d'intégration et E2E déjà écrits, et adaptez-les si besoin ;
- si votre stack est **Java** : appuyez-vous sur les mécanismes de hachage et de filtrage de votre framework (par exemple un encodeur de type BCrypt et une chaîne de filtres de sécurité) plutôt que d'écrire votre propre logique cryptographique ;
- si votre stack est **Python** : utilisez une bibliothèque de hachage éprouvée (par exemple bcrypt ou passlib) et le mécanisme d'authentification intégré à votre framework web plutôt qu'une implémentation maison ;
- si votre stack est **.NET** : appuyez-vous sur Asp.NET Core Identity pour le hachage des mots de passe et l'authentification plutôt que de réimplémenter ces mécanismes.

**Points de vigilance :**

- Mettre en place une authentification sans vérifier le cloisonnement des données entre utilisateurs laisse la vulnérabilité la plus critique de côté : cela peut motiver un refus en soutenance.

## Deux semaines plus tard

Vos premiers tests ont convaincu Farid : le défaut de facturation est corrigé et documenté. Le sujet devient maintenant plus sensible, car il concerne directement la confiance du client. Vous recevez un message de Farid sur le canal de l'équipe.

:::artefact canal=slack canal_nom="#dev-ancrelieu" de="Farid Belkacem"
**Farid Belkacem** [09:14]
Bon boulot sur les tests, le client a été rassuré sur la facturation 🙌

**Farid Belkacem** [09:15]
Deuxième chantier, plus sensible : l'audit du client a aussi pointé notre gestion des comptes. Je vous envoie une note avec le détail, propre à votre stack.

*(Sélectionnez le fichier correspondant à votre stack.)*

- Java : `audit-securite-ancrelieu-java.pdf`
- Python : `audit-securite-ancrelieu-python.pdf`
- .NET : `audit-securite-ancrelieu-dotnet.pdf`

**Farid Belkacem** [09:16]
En gros, il faut qu'on traite ça avant la fin du mois :
1. Les mots de passe sont stockés d'une manière que je ne veux plus voir : rendez-les illisibles, même pour nous.
2. Personne ne doit pouvoir utiliser l'application sans prouver qui il est.
3. Et surtout : un client ne doit jamais pouvoir consulter ou modifier les réservations d'un autre.
4. Reprenez un par un les points de la note d'audit et traitez-les.
5. Prouvez-moi que ces protections tiennent : je veux pouvoir le vérifier sans vous croire sur parole.
6. Écrivez dans le README de quoi expliquer au client ce qui était faible et ce que vous avez corrigé.

**Farid Belkacem** [09:17]
Pas besoin de gérer le déploiement ni la maintenance sur ce coup-là, on s'en occupera plus tard. Concentrez-vous sur le code et les preuves. Merci !

**Fichiers partagés :** audit-securite-ancrelieu-java.pdf, audit-securite-ancrelieu-python.pdf, audit-securite-ancrelieu-dotnet.pdf (ne récupérez que le fichier correspondant à votre stack)
:::

### Étape 5 - Prouvez et documentez vos corrections (Java / Spring Boot)

**Prérequis :**

- avoir terminé l'étape 4.

**Résultats attendus :**

- des tests automatisés démontrant le refus d'accès sans authentification et le refus d'accès aux données d'un autre utilisateur ;
- une note dans le **README** associant chaque vulnérabilité traitée à la protection mise en place ;
- un repo GitHub propre : historique de commits lisible, instructions claires pour exécuter les tests et consulter le rapport de couverture avec les outils de votre stack (*Maven/Gradle pour Java, pytest pour Python, dotnet test pour .NET*) ;
- la fiche d'autoévaluation complétée.

**Recommandations :**

- Rédigez la note README pour un lecteur non technique (le client final) : nommez la faiblesse en langage clair avant de nommer la protection technique.
- Chronométrez votre soutenance avant de la présenter à votre mentor.

**Points de vigilance :**

- L'absence de tests prouvant le refus d'un accès non autorisé, alors que l'autorisation est présentée comme corrigée, peut motiver un refus en soutenance : une affirmation non démontrée par un test n'est pas une preuve.

---

# Renforcez vos connaissances

La reformulation fait partie des techniques de l'apprentissage qui fonctionnent et qui permettent de renforcer vos connaissances et compétences.

Dans ce projet, vous avez appris à écrire des tests unitaires, d'intégration et de bout en bout, et à sécuriser les données et l'authentification d'une application, ce sont des compétences importantes pour votre futur métier.

Nous vous proposons donc un outil qui vous permet de travailler cette reformulation et ainsi ancrer votre apprentissage plus profondément.

Pour cela, vous allez pouvoir utiliser l'outil Companion, qui a été spécialement entraîné pour vous permettre de reformuler et d'affiner votre pensée. Cet engagement cognitif plus fort vous permettra de renforcer vos connaissances et d'être plus à l'aise en soutenance.

Quand vous serez parvenu à une formulation claire et satisfaisante des notions que vous souhaitez retravailler (par exemple : la différence entre test unitaire et test d'intégration, la lecture d'un rapport de couverture, la différence entre authentification et autorisation), faites une capture d'écran de la fin de la conversation et intégrez-la dans vos livrables.

Si des notions du projet vous semblent suffisamment claires pour ne pas nécessiter d'échange d'approfondissement, cela signifie que vous vous sentez suffisamment solide pour votre soutenance avec l'évaluateur.

Cliquez sur le bouton ci-dessous et commencez à échanger avec Companion.

:::encadre type=info
Mettre ici le lien vers le custom Companion
:::

---

# Livrables et Soutenance

## Livrables

1. **Repo GitHub de l'application testée et sécurisée, dans votre stack**, contenant au minimum : la cartographie et la priorisation des tests (étape 1), le code des tests unitaires, d'intégration et E2E, le code applicatif corrigé, le rapport de couverture de code et son analyse écrite, le code sécurisé (mots de passe hachés, authentification, autorisation), les tests démontrant les comportements de sécurité, et un **README** précisant la stack retenue, contenant la note expliquant les vulnérabilités traitées et les protections mises en place ainsi que les instructions d'exécution des tests propres à cette stack.
2. **Support de présentation** pour la soutenance, reprenant : le contexte, la démarche de test, les résultats de couverture, les vulnérabilités traitées et les protections mises en place, ainsi que ce qui reste à faire.
3. **Capture(s) d'écran de vos échanges avec Companion** montrant votre compréhension des notions du projet que vous aviez besoin de retravailler.

:::encadre type=info
Déposez sur la plateforme, dans un dossier zip nommé **Titre_du_projet_nom_prénom**, tous les livrables du projet comme suit : **Nom_Prénom_n° du livrable_nom du livrable_date de démarrage du projet**.

Cela donnera :

- Nom_Prénom_1_repo_github_mmaaaa
- Nom_Prénom_2_support_presentation_mmaaaa
- Nom_Prénom_3_captures_companion_mmaaaa

Par exemple, le premier livrable peut être nommé comme suit : Dupont_Jean_1_repo_github_012026.
:::

## Soutenance

Pendant la soutenance, l'évaluateur jouera le rôle de Farid, le lead développeur, à qui vous présentez votre travail.

**Présentation (13 minutes)**

- Vous présenterez votre démarche de priorisation des tests et les résultats obtenus sur les trois niveaux de test, en situant vos choix dans les conventions de votre stack (Java/JUnit/Mockito, Python/PyTest ou .NET/xUnit selon la version choisie).
- Vous ferez une démonstration live de l'exécution de votre suite de tests avec l'outillage propre à votre stack, y compris ceux qui prouvent le refus d'un accès non autorisé ; l'évaluateur suit l'exécution avec les commandes et la sortie attendues pour la stack que vous avez choisie.
- Vous présenterez votre rapport de couverture généré par l'outil de votre stack, ses limites, et les vulnérabilités traitées avec les protections associées.

**Discussion (10 minutes)**

- L'évaluateur, jouant le rôle de Farid, vous challengera sur vos choix, par exemple :
  - la justification de ce qui a été testé et de ce qui ne l'a pas été ;
  - la différence entre un test unitaire, un test d'intégration et un test E2E sur votre propre code, avec les outils de votre stack (JUnit et Mockito, ou PyTest, ou xUnit et WebApplicationFactory) ;
  - la lecture critique du taux de couverture obtenu et l'outil utilisé pour le produire ;
  - la différence entre authentification et autorisation, et la manière dont vous l'avez vérifiée avec les mécanismes propres à votre stack (Spring Security, ou un middleware d'authentification, ou ASP.NET Core Identity) ;
  - ce qui reste, selon vous, à sécuriser et n'a pas été traité dans ce projet.

**Débriefing pédagogique et conseil (7 minutes)**

L'évaluateur sort de son rôle pour devenir un pair expérimenté. Ce temps est dédié à la transmission :

- **Retour sur votre méthodologie** : conseils pour organiser une suite de tests de façon lisible et pour prioriser efficacement au démarrage d'un projet de tests, par exemple la manière dont vous avez justifié le choix de tester en priorité les règles de calcul et d'annulation plutôt que l'ensemble du code.
- **Utilisation de l'IA** : discussion constructive sur la manière dont vous avez utilisé l'IA (aide à la syntaxe des tests vs raisonnement de sécurité) et conseils pour une utilisation éthique et efficace à l'avenir.
- **Axes d'amélioration** : retour détaillé sur la clarté de votre présentation, la précision de votre vocabulaire technique et la pertinence de vos choix de test.
- **Conseil de carrière** (optionnel) : ce que ce type d'exercice apporte concrètement une fois en poste.

:::encadre type=info
Votre présentation devrait durer 15 minutes (+/- 5 minutes). Puisque le respect des durées des présentations est important en milieu professionnel, les présentations en dessous de 10 minutes ou au-dessus de 20 minutes peuvent être refusées.
:::

---

# biblio-api

API REST de gestion d'une petite bibliothèque (Spring Boot 3.5, Java 25, architecture MVC).

## Démarrer

```bash
./mvnw spring-boot:run
```

- Swagger UI : http://localhost:8080/swagger-ui.html
- Les données sont gardées **en mémoire** (perdues à l'arrêt).

## Structure

| Package | Rôle |
|---|---|
| `model` | Le modèle : `Livre` |
| `repository` | Accès aux données (en mémoire) |
| `service` | Logique métier |
| `controller` | Contrôleur REST (+ `LivreRequest`, corps JSON des requêtes) |
| `exception` | Exceptions et traduction en codes HTTP |

## Endpoints

| Méthode | URL | Description |
|---|---|---|
| GET | `/api/livres` | Lister les livres |
| GET | `/api/livres/{id}` | Obtenir un livre |
| POST | `/api/livres` | Ajouter un livre |
| PUT | `/api/livres/{id}` | Modifier un livre |
| DELETE | `/api/livres/{id}` | Supprimer un livre |
| POST | `/api/livres/{id}/emprunter` | Emprunter un livre |
| POST | `/api/livres/{id}/retourner` | Retourner un livre |
| GET | `/api/livres/{id}/frais-retard?joursRetard=N` | Frais de retard en $ |

---

> Remplacez le contenu ci-dessous par vos réponses du TP (voir `ENONCE_TP.md`).

---
## Partie A — Comprendre, exécuter, tester avec Swagger (semaines 2 et 3)

### A.1 Lecture de code
Parcourez les packages `model`, `repository`, `service`, `controller`, `exception`. Répondez par écrit
(2-3 lignes chacune) dans votre `README.md` :

1. Quelle classe joue le rôle du **Modèle**, du **Contrôleur** ? Où se trouve la **logique métier** ?
La classe Livre joue le rôle du modèle, LivreController celui du contrôleur et la logique métier se retrouve dans le service LivreService.

2. Que fait `GestionnaireErreurs` et quel code HTTP obtient-on pour un livre introuvable ? Et pour un double emprunt ?
GestionnaireErreurs s'assure de transformer les réponses du service en codes HTTP lisible et on obtient un code HTTP 404 NOT FOUND. Pour un double emprunt on obtient un code HTTP 409 CONFLICT.

3. Où les données sont-elles stockées ? Que se passe-t-il quand l'application redémarre ?
Les données sont stockées dans une linkedhashmap liste en mémoire et lorsque l'application redémarre toutes les données sont effacés.

4. Suivez le trajet complet d'un appel `POST /api/livres/1/emprunter` : quelles classes/méthodes sont appelées, dans quel ordre ?
Le trajet pour l'appel `POST /api/livres/1/emprunter` passe d'abord par LivreRequest, le LivreRepository, le LivreService et le LivreController.

### A.2 Exécuter
```bash
./mvnw spring-boot:run
```
Ouvrez **http://localhost:8080/swagger-ui.html**.

### A.3 Tester avec Swagger
Exécutez ces scénarios et notez le code HTTP obtenu ainsi que le résultat et explique que veut dire ce code:

| # | Appel | Résultat attendu à vérifier |
|---|---|---|
| 1 | `GET /api/livres` | Liste de 3 livres | 200 OK Une liste de 3 livres
| 2 | `POST /api/livres` avec un nouveau livre | Code 201, id attribué | Un nouveau livre créer avec son ID
| 3 | `POST /api/livres` avec un titre vide | Code 400 | Une erreur car il n'ya pas de titre
| 4 | `POST /api/livres/1/emprunter` deux fois de suite | 200 puis 409 | Le livre est disponible, alors il peut être emprunter, puis il y a une erreur, car il est déjà emprunter
| 5 | `GET /api/livres/2/frais-retard?joursRetard=100` | Notez le montant : il vous servira pour la partie C | 200 OK | 20$ - La requête à marcher les frais sont de 20$ 

Ces réponses vont dans votre `README.md` : branche `feature/lecture-code`, puis Pull Request (voir les règles de la partie 0).

---

## Partie B — Pipeline CI avec GitHub Actions (semaine 5)

Créez `.github/workflows/ci.yml` avec les exigences suivantes :

- **Déclencheurs** : `pull_request` vers `master`, `push` sur `master`, et déclenchement **manuel**.
- **Matrice** : le job de build/test s'exécute avec **JDK 21 et JDK 25** (Temurin), avec cache Maven.
- Une étape rend `mvnw` **exécutable** (le runner est Linux et le fichier vient de Windows), **avant** de l'utiliser.
- Le projet est en **Java 25** (`java.version` du `pom.xml`). La ligne JDK 21 de la matrice sert seulement à vérifier la
  **rétrocompatibilité** : pour qu'elle compile avec un JDK 21, passez la version de la matrice à Maven
  (`-Djava.version=<version de la matrice>`).
- Il exécute `./mvnw -B clean verify`.
- Il publie le **`.jar`** en artefact `application-jar` (une seule fois, depuis la version JDK 25).

**Exigence de processus** : ce fichier est introduit **par une Pull Request** vers `master` (règles de la partie 0).
Vérifiez que le workflow s'exécute sur la PR et qu'il est vert, fusionnez, puis **lancez-le manuellement**
depuis l'onglet **Actions** pour confirmer le déclencheur manuel.

À partir d'ici, **toutes** vos Pull Request sont vérifiées par ce build avant d'être fusionnées.

---

## Partie C — Tests unitaires et JaCoCo (semaines 4 et 5)

1. Étudiez le test fourni `LivreRepositoryTest` (modèle Arrange / Act / Assert).
2. Le fichier `LivreServiceTest` contient 3 **squelettes de tests** annotés `@Disabled`, avec en commentaire ce qu'il faut tester.
   **Implémentez-les**. Voici ce qui est attendu :
   - **1 test simple** (sans mock) : un calcul de frais de retard avec plafond ;
   - **1 test sur une exception** (`assertThrows`, semaine 4 §3) : un livre déjà emprunté ;
   - **1 test avec mock** (Mockito, semaine 4 §5) : isolez `LivreService` de `LivreRepository` avec un `@Mock`,
     définissez son comportement (`when(...).thenReturn(...)`) et **vérifiez les interactions**
     (`verify(...)`) — par exemple que `save` est bien appelé lors d'un emprunt.

   Nommez-les clairement (`methode_condition_resultat`) et suivez le modèle Arrange / Act / Assert.
   Mockito est déjà inclus dans `spring-boot-starter-test` : aucune dépendance à ajouter.
3. Ajoutez le plugin **JaCoCo** au `pom.xml` (voir semaine 5) et générez le rapport :
   ```bash
   ./mvnw clean verify
   # rapport : target/site/jacoco/index.html
   ```
   >  La version `0.8.12` de la note de cours **ne supporte pas Java 25**. Utilisez **0.8.14**.
   > Le rapport doit être généré pendant la phase `test`/`verify` (ajoutez l'exécution `report`).
4. Modifiez votre workflow pour qu'il publie aussi le **rapport JaCoCo** en artefact, **même si les tests échouent**.

Branche `feature/tests-jacoco`, Pull Request, build vert, fusion.

---

## Partie D — Code smells (semaine 6)

Le code contient **3 à 4 code smells** du catalogue de la semaine 6.

1. **Identifiez-les** : dans un tableau du `README.md`, indiquez pour chacun le nom du smell, le fichier, les
   lignes, et pourquoi c'est un problème.
2. **Corrigez-les** avec les réusinages de la semaine 6, **un commit par smell**.
3. Après chaque correction, relancez `./mvnw verify` : les tests doivent rester **verts**.
   (Si vous changez la signature d'une méthode, adaptez les tests concernés dans un commit à part.)

> Rappel : réusiner ne change **pas** le comportement observable de l'API.

Branche `refactor/code-smells`, Pull Request, build vert, fusion.

---

## Partie E — Docker, Codespaces, GHCR, SemVer, Release (semaines 7 et 8)

### E.1 Dockerfile
Écrivez un `Dockerfile` **« exécution seulement »** (semaine 7, Option A) :
image de base **JRE** légère, **utilisateur non-root**, port 8080 documenté, **utilise le `.jar` déjà
construit par le workflow** (aucune recompilation dans l'image). Ajoutez un `.dockerignore`.

> Attention : un `.dockerignore` qui exclut tout le dossier `target/` empêcherait `COPY target/*.jar`
> de trouver le `.jar`. Réfléchissez à ce qu'il faut exclure — et à ce qu'il faut garder.

### E.2 Tester le Dockerfile dans GitHub Codespaces
1. Ouvrez votre dépôt dans un **Codespace** (*Code → Codespaces → Create codespace*, sur votre branche de travail).
2. Construisez le `.jar` dans le Codespace avec `./mvnw -B package` (le workflow, lui, produit et réutilise ce même `.jar` automatiquement pour construire l'image lors d'un tag).
3. Construisez l'image, démarrez un conteneur en arrière-plan avec le bon mappage de ports, puis
   validez avec une vraie requête (`curl`) **et** avec Swagger via le port redirigé par Codespaces.
4. Notez dans le `README.md` les commandes utilisées.

### E.3 Variables et secrets
Le workflow a besoin du registre, de votre nom d'utilisateur et d'un jeton d'accès pour publier sur GHCR. **Aucune de ces valeurs ne doit être écrite en dur dans le YAML** : à vous de choisir ce qui doit être une variable ou un secret, de les créer et de les utiliser dans le workflow.

### E.4 Publication automatique sur GHCR avec versionnage SemVer
Modifiez le workflow (par Pull Request, comme toujours) pour qu'il se déclenche aussi sur le **push d'un tag `v*.*.*`**,
puis ajoutez, **uniquement sur un tag `vX.Y.Z`** :

- un job qui **dépend** du job de tests (`needs`), télécharge l'artefact `application-jar`,
  se connecte à GHCR avec les variables/secrets ci-dessus, **construit et pousse** l'image automatiquement ;
- des **tags d'image conformes à SemVer**, déduits du tag Git qui a déclenché le workflow : pour le tag `v1.2.3`,
  l'image est `ghcr.io/<utilisateur>/biblio-api:v1.2.3` (utilisez le contexte `github.ref_name` ;
  aucune valeur codée en dur, aucune publication hors tag). **Vous devez donc créer et pousser le tag Git avant**
  que l'image soit construite ;
- un job qui crée automatiquement une **Release GitHub** portant le **numéro du tag**, avec le `.jar` en pièce jointe
  (`permissions: contents: write` — semaine 8).

### E.5 Versionner et livrer
1. Assurez-vous que la `<version>` du `pom.xml` correspond à votre tag. Une fois la PR fusionnée,
   créez et poussez le tag **`v1.0.0`** sur `master` (les tags ne passent pas par une PR, mais ne se posent que sur un `master` vert).
2. Après vos corrections de smells (partie D), **choisissez le bon numéro** de la prochaine version
   (SemVer, semaine 8), mettez le `pom.xml` à jour (par PR), puis créez et poussez ce nouveau tag.
   Justifiez votre choix dans le `README.md` (MAJEUR, MINEUR ou CORRECTIF ? pourquoi ?).
3. Vérifiez : onglet **Packages** (images et tags), onglet **Releases**, et `docker pull` d'une des images dans Codespaces
   (connexion avec `docker login ghcr.io` et votre token).

---

## Partie F — Remise

Aucun fichier à téléverser : je (`sfostine`) dois avoir accès à votre dépôt **`TP3_NomEtudiant`**, qui contient :

- le code final, les tests, le `pom.xml` avec JaCoCo, le `Dockerfile`, le `.dockerignore`, `ci.yml` ;
- un `README.md` avec : réponses de la partie A, couverture JaCoCo (C), tableau des smells (D),
  commandes Codespaces (E.2), justification des numéros de version (E.5) ;
- les **liens** : une exécution réussie du workflow (matrice 21 et 25), vos Pull Request fusionnées
  (avec `sfostine` en reviewer et un build vert), la page **Releases**, la page **Packages** (GHCR).

---

## Barème (100 points)

| Critère | Points |
|---|---|
| A — Lecture de code et tests Swagger | 10 |
| B — Pipeline : déclencheurs, matrice 21/25, artefact jar, introduit par PR | 15 |
| C — 3 tests (1 simple, 1 exception, 1 mock) + rapport JaCoCo fonctionnel + interprétation | 15 |
| D — Smells correctement identifiés et corrigés, tests toujours verts | 15 |
| E — Dockerfile (bonnes pratiques), test Codespaces, variables/secrets, image GHCR automatique | 25 |
| E — Tag d'image = tag SemVer + Release automatique + justification des versions | 10 |
| Processus Git : aucun push direct sur `master`, branches, commits atomiques, PR avec `sfostine` en reviewer, merge seulement si build vert | 10 |



# Framework MVC — Sprint 10bis

## Rôles : `@Auth(roles = ...)` et erreur 403

Le sprint 10bis reprend le sprint 10 (`@Auth`, erreur 401) et ajoute les rôles.

```java
@Auth                                      // connecté suffit (roles vide par défaut)
@Auth(roles = "admin")                     // le rôle admin est exigé
@Auth(roles = {"admin", "manager"})        // admin OU manager
```

- `roles()` est un `String[]` de valeur par défaut `{}`. Vide : seule l'authentification est nécessaire. Sinon l'utilisateur doit avoir **au moins un** des rôles listés.
- Comme au sprint 10, l'annotation de la méthode remplace entièrement celle de la classe, rôles compris. Une méthode `@Auth` (sans rôle) dans une classe `@Auth(roles = "admin")` n'exige donc que la connexion.

### Configuration (`web.xml`)

```xml
<context-param>
    <param-name>session-user-name</param-name>
    <param-value>user</param-value>
</context-param>
<context-param>
    <param-name>session-role-name</param-name>
    <param-value>roles</param-value>
</context-param>
```

`session-role-name` est le nom de l'attribut de session qui contient les rôles. Le `FrontControllerListener` le range dans le `ServletContext` (`framework.sessionRoleName`). Il est obligatoire dès qu'une route déclare des rôles : sinon l'application refuse de démarrer.

### Vérification et erreur 403

`checkAuthorization` fait d'abord le contrôle du sprint 10 (401 si personne n'est connecté), puis :

- `roles` vide : accès autorisé ;
- sinon l'attribut de session des rôles peut contenir **un rôle** (`String`) ou **plusieurs** (`Collection`, `List`, `Set`, tableau). Le framework cherche si l'un d'eux correspond à un des rôles de l'annotation (comparaison exacte, la casse compte : `ADMIN` n'est pas `admin`). Les éléments qui ne sont pas des `String` (une `enum`, par exemple) sont comparés par leur nom ;
- aucun rôle ne correspond, ou l'attribut est absent : `handleForbidden` répond **403** (`HttpServletResponse.SC_FORBIDDEN`) et la méthode n'est pas exécutée.

Comme pour le 401, la réponse est en JSON (`@APIREST` ou `Accept: application/json`) ou en HTML :

```json
{"status":403,"error":"Forbidden","message":"Acces refuse a GET /role/api : role requis : admin"}
```

### Application de test

`/auth/login` propose maintenant un rôle : `aucun`, `admin`, `manager`, `user`, ou `admin + manager` (stocké comme **liste** dans la session ; les autres sont stockés comme `String`).

| URL | Protection | Résultat |
|---|---|---|
| `GET /auth/profil` | `@Auth` | 401 sans connexion, sinon 200 |
| `GET /auth/admin` | `@Auth(roles = "admin")` | 401 sans connexion, 403 sans le rôle admin |
| `GET /auth/gestion` | `@Auth(roles = {"admin", "manager"})` | 403 pour `user` |
| `GET /auth/api` | `@Auth` + `@APIREST` | 401 en JSON |
| `GET /espace/membres`, `/espace/messages` | `@Auth(roles = "admin")` sur la classe | 403 sans le rôle admin |
| `GET /espace/ouvert` | `@Auth` sur la méthode (prime sur la classe) | 200 pour tout connecté |

## Build et déploiement

Depuis ce dossier :

```bat
.\tomcat-start.bat
.\deploy.bat
```

L'application s'ouvre sur `http://localhost:8080/testapp/`.

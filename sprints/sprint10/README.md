# Framework MVC — Sprint 10

## Authentification : annotation `@Auth`

Le sprint 10 reprend le framework du sprint 9 (arguments, objets, upload, `MaSession`) et ajoute la protection des routes. `@Auth` se pose sur une méthode ou sur toute une classe de contrôleur (`@Target` : `TYPE` et `METHOD`).

```java
@Controller("auth")
public class AuthController {

    @UrlMapping("auth/public")            // aucune annotation : route publique
    public ModelAndView publique(MaSession session) { ... }

    @Auth                                 // il faut être connecté
    @UrlMapping("auth/profil")
    public ModelAndView profil(MaSession session) { ... }
}

@Controller("espace")
@Auth                                     // toutes les méthodes de la classe sont protégées
public class EspaceController { ... }
```

Si la méthode et la classe portent chacune `@Auth`, c'est celle de la **méthode** qui est utilisée.

### Configuration (`web.xml`)

Le développeur déclare le nom de l'attribut de session qui contient l'utilisateur connecté :

```xml
<context-param>
    <param-name>session-user-name</param-name>
    <param-value>user</param-value>
</context-param>
```

`session-log-name` est accepté comme autre nom du même paramètre. Le `FrontControllerListener` lit ce paramètre au démarrage et le range dans le `ServletContext` (`framework.sessionUserName`). Si une route porte `@Auth` et que le paramètre est absent, **l'application refuse de démarrer** (`IllegalStateException`) : on évite ainsi une route « protégée » qui serait ouverte par oubli.

### Vérification (`FrontControllerServlet`)

Avant d'appeler la méthode du contrôleur, `checkAuthorization(route, req, resp)` :

1. cherche `@Auth` sur la méthode, puis sur la classe ; sans `@Auth` la route est publique (retour `true`) ;
2. récupère la session **sans la créer** (`getSession(false)`) : un visiteur anonyme n'ouvre donc pas de session ;
3. regarde si l'attribut de session `session-user-name` existe ;
4. sinon appelle `handleUnauthorized` : statut **401** (`HttpServletResponse.SC_UNAUTHORIZED`), et la méthode du contrôleur n'est pas exécutée (retour `false`).

La réponse 401 est en **JSON** pour une méthode `@APIREST` ou quand le client demande du JSON (en-tête `Accept: application/json` sans `text/html`), et en **HTML** sinon :

```json
{"status":401,"error":"Unauthorized","message":"Authentification requise pour GET /auth/api"}
```

La page d'accueil du framework (liste des routes) reste publique.

### Application de test

`testapp` ajoute `AuthController` et `EspaceController`. Connexion de démonstration : `/auth/login` range le nom saisi dans la session sous la clé `user`.

| URL | Protection |
|---|---|
| `GET /auth/login`, `POST /auth/connecter`, `POST /auth/deconnecter`, `GET /auth/public` | aucune |
| `GET /auth/profil` | `@Auth` sur la méthode |
| `GET /auth/api` | `@Auth` + `@APIREST` (401 en JSON) |
| `GET /espace/membres`, `GET /espace/messages` | `@Auth` sur la classe |

Pour tester : ouvrir `/auth/profil` sans être connecté (erreur 401), se connecter sur `/auth/login`, puis rouvrir `/auth/profil`. Après `POST /auth/deconnecter`, la page est de nouveau refusée.

## Build et déploiement

Depuis ce dossier :

```bat
.\tomcat-start.bat
.\deploy.bat
```

L'application s'ouvre sur `http://localhost:8080/testapp/`.

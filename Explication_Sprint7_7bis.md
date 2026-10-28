# Explication détaillée — Sprint 7 et 7bis (avec les lignes concernées)

Les numéros de ligne viennent des fichiers de `sprint7.zip` et `sprint7bis.zip`. Ils peuvent bouger d'une ou deux lignes si tu modifies les imports.

## Vue d'ensemble du trajet d'une requête

```
Formulaire / URL  ->  FrontControllerServlet.processRequest
                       -> trouve la route (url + méthode HTTP)
                       -> invoke() -> buildArguments()  <== NOUVEAUTÉ des sprints 7 / 7bis
                            -> ParamUtil.convert(...)   (valeur simple)
                            -> ObjectBinder.bind(...)   (objet, 7bis)
                       -> method.invoke(controller, args)
                       -> ModelAndView (JSP) ou JSON (@APIREST)
```

---

# PARTIE 1 — SPRINT 7

## Étape 1 — Compiler avec les noms des arguments
**Fichier : `testapp/deploy.bat`, ligne 35**
```
javac -parameters -encoding UTF-8 --release 17 ...
```
Par défaut, Java efface les noms des arguments (`nom` devient `arg0`). L'option `-parameters` les garde dans le `.class`. Sans elle, `p.getName()` renverrait `arg0` et rien ne marcherait. Les lignes 27–31 préparent la liste des sources (`build\sources.txt`), les lignes 42–43 copient la webapp et `framework.jar`, la ligne 47 crée le `.war`, les lignes 53–55 le déploient dans Tomcat.

## Étape 2 — Accents des champs
**Fichier : `FrontControllerServlet.java`, ligne 61**
```java
req.setCharacterEncoding("UTF-8");
```
À faire **avant** le premier `getParameter`, sinon « é » devient « Ã© » en POST.

## Étape 3 — La requête arrive et la route est trouvée
**`FrontControllerServlet.java`**
- l.49–57 : `doGet` et `doPost` appellent `processRequest(req, resp, HttpMethod.GET/POST)`.
- l.62–63 : l'URL est construite à partir de `getServletPath()` puis normalisée.
- l.76–81 : on cherche la route avec un `UrlMethod` (url + méthode HTTP) ; si rien n'est trouvé, exception.

## Étape 4 — Appel de la méthode : `invoke`
**`FrontControllerServlet.java`, l.113–125**
- l.116 : `Object[] args = buildArguments(route, req);` ← c'est le cœur du sprint 7.
- ensuite : `newInstance()` du contrôleur, puis `method.invoke(controller, args)`.

Avant le sprint 7, on appelait la méthode sans argument. Maintenant on lui passe `args`.

## Étape 5 — Fabriquer le tableau d'arguments : `buildArguments`
**`FrontControllerServlet.java`, l.127–147**

| Ligne | Rôle |
|---|---|
| 131 | signature `buildArguments(RouteMapping route, HttpServletRequest req)` |
| 132 | `Parameter[] parameters = route.getMethod().getParameters();` lit les arguments par réflexion |
| 133 | `Object[] args = new Object[parameters.length];` tableau de même taille |
| 134–135 | boucle sur chaque argument `p` |
| 136–139 | `if (!p.isNamePresent())` : le nom n'a pas été gardé → `ServletException` « compiler avec javac -parameters » |
| 140–141 | `args[i] = ParamUtil.convert(req.getParameter(p.getName()), p.getType());` : on lit le paramètre de requête qui porte le **nom de l'argument**, et on le convertit vers son **type** |
| 142–144 | `catch (IllegalArgumentException e)` → `ServletException` « Argument "age" de … : valeur … invalide » |
| 146 | `return args;` |

Exemple : pour `save(String nom, String prenom, int age)`, la boucle fait `getParameter("nom")`, `getParameter("prenom")`, `getParameter("age")`, convertis en `String`, `String`, `int`.

## Étape 6 — Conversion : `ParamUtil.convert`
**`framework/.../util/ParamUtil.java` (66 lignes)**

| Lignes | Rôle |
|---|---|
| 15 | `convert(String raw, Class<?> type)` |
| 16–18 | type `String` : on renvoie `raw` tel quel (`null` reste `null`) |
| 19–21 | valeur absente ou vide pour un autre type → `defaultValue(type)` |
| 22 | `trim` |
| 24–44 | conversions : `int`, `long`, `double` (la virgule devient point), `float`, `short`, `boolean` (via `isTrue`), `LocalDate` |
| 45–47 | `NumberFormatException` / `DateTimeParseException` → `IllegalArgumentException` « valeur … invalide pour le type … » |
| 48 | type non géré (objet) → `IllegalArgumentException` « type d'argument non supporté » |
| 51–55 | `isTrue` : accepte true, on, 1, oui, yes |
| 57–65 | `defaultValue` : `0`, `0L`, `0.0`, `0f`, `(short)0`, `false` pour les primitifs, `null` sinon |

**Pourquoi `0` et pas `null` pour `int` ?** Java ne peut pas mettre `null` dans un `int`. Si tu veux un vrai `null`, déclare `Integer age`.

## Étape 7 — Le contrôleur de test
**`EmployeController.java` (sprint 7)**
- l.13 `@Controller("employe")`
- l.16–20 `form()` : affiche la vue `employe/form`
- l.22–30 `save(String nom, String prenom, int age)` en POST sur `employe/save` : met nom, prénom, âge dans le `ModelAndView` et affiche `employe/result`
- l.32–38 `hello(String nom)` en GET sur `employe/hello`
- l.40–48 `json(String nom, int age)` avec `@APIREST`, renvoie une `Map` en JSON

## Étape 8 — Les vues
**`WEB-INF/views/employe/form.jsp`**
- l.6 formulaire `method="post"` vers `/employe/save`
- l.7–9 champs `nom`, `prenom`, `age` : **leurs attributs `name` doivent être identiques aux noms des arguments Java**
- l.10 bouton ; l.12–13 liens de test hello / json

**`result.jsp`**
- l.2–8 fonction `h()` qui échappe le HTML
- l.13 méthode appelée ; l.15–17 nom, prénom (si non null), âge (si non null)

---

# PARTIE 2 — SPRINT 7bis

## Étape 9 — L'annotation `@Param`
**`framework/.../annotation/Param.java` (17 lignes)**
- l.12–13 `@Target(ElementType.PARAMETER)` et `@Retention(RetentionPolicy.RUNTIME)` : posée sur un argument et lisible à l'exécution
- l.14–17 `public @interface Param { String value(); }`

## Étape 10 — Distinguer simple et objet : `isSimple`
**`ParamUtil.java`, l.51–56**
```java
public static boolean isSimple(Class<?> type) {
    return type == String.class || type.isPrimitive() || type == LocalDate.class
            || type == Integer.class || ... || type == Boolean.class;
}
```
Vrai pour String, primitifs, enveloppes et `LocalDate` ; faux pour une classe métier comme `Employe`.

## Étape 11 — `buildArguments` étendu
**`FrontControllerServlet.java` (7bis), imports l.8 `Param` et l.10 `ObjectBinder`, méthode l.135–157**

| Ligne | Rôle |
|---|---|
| 141–144 | `if (!ParamUtil.isSimple(p.getType())) { args[i] = ObjectBinder.bind(p.getType(), req); continue; }` : argument objet → on le construit et on passe à l'argument suivant |
| 145 | `Param param = p.getAnnotation(Param.class);` |
| 146–149 | ni `@Param` ni nom lisible → erreur qui conseille `@Param` ou `-parameters` |
| 150 | `String name = (param != null) ? param.value() : p.getName();` : `@Param` est prioritaire, sinon le nom de l'argument |
| 151 | `ParamUtil.convert(req.getParameter(name), p.getType())` |
| 152–154 | `catch` → `ServletException` « Argument N de … » |

Les autres blocs (`@APIREST`, `ModelAndView`) sont simplement décalés de 2 lignes à cause des imports ; `invoke` est maintenant l.116–127.

## Étape 12 — Remplir un objet : `ObjectBinder`
**`framework/.../util/ObjectBinder.java` (53 lignes)**

| Lignes | Rôle |
|---|---|
| 17 | `bind(Class<?> type, HttpServletRequest req)` |
| 18 | `newInstance(type)` : crée l'objet |
| 19–20 | boucle sur la classe puis ses super-classes, sur `getDeclaredFields()` |
| 22–24 | on saute les attributs `static`, `final`, et non simples |
| 25–28 | `req.getParameter(field.getName())` ; si `null`, on laisse la valeur par défaut |
| 30–31 | `field.setAccessible(true)` puis `field.set(object, ParamUtil.convert(raw, field.getType()))` |
| 32–36 | erreurs qui nomment l'attribut fautif |
| 42–52 | `newInstance` : exige un constructeur sans argument, sinon message clair |

Conséquence : la classe `Employe` **doit** avoir un constructeur vide, et les champs du formulaire doivent porter le même nom que ses attributs.

## Étape 13 — Modèle et contrôleur de test
**`testapp/.../model/Employe.java` (15 lignes)** : attributs `nom`, `prenom`, `age` (l.5–7), constructeur vide (l.9–10), getters (l.12–14).

**`EmployeController.java` (7bis)**, imports `Employe` l.3 et `Param` l.7 :
- l.55–56 `saveParam(@Param("nom") String lastName, @Param("prenom") String firstName)` en POST sur `employe/saveParam`
- l.64–65 `saveObjet(Employe employe)` en POST sur `employe/saveObjet`
- l.74–76 `@APIREST` + `employe/objet` : `objet(Employe employe)` renvoie l'objet en JSON
- Les méthodes du sprint 7 (`form`, `save`, `hello`, `json`) sont inchangées : l.19, 25, 35, 43–45.

**`form.jsp` (27 lignes)**
- l.12–16 formulaire vers `/employe/saveParam`
- l.17–21 formulaire vers `/employe/saveObjet`
- l.22–24 liens GET (hello, json, objet)

---

# Test pas à pas
1. `tomcat-stop.bat`, puis `deploy.bat` dans le dossier du sprint, puis `tomcat-start.bat`.
2. `http://localhost:8080/testapp/employe/form` : remplir et envoyer chaque formulaire.
3. `/employe/hello?nom=Mimi` → affiche Mimi ; `/employe/hello` → `nom` null, sans erreur.
4. `/employe/json?nom=Mimi&age=30` → JSON.
5. `/employe/objet?nom=Mimi&prenom=Rakoto&age=30` → JSON de l'objet.
6. `/employe/json?nom=Mimi&age=abc` → erreur 500 qui cite l'argument `age`.

# Pièges fréquents
- Oublier `-parameters` → message « compiler avec javac -parameters » (sauf si `@Param`).
- Nom du champ HTML différent du nom de l'argument ou de l'attribut → valeur `null` / `0`.
- Pas de constructeur vide dans la classe objet → erreur de `ObjectBinder`.
- Tomcat sert l'ancienne version → supprimer `webapps\testapp` et `testapp.war`, redéployer.
- Ne pas lancer `finaliser-7.ps1` tant que les PR 7 / 7bis ne sont pas voulues.

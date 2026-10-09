# Framework MVC (equivalent Spring MVC)

Framework Java construit sprint par sprint (Servlet Jakarta 6, Tomcat 11, compilation avec `javac` et `jar`).
Chaque dossier `sprints/sprintN` contient `framework/` (le framework) et `testapp/` (l'application de test).
Le cahier des charges est dans `TODO.md`.

| Sprint | Contenu |
|---|---|
| 0 | Front controller |
| 1 | Annotation `@Controller`, `UtilAnalyser`, `ScanAnnotation` |
| 2 | `@UrlMapping`, `RouteMapping` |
| 3 | GET/POST (`UrlMethod`), invocation par reflexion |
| 4 | `FrontControllerListener` |
| 5 | `ModelAndView` |
| 6 | `@APIREST` (JSON) |
| 7 | Arguments des methodes remplis depuis le formulaire / l'URL (`ParamUtil`, `javac -parameters`) |
| 7bis | `@Param` et arguments objet (`ObjectBinder`) |

## Sprint 7 : arguments des methodes (formulaire)
- `FrontControllerServlet.buildArguments` : un argument nomme `nom` recoit le parametre de requete `nom` (champ de formulaire en POST, ou `?nom=...` en GET)
- `ParamUtil.convert` : `String`, `int`, `long`, `double`, `float`, `short`, `boolean` (et classes enveloppes), `LocalDate`
- Parametre absent ou champ vide : `null` pour un objet, `0` / `false` pour un primitif ; valeur invalide : exception qui nomme l'argument
- `UTF-8` fixe avant la lecture des champs (accents)
- Application de test compilee avec `javac -parameters` (sinon le nom des arguments est perdu)
- Test : `/employe/form`, POST `/employe/save(String nom, String prenom, int age)`, `/employe/hello?nom=Mimi`, `/employe/json?nom=Mimi&age=30`

## Sprint 7bis : `@Param` et arguments objet
- `@Param("nom")` sur un argument : choisit le parametre de requete a lire quand son nom differe de celui de l'argument (sans `-parameters` pour cet argument)
- Argument de type objet (`Employe`) : `ObjectBinder.bind` cree l'objet (constructeur sans argument) et remplit chaque attribut de type simple avec le parametre du meme nom
- `ParamUtil.isSimple` distingue type simple et objet
- Test : `/employe/form` (formulaires `@Param` et objet), `/employe/saveParam`, `/employe/saveObjet`, `/employe/objet?nom=Mimi&prenom=Rakoto&age=30` (JSON)

Build et deploiement d'un sprint, depuis son dossier : `.\deploy.bat` (Tomcat 11 installe dans `C:\apache-tomcat-11.0.23`).

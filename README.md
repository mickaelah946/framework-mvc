# Framework MVC (equivalent Spring MVC)

Framework Java construit sprint par sprint (Servlet Jakarta 6, Tomcat 11, compilation avec `javac` et `jar`).
Chaque dossier `sprints/sprintN` contient `framework/` (le framework) et ses scripts de build. L'application de test `testapp/` reste en local : elle n'est pas dans le depot.
Le cahier des charges est dans `TODO.md`.

| Sprint | Contenu |
|---|---|
| 0 | Front controller |
| 1 | Annotation `@Controller`, `UtilAnalyser`, `ScanAnnotation` |
| 2 | `@UrlMapping`, `Mapping` |
| 3 | GET/POST (`UrlMethod`), invocation par reflexion |
| 4 | `FrontControllerListener` |
| 5 | `ModelAndView` |
| 6 | `@APIREST` (JSON) |
| 7 | Arguments des methodes (formulaire), `ParamUtil` |
| 7bis | `@Param`, `ObjectBinder` (objets, objets imbriques, listes, `Set`, tableaux, plusieurs objets) |
| 8 | `MapFile`, upload multipart, configuration du dossier de stockage |
| 9 | `MaSession` (copie de `HttpSession`, synchronisee apres l'appel), gestion de session |
| 10 | `@Auth` (classe ou methode), erreur 401 (JSON ou HTML), context-param `session-user-name` |
| 10bis | `@Auth(roles = ...)`, erreur 403, context-param `session-role-name` |
| 11 | Une seule instance par controller (map `controllersInstance` dans le `ServletContext`) |

Build et deploiement d'un sprint, depuis son dossier : `.\deploy.bat` (Tomcat 11 installe dans `C:\apache-tomcat-11.0.23`). `deploy.bat` appelle aussi `testapp\deploy.bat`, donc il faut avoir le `testapp` en local. Le dossier du sprint 8 n'a pas ces scripts : seul `framework/` y est a construire (`framework\build.bat`).

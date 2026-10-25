# Framework MVC (equivalent Spring MVC)

Framework Java construit sprint par sprint (Servlet Jakarta 6, Tomcat 11, compilation avec `javac` et `jar`).
Chaque dossier `sprints/sprintN` contient `framework/` (le framework) et `testapp/` (l'application de test).
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

Build et deploiement d'un sprint, depuis son dossier : `.\deploy.bat` (Tomcat 11 installe dans `C:\apache-tomcat-11.0.23`).

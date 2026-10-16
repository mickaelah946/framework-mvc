# framework-servlet

Mini framework MVC (equivalent Spring MVC) : `framework/` produit `framework.jar` avec `javac` et `jar` (seule dependance : `jakarta.servlet-api`, fournie par Tomcat), `testapp/` est l'application de test deployee sur Tomcat 11 (Jakarta Servlet 6).

## Sprint 0 : Front controller
- `FrontControllerServlet` : `processRequest` appele par `doGet` et `doPost`
- Declare dans `web.xml` (Jakarta Servlet 6, Tomcat 11)

## Sprint 1 : Annotation @Controller
- `@Controller` : annotation a poser sur les classes controller
- `UtilAnalyser` : cherche toutes les classes d'un package (dossiers et jars), sans bibliotheque externe
- `ScanAnnotation` : garde les classes qui portent l'annotation cherchee
- `ClasseUtilitaire` : utilise les deux classes ci-dessus
- `FrontControllerServlet.init()` charge les controllers au lancement et `processRequest` les affiche

## Sprint 2 : @UrlMapping
- `@UrlMapping("emp/list")` : associe une URL a une methode d'un controller
- `ScanAnnotation.getMethodsWithAnnotation` : methodes d'une classe qui portent une annotation
- `Mapping` (classe + methode) et map url -> Mapping remplie par `ClasseUtilitaire.getRoutes`
- URL inconnue : exception listant toutes les routes disponibles

## Sprint 3 : GET / POST et invocation par reflexion
- `@UrlMapping(value = "/list", method = HttpMethod.POST)` : meme URL en GET et en POST
- `UrlMethod` (url + methode HTTP, `equals`/`hashCode`) sert de cle : map UrlMethod -> Mapping
- Sprint 3 bis : la methode du controller est invoquee par reflexion, sa valeur de retour est affichee

## Build et deploiement (sans Maven)
    .\tomcat-start.bat
    .\deploy.bat
- `framework\build.bat` : compile le framework (`javac`) et cree `framework\dist\framework.jar` (`jar`)
- `testapp\deploy.bat` : compile l'application, cree `testapp\app\testapp.war` et le copie dans Tomcat 11 (`C:\apache-tomcat-11.0.23`, modifiable avec la variable `TOMCAT_DIR`)

Puis ouvrir http://localhost:8080/testapp/

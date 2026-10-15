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

## Sprint 4 : FrontControllerListener
- `FrontControllerListener` (`ServletContextListener`) scanne les controllers et les routes une seule fois, au demarrage, et les stocke dans le `ServletContext`
- `FrontControllerServlet.init()` relit les routes dans le contexte (plus de scan dans le servlet)
- Le package des controllers se configure avec le `context-param` `controllerPackage` (web.xml)
- `url-pattern` `/` (et non `/*`)
- Une route en double (meme URL et meme methode HTTP) empeche le demarrage de l'application

## Sprint 5 : ModelAndView
- `ModelAndView` : attributs `view` et `data`, `addAttribut(cle, objet)`, constructeur avec la vue, `setView`
- `processRequest` : exception si le type de retour n'est pas un `ModelAndView`
- Les donnees du modele sont copiees dans la requete, puis `RequestDispatcher` envoie vers la JSP (`/WEB-INF/views/<vue>.jsp`)
- Test : `mvt/list` affiche une liste de mouvements ecrite en dur

## Build et deploiement (sans Maven)
    .\tomcat-start.bat
    .\deploy.bat
- `framework\build.bat` : compile le framework (`javac`) et cree `framework\dist\framework.jar` (`jar`)
- `testapp\deploy.bat` : compile l'application, cree `testapp\app\testapp.war` et le copie dans Tomcat 11 (`C:\apache-tomcat-11.0.23`, modifiable avec la variable `TOMCAT_DIR`)

Puis ouvrir http://localhost:8080/testapp/

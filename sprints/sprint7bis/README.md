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

## Sprint 6 : REST API (@APIREST)
- `@APIREST` au niveau **methode**, avec l'attribut `alreadyJson` (true/false)
- Le framework teste l'annotation sur la methode : si elle est presente, reponse `application/json`, sans `RequestDispatcher`; sinon, ancienne execution (`ModelAndView` + dispatcher)
- `alreadyJson = true` et retour `String` : la String (deja du JSON) est renvoyee telle quelle
- Autre cas : `JsonUtil.toJson` (ecrit a la main, sans bibliotheque) convertit objets, listes, maps, tableaux
- Test : `/api/hello`, `/api/info`, `/webapi/hello`, `/webapi/list` (liste d'objets)

## Sprint 7 : arguments des methodes (formulaire)
- `FrontControllerServlet.buildArguments` : un argument nomme `nom` recoit le parametre de requete `nom` (champ de formulaire en POST, ou `?nom=...` en GET)
- `ParamUtil.convert` : convertit le texte vers `String`, `int`, `long`, `double`, `float`, `short`, `boolean` (et classes enveloppes), `LocalDate`
- Parametre absent : `null` par defaut (utiliser `Integer` plutot que `int` ; un type primitif recoit `0` / `false` car Java ne peut pas lui donner `null`). Un champ de formulaire vide est envoye comme chaine vide : il donne `""` pour un `String` et `null` pour un nombre
- `processRequest` fixe `UTF-8` avant de lire les champs (accents)
- L'application est compilee avec `javac -parameters` (sans cette option, Java perd le nom des arguments : message d'erreur explicite)
- Test : `/employe/form` (formulaire nom, prenom, age) puis POST `/employe/save(String nom, String prenom, int age)` ; `/employe/hello?nom=Mimi` ; `/employe/json?nom=Mimi&age=30` (JSON)

## Sprint 7bis : @Param et arguments objet
- Annotation `@Param("nom")` (sur un argument) : choisit le parametre de requete a lire quand son nom differe de celui de l'argument ; avec `@Param`, `-parameters` n'est plus necessaire pour cet argument
- Argument de type objet (`Employe`) : `ObjectBinder.bind` cree l'objet (constructeur sans argument) et remplit chaque attribut avec le parametre de requete du meme nom (attributs de type simple ; parametre absent : valeur par defaut de l'objet)
- `ParamUtil.isSimple` : distingue un type simple (convertit depuis un parametre) d'un objet (rempli par `ObjectBinder`)
- Test : `/employe/form` (formulaires `@Param` et objet), `/employe/saveParam`, `/employe/saveObjet`, `/employe/objet?nom=Mimi&prenom=Rakoto&age=30` (objet en JSON)
- Objet dans un objet : `adresse.rue`, `adresse.ville` remplissent l'attribut `adresse` de `Employe` (prefixe = nom de l'attribut, jusqu'a 5 niveaux)
- Liste d'objets : `List<Employe> employes` est remplie avec `employes[0].nom`, `employes[1].nom`, ... (les trous d'indices sont acceptes) ; liste de types simples : `langues[0]`, `langues[1]` ou plusieurs champs `langues` de meme nom
- Deux objets dans la meme methode : `save(Employe employe, Departement departement)` lit `employe.nom` et `departement.nom` ; le prefixe est le nom de l'argument (ou `@Param`, avec `-parameters` sinon)
- `List`, `Set` et tableaux (`Employe[]`, `int[]`, `String[]`) se lisent de la meme facon, en argument comme en attribut ; une autre collection (`TreeSet`, `Queue`) donne une erreur claire
- Sans prefixe dans le formulaire (`nom`), l'attribut est rempli quand meme (comportement de base) ; des que le formulaire utilise le prefixe (`employe.nom`), seuls les champs prefixes sont lus
- Methode avec plusieurs objets : le prefixe est obligatoire, aucune valeur sans prefixe n'est partagee entre deux objets (sans `-parameters` ni `@Param`, erreur claire)
- `ParamUtil` lit aussi les `enum` (`HOMME` ou `homme`) ; valeur invalide : l'exception donne le chemin (`attribut "employe.age"`)
- Test : `/employe/form` (nouveaux formulaires), `/employe/saveImbrique`, `/employe/saveListe`, `/employe/saveDeux`, `/employe/imbrique?nom=Mimi&adresse.rue=Lalana&adresse.ville=Tana&langues[0]=fr` et `/employe/liste?employes[0].nom=A&employes[1].nom=B` (JSON)

## Build et deploiement (sans Maven)
    .\tomcat-start.bat
    .\deploy.bat
- `framework\build.bat` : compile le framework (`javac`) et cree `framework\dist\framework.jar` (`jar`)
- `testapp\deploy.bat` : compile l'application, cree `testapp\app\testapp.war` et le copie dans Tomcat 11 (`C:\apache-tomcat-11.0.23`, modifiable avec la variable `TOMCAT_DIR`)

Puis ouvrir http://localhost:8080/testapp/

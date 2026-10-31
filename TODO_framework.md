## Sprint 0 : Front controller
- creation de FrontController
- creation du fonction processRequest
- apple de process request avec doGet et doPost

## Sprint 1 : Annotation
- creation de l'annotation "Controller"
- creation des classes utils :
    - UtilAnalyser : fonction chercher tous les classes dans le projet (selon package ou non)
    - ScanAnnotation : fonction trouver tous les classes qui contient les annotations à chercher
- ajout de l'attribut classes dans FrontController
- Overide de la fonction init() pour charger tous les classes lors du lancement
- Modification de processRequest pour afficher tous les classes trouver

##  Sprint 2 : Url Mapping
- creation de l'annotation "UrlMaping"
- ajout de l'argument url dans l'annotation
- Ajout de fonction dans ScanAnnotation :
    - fonction getMethodWithAnnotation : fonction qui retourne tous les methodes d'une classe qui contient une annotation
- creation d'un classe Mapping qui contient class et method 
- ajout d'un attribut HashMap mapping dans FrontController :
    - clé : url de la map (String)
    - value : la nouvelle classe Mapping
- creation d'une methode initMapping dans FrontController pour remplir le hashMap
    - parcour des listes de classes initialiser
    - appel de la fonction getMethodWithAnnotation
    - creation des mapping 
- appel de initMapping dans init
- changement de processRequest 
    - afficher une erreur en cas de mapping introuvable et afficher tous les url disponible
    - afficher l'url, sa classe et methode correspondant si url dispo
- Refactor : creation des variables static final pour garder les class des annotation


## Sprint 3 

### Sprint 3 : suite de Sprint 2
Prise en charge des types de methode http (GET/POST)
- Creation d'une classe UrlMethode :
    - attributs :
        - url (String)
        - methode (String ou creation d'un Enum java) GET/POST
    - Override de la fonction equals pour des futurs comparaison
- Ajout d'un nouveau attribut dans l'annotation UrlMapping : method pour definir le methode http
- Changement de la clé de l'ancien map par la nouvelle classe
- Mise à jour de la fonction initMApping

### Sprint 3-Bis
- Invocation de la methode qui contient le l'url par reflexion

### Sprint 4-Special
- Creation de la classe FrontControllerListner.java :
    - implement : ServletContextListner
    - Override de la fonction ContextInitialized
    - transfert des fonctions de l'ancien init pour initialiser 
    - envoye du mapping gener par ServletContext
- Recupaeration des maps depuis le FrontController par Context


### Sprint 5-Support de vue ModelAndView
- Creation de la classe ModelAndView :
    - attributs : String view, Map<String, Object> data
    - fonction addAttribut(clé, object) pour ajouter des élements à la map
    - constructeur avec le parametre view
    - fonction setview
- Modification de ProcessRequest sur l'invocation:
    - exception si le type de retour n'est pas ModelAndView
    - caster le resultat de retour 
    - creation d'un fonction pour ajouter data du model dans le request SErvlet : addArgToRequest(req, map)
    - creation d'un dispatcher qui envoye vers la page correspondant au vu

### Sprint 6 : REST API
- On va implémenter Web API pour retourner un JSON
Objectif: 
    - Un méthode d'action dans le controlleur ne retroune pas un Vue mais renvoie un JSON
    - Pas de requestDispatcher

Comment : 
    - ajouter un annotaion @APIREST (qui va renvoyer un JSON) au niveau methode
        - Avec un attribut True-false pour voir si elle est déjà en JSON ou pas
    Coté Framework
    -Tester l'existence decette annotation au niveau de la  methode : return JSON sinon dispatcher (Content-Type: application/json)
    2 type
    - La création du JSON se crée au niveau framework
    - Du coté developpeur aussi, si le type est String mais si c'est une autre type on appel la fonction toJSON du framework


### Sprint 7 : Paramètres des méthodes de contrôleur
Objectif :
    - Quand le FrontController appelle une méthode, il lui donne les arguments venant de la requête (formulaire en POST ou ?nom=... en GET)
    - Valeur par défaut null si le paramètre n'existe pas dans la requête

Comment :
    - Lire les arguments de la méthode par réflexion (getParameters), le nom de l'argument = le nom du paramètre de la requête (compilation avec javac -parameters)
    - Même nom : on prend la valeur, on la convertit vers le type de l'argument (String, int, long, double, float, short, boolean, LocalDate) et on l'utilise dans invoke
    - Paramètre absent : null (utiliser Integer plutôt que int, un type primitif reçoit 0 / false car Java ne peut pas lui donner null)
    - Argument de type objet : exception (au sprint 7)
    - req.setCharacterEncoding("UTF-8") avant la lecture des champs
    - Test : formulaire (nom, prenom, age) vers employe/save, employe/hello?nom=Mimi, employe/json?nom=Mimi&age=30

### Sprint 7bis : @Param et arguments objet
Objectif :
    - Choisir le champ lu avec une annotation, et recevoir un objet rempli automatiquement

Comment :
    - Annotation @Param("champ") sur un argument : le framework lit le paramètre de requête "champ" (sans -parameters pour cet argument)
    - Argument de type objet : le framework crée l'objet (constructeur sans argument) et remplit chaque attribut avec le paramètre de même nom (ObjectBinder)
    - Les attributs static et final sont ignorés
    - Test : employe/saveParam (@Param), employe/saveObjet (objet Employe), employe/objet?nom=Mimi&prenom=Rakoto&age=30 (JSON)
    - Objet dans un objet : adresse.rue et adresse.ville remplissent l'attribut adresse (prefixe = nom de l'attribut, 5 niveaux au maximum)
    - Liste d'objets : List<Employe> employes avec employes[0].nom, employes[1].nom (trous d'indices acceptes) ; List, Set et tableaux se lisent de la meme facon
    - Plusieurs objets dans la meme methode : employe.nom et departement.nom, le prefixe est obligatoire (aucune valeur sans prefixe n'est partagee)
    - Test : employe/saveImbrique, employe/saveListe, employe/saveDeux, employe/imbrique (JSON), employe/liste (JSON)

### Sprint 8 : Upload de fichier
Upload fichier:

Formulaire:
Map<nom, tableau bytes>

Dans erveur:
- avoir pour que l'info enoye est un File
- File doit etre dnas la methode d'action

action(Map<String/ou extension, tableau de bytes>)

Ex:
classe modele Employe: nom, 

RectCIN tableua de bytes[]
ReCIN tableua de bytes[]

Binding:
si mitovy anarana anle attribut --> binding
avec Map

Creer objets: nom, extension

Comment avoir l'extension ?


conclusion
Creer une classe modele:
MapFile: nom du fichier, extension, bytes[]
Instancer dans le framework: si match input + rectoCIN + field de l'objet, avec un type MapFile dans le filed --> instance MapFile(nom, extension, bytes[])



Dans le web.xml on met;
```xml
<context-param>
    <param-name>upload-dir</param-name>
    <param-value>/chemin/absolu/vers/uploads</param-value>
</context-param>

<servlet>
    <servlet-name>FrontControllerServlet</servlet-name>
    <servlet-class>mg.itu.controller.FrontControllerServlet</servlet-class>
    <multipart-config>
        <max-file-size>10485760</max-file-size>        <!-- 10MB -->
        <max-request-size>52428800</max-request-size>  <!-- 50MB -->
        <file-size-threshold>1048576</file-size-threshold>
    </multipart-config>
</servlet>
```

## Sprint 9 : Session :
- Créer une classe MaSession(LcsSession) :
  - id
  - Map<String, Object> attributes
  - Set<String> initialKeys (les clés présentes au moment de la copie initiale)
  - fonction statique from(HttpSession) qui retourne une MaSession:
    - creation d'une instance de MaSession
    - remplir son id, et attributes et initialKeys par les contenu de HttpSession (httpsession.getAttributeNames().asIterator().forEachRemaining(name -> {...}))
  - fonction flushTo(HttpSession) qui va mettre à jour le contenu du vrai HttpSession :
    - boucler initalKeys et supprimer les données de httpSession qui ne sont plus dans attribute de MaSession(if (!attribute.containsKey(key)) httpsession.removeAttribute(key))
    - boucler les attribut de Masession et mettre à jour httpSession : httpSession.setAttribute(key, value) 
- Dans le FrontController :
  - avant invocation de la methode, pendant la resolution des argument :
    - verifier si le type de parametre est Masession :
      - si oui envoyer MaSession.from(request.getSession(true))
      - sinon comme avant
  - apres l'invocation de la methode, recuperer l'argument de type MaSession passé à la methode et utiliser flushTo(request.getSession(false))
- Dans MaSession ajouter les fonction :
  - getAttribute(name) pour lire un attribut coté developper
  - setAttribute(name,value)
  - removeAttribute(name)
  
## Sprint 10 : Authentification et roles
### sprint-10 Authentification
- Création de l'annotation Auth :
  - Applicable au niveau de la classe (TYPE) et de la méthode (METHOD).
- Ajout de fonction de gestions d'erreur dans frontcontroller :
  - Ajout de la fonction handleUnauthorized pour gérer les erreurs d'authentification (HTTP 401) :
    - Session inexistante ou utilisateur non authentifié.
    - status : `resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED)`,
    - Gestion des réponses selon le type de requête : erreur JSON ou HTML selon la requete
- Configuration du nom de l'attribut (clé) de l'utilisateur authentifier :
  - declarer dans un contextparam coté dev (session-user-name ou session-log-name)
  - recuperer du nom de la variable contenant l'utilisateur depuis le web.xml dans FrontControllerListener.
  - stocker de cette configuration dans le ServletContext pour la rendre accessible au FrontController.
- Vérification de l'authentification dans FrontController :
  - Création de la fonction boolean checkAuthorization pour vérifier l'authentificationanvant (true si autoriser) :
    - Récupération de l'annotation Auth sur la méthode, puis sur la classe si elle est absente
    - Priorité à l'annotation de la méthode par rapport à celle de la classe.
    - Vérification de l'existence de la session et de l'attribut de session contenant l'utilisateur.
    - Appel de handleUnauthorized si la session est null ou pas d'utilisateur authentifié dedans.
  - appler la fonction checkautorization avant invocation de la methode du controller :
    - arrêt du traitement de la requête en cas de refus d'accès, false.
    - on continue sinon

### sprint-10-bis Role 
- dans l'annotation Auth :
  - Ajout de l'attribut roles (les roles authorisé) avec une valeur par défaut {} (`String[] roles() default {}`).
  - Si le rôle est vide, seule l'authentification est nécessaire.
  - Si un rôle est précisé, l'utilisateur doit posséder ce rôle pour accéder à la ressource.
- ajout d'un nouveau fonction de gestion d'erreur dans frontcontroller :
  - Ajout de la fonction handleForbidden pour gérer les erreurs d'autorisation (HTTP 403) :
  - Utilisateur authentifié, mais ne possédant pas le rôle requis.
  - status : `HttpServletResponse.SC_FORBIDDEN`
-  Configuration du nom de l'attribut (clé) du role de l'utilisateur authentifier :
  - declarer dans un contextparam coté dev (session-role-name)
  - recuperer du nom de la variable contenant les role l'utilisateur depuis le web.xml dans FrontControllerListener.
  - stocker de cette configuration dans le ServletContext pour la rendre accessible au FrontController.
- Vérification des autorisations dans FrontController :
  - ajouter dans la fonction checkAuthorization :
    - Si le rôle requis est vide, autorisation de l'accès après authentification.
    - Si un rôle est requis, vérification de sa présence dans l'attribut de session, qui peut contenir un rôle unique (String) ou plusieurs rôles (Collection, Tableau ou list) :
      - si la session contient un role unique, on verifie si ce role match avec l'un de celui mis dans l'annotation du controller ou methode
      - si la session contient plusieurs role (collection ou liste), on boucle et verifie si un match et on continue
    - Appel de handleForbidden si l'utilisateur est authentifié, mais ne possède pas le rôle requis.

## Sprint 11 : Performance(un seule instance par controller) 
- dans frontControllerListner (à l'initialistion de l'app) :
  - creer une HashMap<Class,Objet>, class->class du controller, Object-> instance deja creer de ce controller
  - mettre cette map dans le contexte avec le nom controllersInstance
- dans frontController :
  - au lieu de creer une nouvelle instance de la avec
  
  Object controller = controllerClass.getDeclaredConstructor().newInstance();
  
  - verifier d'abord si dans cette classe n'a pas deja été initiliser et mis dans le context dt creer une instance et mettre dans la map sinon
  
  Map<Class<?>,Object> controllerList = (Map<Class<?>,Object>) req.getServletContext().getAttribute("controllersInstance");
  Object controller = controllerList.get(controllerClass);
  if (controller == null) {
      controller = controllerClass.getDeclaredConstructor().newInstance();
      controllerList.put(controllerClass, controller);
  }

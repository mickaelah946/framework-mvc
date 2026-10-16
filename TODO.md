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


### Sprint 8 : Upload de fichiers
Objectif :
    - Recevoir les fichiers d'un formulaire (enctype="multipart/form-data") dans la méthode d'action : un fichier est un objet (nom, extension, tableau de bytes)

Comment :
    - Classe modèle MapFile : nom du fichier, extension, bytes[]
    - Argument MapFile (même nom que le champ file) ou Map<String, MapFile> : tous les fichiers du formulaire (clé = nom du champ), ou Map<String, byte[]> : seulement les octets
    - Binding : si le nom d'un champ file correspond à un attribut de l'objet (ex. rectoCIN, versoCIN) et que cet attribut est de type MapFile, le framework crée MapFile(nom, extension, bytes[]) ; List<MapFile> pour plusieurs fichiers dans le même champ
    - Extension : lue dans le nom du fichier envoyé (dernier point)
    - web.xml : <multipart-config> dans le servlet (max-file-size, max-request-size, file-size-threshold) et context-param upload-dir (dossier où MapFile.save() enregistre les fichiers)
    - Test : upload/form (saveFichiers, saveObjet, saveMap, saveOctets, saveListe, info en JSON)

### Gestion de Session
### Authaurisation
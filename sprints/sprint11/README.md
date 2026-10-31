# Framework MVC — Sprint 11

## Performance : une seule instance par controller

Jusqu'au sprint 10bis, chaque requête créait un nouvel objet du controller (`getDeclaredConstructor().newInstance()`). Le sprint 11 crée l'objet une seule fois, à la première requête qui l'utilise, puis le réutilise pour toutes les suivantes.

### Comment

- Le `FrontControllerListener` crée, à l'initialisation de l'application, une map `classe du controller -> instance déjà créée` et la place dans le `ServletContext` sous le nom `controllersInstance`.
- Dans le `FrontControllerServlet`, avant d'appeler la méthode : si la classe a déjà une instance dans la map, elle est réutilisée ; sinon on la crée avec le constructeur sans argument et on la range dans la map.

```java
Object controller = controllerInstances.get(controllerClass);
if (controller == null) {
    controller = controllerClass.getDeclaredConstructor().newInstance();
    controllerInstances.put(controllerClass, controller);
}
```

Deux précisions par rapport à ce schéma :

- la map est une `ConcurrentHashMap` (une `Map` comme demandé) et l'ajout utilise `putIfAbsent` : si deux premières requêtes arrivent en même temps, une seule instance est conservée au lieu d'écraser l'autre ;
- si l'attribut `controllersInstance` est absent du contexte, le servlet crée la map lui-même.

### Attention : le controller est maintenant partagé

Comme il n'y a qu'un objet pour tous les utilisateurs, un controller ne doit **pas** garder dans ses attributs une valeur propre à une requête ou à un utilisateur (nom saisi, utilisateur connecté, liste en cours...). Les arguments des méthodes, `ModelAndView` et `MaSession` restent propres à chaque requête, ce sont eux qu'il faut utiliser. Un attribut partagé doit être sûr pour plusieurs threads (`AtomicInteger`, `ConcurrentHashMap`...).

### Application de test

`PerfController` garde un compteur dans un attribut d'instance :

`GET /perf/compteur` renvoie `{"instance":"1a2b3c","appels":1}`. En rechargeant la page, `instance` reste identique et `appels` augmente (2, 3, 4...). Avant le sprint 11, `appels` valait toujours 1 et `instance` changeait à chaque requête. Les autres controllers (`/auth/...`, `/session/...`, `/upload/...`) fonctionnent comme avant.

## Build et déploiement

Depuis ce dossier :

```bat
.\tomcat-start.bat
.\deploy.bat
```

L'application s'ouvre sur `http://localhost:8080/testapp/`.

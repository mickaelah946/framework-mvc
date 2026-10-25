# Framework MVC — Sprint 9

## Gestion de session HTTP

Le sprint 9 reprend le framework et l'application de démonstration du sprint 8, y compris l'upload multipart. Il ajoute une copie `MaSession` de la session HTTP au contrôleur. Après l'appel, le framework recopie les changements dans `HttpSession` : les attributs supprimés sont retirés et les attributs présents sont ajoutés ou mis à jour.

```java
@UrlMapping("session/compteur")
public ModelAndView compteur(MaSession session) {
    session.setAttribute("visites", 1);
    return new ModelAndView("session/result");
}
```

`MaSession.from(HttpSession)` copie l'id, les attributs et les clés initiales. `getAttribute`, `setAttribute` et `removeAttribute` manipulent cette copie ; `flushTo(HttpSession)` applique les différences. Le framework crée une session si la requête n'en possède pas encore. Les autres arguments (`@Param`, objets, fichiers et types simples) gardent leur traitement.

L'application de démonstration expose :

- `GET /session/compteur` : incrémente un compteur stocké dans la session ;
- `GET /session/etat` : relit les données de la session ;
- `POST /session/fin` : supprime l'attribut du compteur de démonstration.

## Build et déploiement

Depuis ce dossier :

```bat
.\tomcat-start.bat
.\deploy.bat
```

L'application s'ouvre sur `http://localhost:8080/testapp/`.

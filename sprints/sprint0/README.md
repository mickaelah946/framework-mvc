# framework-servlet

Mini framework MVC (equivalent Spring MVC) : `framework/` produit `framework.jar` avec `javac` et `jar` (seule dependance : `jakarta.servlet-api`, fournie par Tomcat), `testapp/` est l'application de test deployee sur Tomcat 11 (Jakarta Servlet 6).

## Sprint 0 : Front controller
- `FrontControllerServlet` : `processRequest` appele par `doGet` et `doPost`
- Declare dans `web.xml` (Jakarta Servlet 6, Tomcat 11)

## Build et deploiement (sans Maven)
    .\tomcat-start.bat
    .\deploy.bat
- `framework\build.bat` : compile le framework (`javac`) et cree `framework\dist\framework.jar` (`jar`)
- `testapp\deploy.bat` : compile l'application, cree `testapp\app\testapp.war` et le copie dans Tomcat 11 (`C:\apache-tomcat-11.0.23`, modifiable avec la variable `TOMCAT_DIR`)

Puis ouvrir http://localhost:8080/testapp/

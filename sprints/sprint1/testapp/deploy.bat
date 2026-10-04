@echo off
setlocal enabledelayedexpansion
rem Compile l'application de test, cree app\testapp.war et le copie dans Tomcat 11
cd /d "%~dp0"

set "APP_NAME=testapp"
if not defined TOMCAT_DIR set "TOMCAT_DIR=C:\apache-tomcat-11.0.23"
if not defined JAVA_HOME set "JAVA_HOME=C:\Program Files\Java\jdk-17"
if exist "%JAVA_HOME%\bin\javac.exe" set "PATH=%JAVA_HOME%\bin;%PATH%"

set "SERVLET_API_JAR=..\framework\lib\jakarta.servlet-api-6.0.0.jar"
set "FRAMEWORK_JAR=..\framework\dist\framework.jar"

if not exist "%FRAMEWORK_JAR%" (
    echo Erreur : %FRAMEWORK_JAR% introuvable. Lancer d'abord framework\build.bat
    exit /b 1
)
if not exist "%TOMCAT_DIR%\webapps" (
    echo Erreur : Tomcat introuvable dans %TOMCAT_DIR%
    exit /b 1
)

if exist build rmdir /s /q build
mkdir build\war\WEB-INF\classes
mkdir build\war\WEB-INF\lib

for /R "src\main\java" %%f in (*.java) do (
    set "src=%%f"
    set "src=!src:\=/!"
    echo "!src!">> build\sources.txt
)

if exist build\sources.txt (
    echo Compilation de l'application...
    javac -encoding UTF-8 --release 17 -cp "%SERVLET_API_JAR%;%FRAMEWORK_JAR%" -d build\war\WEB-INF\classes @build\sources.txt
    if errorlevel 1 (
        echo Erreur de compilation !
        exit /b 1
    )
)

xcopy "src\main\webapp" "build\war" /E /I /Y /Q >nul
copy /Y "%FRAMEWORK_JAR%" "build\war\WEB-INF\lib\" >nul

if not exist app mkdir app
if exist "app\%APP_NAME%.war" del /f /q "app\%APP_NAME%.war"
jar cf "app\%APP_NAME%.war" -C build\war .
if errorlevel 1 (
    echo Erreur de creation du WAR !
    exit /b 1
)

if exist "%TOMCAT_DIR%\webapps\%APP_NAME%.war" del /f /q "%TOMCAT_DIR%\webapps\%APP_NAME%.war"
if exist "%TOMCAT_DIR%\webapps\%APP_NAME%" rmdir /s /q "%TOMCAT_DIR%\webapps\%APP_NAME%"
copy /Y "app\%APP_NAME%.war" "%TOMCAT_DIR%\webapps\" >nul
if errorlevel 1 (
    echo Erreur : copie vers Tomcat refusee.
    exit /b 1
)

echo.
echo Deploye. Ouvrir http://localhost:8080/%APP_NAME%/
endlocal

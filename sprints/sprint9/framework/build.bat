@echo off
setlocal enabledelayedexpansion
rem Compile le framework avec javac et cree dist\framework.jar (sans Maven)
cd /d "%~dp0"

if not defined JAVA_HOME set "JAVA_HOME=C:\Program Files\Java\jdk-17"
if exist "%JAVA_HOME%\bin\javac.exe" set "PATH=%JAVA_HOME%\bin;%PATH%"

set "SERVLET_API_JAR=lib\jakarta.servlet-api-6.0.0.jar"
if not exist "%SERVLET_API_JAR%" (
    echo Erreur : %SERVLET_API_JAR% introuvable.
    exit /b 1
)

if exist build rmdir /s /q build
mkdir build\classes
if not exist dist mkdir dist

for /R "src\main\java" %%f in (*.java) do (
    set "src=%%f"
    set "src=!src:\=/!"
    echo "!src!">> build\sources.txt
)

echo Compilation du framework...
javac -encoding UTF-8 --release 17 -cp "%SERVLET_API_JAR%" -d build\classes @build\sources.txt
if errorlevel 1 (
    echo Erreur de compilation du framework !
    exit /b 1
)

if exist dist\framework.jar del /f /q dist\framework.jar
jar cf dist\framework.jar -C build\classes .
if errorlevel 1 (
    echo Erreur de creation du JAR !
    exit /b 1
)

echo Framework genere : framework\dist\framework.jar
endlocal

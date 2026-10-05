@echo off
rem Construit le framework puis deploie l'application de test sur Tomcat 11
cd /d "%~dp0"
call framework\build.bat
if errorlevel 1 exit /b 1
call testapp\deploy.bat

@echo off
REM ===========================================================================
REM  Script de compilation et de lancement (Windows) sans VS Code ni Maven.
REM  Nécessite uniquement un JDK 17+ accessible dans le PATH.
REM ===========================================================================
setlocal
cd /d "%~dp0"

echo [1/2] Compilation des sources Java...
if exist bin rmdir /s /q bin
mkdir bin
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
if errorlevel 1 (
    echo Echec de la compilation.
    del sources.txt
    exit /b 1
)
del sources.txt

echo [2/2] Lancement de l'application...
java -Dfile.encoding=UTF-8 -cp bin com.location.Main
endlocal

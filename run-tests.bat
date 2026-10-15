@echo off
REM ===========================================================================
REM  Compile et exécute tous les tests unitaires JUnit 5 (Windows).
REM  Nécessite un JDK 17+ et le jar JUnit présent dans lib/.
REM ===========================================================================
setlocal
cd /d "%~dp0"

set JUNIT=lib\junit-platform-console-standalone-1.10.2.jar

echo [1/3] Compilation des sources de l'application...
if exist bin rmdir /s /q bin
mkdir bin
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
if errorlevel 1 ( echo Echec compilation src. & del sources.txt & exit /b 1 )
del sources.txt

echo [2/3] Compilation des tests...
dir /s /b test\*.java > tests.txt
javac -encoding UTF-8 -cp "bin;%JUNIT%" -d bin @tests.txt
if errorlevel 1 ( echo Echec compilation test. & del tests.txt & exit /b 1 )
del tests.txt

echo [3/3] Execution des tests JUnit...
java -jar "%JUNIT%" execute -cp bin --scan-classpath --details=tree
endlocal

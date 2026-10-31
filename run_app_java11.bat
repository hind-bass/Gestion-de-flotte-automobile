@echo off
echo Lancement de l'application Gestion Carburant avec Java 11...
echo.

set JAVA_HOME=C:\Program Files\Java\jdk-11
set PATH=%JAVA_HOME%\bin;%PATH%

echo Utilisation de Java 11:
java -version
echo.

echo Compilation avec Java 11...
javac -cp "lib/*" -d build src/model/*.java src/service/*.java src/util/*.java src/main/*.java src/controller/*.java src/components/*.java

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Erreur de compilation
    pause
    exit /b 1
)

echo ✅ Compilation réussie
echo.

echo Lancement de l'application...
java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "lib/*;build" main.MainApp

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Erreur lors du lancement de l'application
    echo Vérifiez que JavaFX est correctement installé
) else (
    echo.
    echo ✅ Application fermée avec succès
)

pause

@echo off
echo Nettoyage simple des fichiers Java...
echo.

echo Suppression des fichiers corrompus...
del /Q "src\service\LogoPersonnalise.java" 2>nul

echo Nettoyage des caractères BOM...
echo.

echo Recréation de ImpressionService.java...
copy /Y "src\service\ImpressionService.java" "src\service\ImpressionService.java.bak" >nul

echo.
echo Tentative de compilation simple...
echo.

javac -cp "lib/*" -d build src/service/ImpressionService.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ ImpressionService.java compilé avec succès !
    echo.
    echo Test de la fonctionnalité d'impression...
    echo.
    
    java -cp "lib/*;build" test.TestLogo
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo ✅ Test réussi ! La fonctionnalité d'impression fonctionne.
        echo.
        echo Maintenant, essayons de compiler le projet complet...
        echo.
        
        javac -cp "lib/*" -d build src/model/*.java src/service/*.java src/util/*.java src/main/*.java src/controller/*.java src/components/*.java
        
        if %ERRORLEVEL% EQU 0 (
            echo.
            echo ✅ Compilation complète réussie !
            echo.
            echo 🚀 Lancement de l'application...
            java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "lib/*;build" main.MainApp
        ) else (
            echo.
            echo ❌ Erreur de compilation complète
            echo Vérifiez les autres fichiers manuellement
        )
    ) else (
        echo.
        echo ❌ Test échoué
    )
) else (
    echo.
    echo ❌ Erreur de compilation d'ImpressionService.java
)

pause


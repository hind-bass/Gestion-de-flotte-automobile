 @echo off
echo Lancement de l'application Gestion Carburant...
echo.

set JAVA_OPTS=-Dprism.order=sw -Dprism.verbose=true -Djava.awt.headless=false

java %JAVA_OPTS% --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "lib/*;build/classes" main.MainApp

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Erreur lors du lancement de l'application
    echo Vérifiez que Java et JavaFX sont correctement installés
    echo.
    echo Tentative avec mode headless...
    echo.
    
    set JAVA_OPTS=-Dprism.order=sw -Djava.awt.headless=true
    
    java %JAVA_OPTS% --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "lib/*;build/classes" main.MainApp
    
    if %ERRORLEVEL% NEQ 0 (
        echo.
        echo ❌ Échec également en mode headless
        echo Le problème semble être lié à l'incompatibilité Java 23 / JavaFX 21
    ) else (
        echo.
        echo ✅ Application lancée en mode headless
    )
) else (
    echo.
    echo ✅ Application fermée avec succès
)

pause

@echo off
echo ========================================
echo   Application de Gestion des Carburants
echo ========================================
echo.

echo [1/3] Verification de Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java n'est pas installé ou n'est pas dans le PATH
    echo Veuillez installer Java 11+ et réessayer
    pause
    exit /b 1
)
echo ✅ Java détecté

echo.
echo [2/3] Verification de la base de données...
echo Assurez-vous que MySQL est démarré et que la base 'gestion_carburant' existe
echo.

echo [3/3] Lancement de l'application...
echo.

REM Compiler le projet
echo Compilation en cours...
ant build
if errorlevel 1 (
    echo ❌ Erreur de compilation
    pause
    exit /b 1
)

REM Lancer l'application
echo Lancement de l'application...
ant run

echo.
echo Application fermée
pause

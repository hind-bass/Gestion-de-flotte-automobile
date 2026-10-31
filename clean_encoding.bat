@echo off
echo Nettoyage de l'encodage des fichiers Java...
echo.

echo Suppression des fichiers corrompus...
del /Q "src\service\LogoPersonnalise.java" 2>nul

echo Nettoyage des commentaires corrompus...
echo.

echo Correction de ImpressionService.java...
powershell -Command "(Get-Content 'src\service\ImpressionService.java' -Encoding UTF8) | ForEach-Object { $_ -replace 'Ôò?', '═' -replace 'ÔòÜ', '╔' -replace 'Ôò?', '╗' -replace 'Ôòæ', '║' -replace 'ÔòÜ', '╚' -replace 'Ôò?', '╝' -replace 'ÔÜá´©?', '⚠️' } | Set-Content 'src\service\ImpressionService.java' -Encoding UTF8"

echo Correction de MainApp.java...
powershell -Command "(Get-Content 'src\main\MainApp.java' -Encoding UTF8) | ForEach-Object { $_ -replace 'ÔÜá´©?', '⚠️' -replace 'Ô?î', '❌' -replace '­ƒøá´©?', '🛠️' } | Set-Content 'src\main\MainApp.java' -Encoding UTF8"

echo Correction de LoginController.java...
powershell -Command "(Get-Content 'src\controller\LoginController.java' -Encoding UTF8) | ForEach-Object { $_ -replace 'Ô?î', '❌' -replace '­ƒö?', '🔐' } | Set-Content 'src\controller\LoginController.java' -Encoding UTF8"

echo Correction de DatabaseConnection.java...
powershell -Command "(Get-Content 'src\service\DatabaseConnection.java' -Encoding UTF8) | ForEach-Object { $_ -replace 'Ô?î', '❌' } | Set-Content 'src\service\DatabaseConnection.java' -Encoding UTF8"

echo Correction de CarteCarburantService.java...
powershell -Command "(Get-Content 'src\service\CarteCarburantService.java' -Encoding UTF8) | ForEach-Object { $_ -replace 'ÔÜá´©?', '⚠️' } | Set-Content 'src\service\CarteCarburantService.java' -Encoding UTF8"

echo Correction de UtilisateurService.java...
powershell -Command "(Get-Content 'src\service\UtilisateurService.java' -Encoding UTF8) | ForEach-Object { $_ -replace '­ƒö?', '🔐' } | Set-Content 'src\service\UtilisateurService.java' -Encoding UTF8"

echo Correction de GestionVehiculesController.java...
powershell -Command "(Get-Content 'src\controller\GestionVehiculesController.java' -Encoding UTF8) | ForEach-Object { $_ -replace 'Ô£?', '✏️' -replace '­ƒö?', '🔍' } | Set-Content 'src\controller\GestionVehiculesController.java' -Encoding UTF8"

echo Correction de Ravitaillement.java...
powershell -Command "(Get-Content 'src\model\Ravitaillement.java' -Encoding UTF8) | ForEach-Object { $_ -replace '­ƒö?', '📝' } | Set-Content 'src\model\Ravitaillement.java' -Encoding UTF8"

echo.
echo ✅ Nettoyage terminé !
echo.
echo Tentative de compilation...
echo.

javac -cp "lib/*" -d build src/model/*.java src/service/*.java src/util/*.java src/main/*.java src/controller/*.java src/components/*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Compilation réussie !
    echo.
    echo Lancement de l'application...
    java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "lib/*;build" main.MainApp
) else (
    echo.
    echo ❌ Erreur de compilation
    echo Vérifiez les fichiers manuellement
)

pause

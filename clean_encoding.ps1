Write-Host "Nettoyage complet de l'encodage des fichiers Java..." -ForegroundColor Green
Write-Host ""

# Fonction pour nettoyer un fichier
function Clean-File {
    param([string]$FilePath)
    
    try {
        # Lire le contenu et supprimer le BOM
        $content = Get-Content $FilePath -Raw -Encoding UTF8
        if ($content) {
            # Supprimer le BOM et les caractères corrompus
            $cleanContent = $content -replace '^\uFEFF', '' `
                                    -replace 'Ôò?', '═' `
                                    -replace 'ÔòÜ', '╔' `
                                    -replace 'Ôò?', '╗' `
                                    -replace 'Ôòæ', '║' `
                                    -replace 'ÔòÜ', '╚' `
                                    -replace 'Ôò?', '╝' `
                                    -replace 'ÔÜá´©?', '⚠️' `
                                    -replace 'Ô?î', '❌' `
                                    -replace '­ƒøá´©?', '🛠️' `
                                    -replace '­ƒö?', '🔐' `
                                    -replace 'Ô£?', '✏️' `
                                    -replace '­ƒö?', '🔍' `
                                    -replace '­ƒö?', '📝'
            
            # Sauvegarder avec encodage UTF-8 sans BOM
            [System.IO.File]::WriteAllText($FilePath, $cleanContent, [System.Text.Encoding]::UTF8)
            Write-Host "✅ $FilePath nettoyé" -ForegroundColor Green
        }
    }
    catch {
        Write-Host "❌ Erreur lors du nettoyage de $FilePath : $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Supprimer le fichier corrompu
if (Test-Path "src\service\LogoPersonnalise.java") {
    Remove-Item "src\service\LogoPersonnalise.java" -Force
    Write-Host "🗑️ LogoPersonnalise.java supprimé" -ForegroundColor Yellow
}

# Nettoyer tous les fichiers Java
$javaFiles = @(
    "src\service\ImpressionService.java",
    "src\main\MainApp.java",
    "src\controller\LoginController.java",
    "src\service\DatabaseConnection.java",
    "src\service\CarteCarburantService.java",
    "src\service\UtilisateurService.java",
    "src\controller\GestionVehiculesController.java",
    "src\model\Ravitaillement.java"
)

foreach ($file in $javaFiles) {
    if (Test-Path $file) {
        Clean-File $file
    }
}

Write-Host ""
Write-Host "🧹 Nettoyage terminé !" -ForegroundColor Green
Write-Host ""

# Tentative de compilation
Write-Host "🔨 Tentative de compilation..." -ForegroundColor Blue
Write-Host ""

try {
    $javacResult = javac -cp "lib/*" -d build src/model/*.java src/service/*.java src/util/*.java src/main/*.java src/controller/*.java src/components/*.java 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Compilation réussie !" -ForegroundColor Green
        Write-Host ""
        Write-Host "🚀 Lancement de l'application..." -ForegroundColor Blue
        
        # Lancer l'application
        java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "lib/*;build" main.MainApp
    } else {
        Write-Host "❌ Erreur de compilation :" -ForegroundColor Red
        Write-Host $javacResult -ForegroundColor Red
    }
}
catch {
    Write-Host "❌ Erreur lors de la compilation : $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "Appuyez sur une touche pour continuer..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

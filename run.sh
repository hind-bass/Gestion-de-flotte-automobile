#!/bin/bash

echo "========================================"
echo "  Application de Gestion des Carburants"
echo "========================================"
echo

echo "[1/3] Vérification de Java..."
if ! command -v java &> /dev/null; then
    echo "❌ Java n'est pas installé ou n'est pas dans le PATH"
    echo "Veuillez installer Java 11+ et réessayer"
    exit 1
fi

java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" -lt 11 ]; then
    echo "❌ Java 11+ est requis. Version actuelle: $java_version"
    exit 1
fi

echo "✅ Java détecté (version: $(java -version 2>&1 | head -n 1 | cut -d'"' -f2))"

echo
echo "[2/3] Vérification de la base de données..."
echo "Assurez-vous que MySQL est démarré et que la base 'gestion_carburant' existe"
echo

echo "[3/3] Lancement de l'application..."
echo

# Compiler le projet
echo "Compilation en cours..."
if ! ant build; then
    echo "❌ Erreur de compilation"
    exit 1
fi

# Lancer l'application
echo "Lancement de l'application..."
ant run

echo
echo "Application fermée"

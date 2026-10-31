# 🚀 Installation Rapide - Gestion des Carburants

## ⚡ Installation en 5 minutes

### 1. **Prérequis**
- ✅ Java 11+ installé
- ✅ MySQL 8.0+ installé et démarré
- ✅ Ant (inclus avec NetBeans)

### 2. **Base de Données**
```sql
-- Connectez-vous à MySQL
mysql -u root -p

-- Créez la base
CREATE DATABASE gestion_carburant;
USE gestion_carburant;

-- Exécutez le script
source src/gestionCarburant.sql;
```

### 3. **Configuration**
Modifiez `src/service/DatabaseConnection.java` si nécessaire :
```java
private final String USERNAME = "votre_utilisateur";
private final String PASSWORD = "votre_mot_de_passe";
```

### 4. **Lancement**
```bash
# Windows
run.bat

# Linux/Mac
./run.sh
```

### 5. **Connexion**
- **Login** : `admin`
- **Mot de passe** : `admin123`

## 🔧 Dépannage

### Erreur de connexion MySQL
- Vérifiez que MySQL est démarré
- Vérifiez les identifiants dans `DatabaseConnection.java`
- Testez la connexion : `mysql -u root -p`

### Erreur Java
- Vérifiez la version : `java -version`
- Java 11+ requis

### Erreur de compilation
- Vérifiez que toutes les librairies sont dans `lib/`
- Vérifiez que Ant est installé

## 📱 Test de l'Application

1. **Connexion** : Utilisez admin/admin123
2. **Dashboard** : Vérifiez les graphiques
3. **Véhicules** : Ajoutez un véhicule de test
4. **Ravitaillement** : Créez un ravitaillement
5. **Rapports** : Consultez les statistiques

## 🆘 Support

- **Documentation complète** : `README.md`
- **Script SQL** : `src/gestionCarburant.sql`
- **Configuration** : `config.properties`

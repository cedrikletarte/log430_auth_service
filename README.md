# Guide d'exploitation


## Application BrokerX monolithique

### Déploiement via Docker

#### Présentation

BrokerX est une application monolithique de courtage en ligne (prototype MVP). L’application est packagée sous forme d’image Docker, et peut être déployée avec un docker-compose.yml qui lance : - Une base de données PosgreSQL 17 exposée sur le port 5432 - L’application Spring Boot exposée sur le port 8080

#### Prérequis
	- Docker d'installer sur la machine. (Windows utilisé docker desktop)
	- port 5432 et 8080 disponible

#### Configuration
Certaines variables doivent être définies avant le lancement (via un fichier .env) :

| Variable | Description    | Exemple |
|----------|---------------------|----------|
|SPRING_DATASOURCE_URL|URL JDBC vers PostgreSQL|jdbc:localhost://postgres:5432/|
|SPRING_DATASOURCE_USERNAME|Utilisateur DB|postgres|
|SPRING_DATASOURCE_PASSWORD|Mot de passe DB|postgres|
|JWT_SECRET|Secret pour les tokens JWT|changeme-secret|
|SPRING_MAIL_USERNAME|Compte email pour envoi OTP|brokerx.noreply@gmail.com|
|SPRING_MAIL_PASSWORD|Mot de passe email|app-password-gmail|

Ces variables sont injectées automatiquement dans le conteneur app par docker-compose.yml

##### Lancement de l'appplication localement
docker compose up --build -d

##### Accès à l'application 
	- BrokerX : http://localhost:8080


### Déploiement local (développement)

Le déploiement local permet de développer et tester l'application en utilisant PostgreSQL dans Docker tout en exécutant l'application Spring Boot directement sur la machine locale.

#### Présentation
Cette approche hybride offre les avantages suivants :
- Base de données PostgreSQL isolée dans un conteneur Docker
- Application Spring Boot exécutée localement pour un développement rapide
- Rechargement automatique des modifications de code
- Accès facile aux logs et au débogage

#### Prérequis
- Docker installé sur la machine (Windows : utiliser Docker Desktop)
- JDK 21 installé
- Apache Maven installé
- Port 5432 disponible pour PostgreSQL
- Port 8080 disponible pour l'application

#### Configuration des variables d'environnement
Compléter les variables suivantes dans le fichier /src/ressource/application.properties :

```properties
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
jwt.secret=${JWT_SECRET}
spring.mail.username=${SPRING_MAIL_USERNAME}
spring.mail.password=${SPRING_MAIL_PASSWORD}
```

#### Étapes de lancement

##### 1. Nettoyer et compiler le projet
```powershell
.\mvnw.cmd -q clean
```

##### 2. Démarrer uniquement PostgreSQL dans Docker
```powershell
docker compose up postgres -d
```

##### 4. Accès à l'application
- Application BrokerX : http://localhost:8080
- Base de données PostgreSQL : localhost:5432

#### Commandes utiles pour le développement

##### Tests
```powershell
mvn test
```

##### Rapport de couverture de code
```powershell
./mvnw jacoco:report
start .\target\site\jacoco\index.html
```

##### Arrêter PostgreSQL
```powershell
docker compose down
```

##### Redémarrer avec reconstruction complète (si nécessaire)
```powershell
docker compose up -d --build
```



# Guide de démo de l'application BrokerX

Ce guide vous permettra de tester les fonctionnalités principales de l'application BrokerX en suivant les cas d'usage typiques.

## 📋 Cas d'usage à tester

### **UC1 : Inscription d'un nouvel utilisateur**

#### Étapes :
1. **Accéder à la page d'inscription**
   - Cliquez sur le lien "Create account" ou allez à `http://localhost:8080/register`

2. **Remplir le formulaire d'inscription**
   - **Email** : `demo@exemple.com` (utilisez un email valide si vous voulez tester l'OTP)
   - **Mot de passe** : `Password123!`
   - **Prénom** : `Jean`
   - **Nom** : `Dupont`
   - **Téléphone** : `+1234567890`
   - **Date de naissance** : `1990-01-01`
   - **Adresse** : `123 Rue de la Paix`
   - **Ville** : `Montréal`
   - **Code postal** : `H1A 1A1`

3. **Soumettre l'inscription**
   - Cliquez sur "Create account"
   - **Résultat attendu** : Redirection vers la page OTP pour vérification email

4. **Vérification OTP (si email configuré)**
   - Consultez l'email reçu pour obtenir le code OTP
   - Saisissez le code dans le champ prévu
   - Cliquez sur "Verify"
   - **Résultat attendu** : Redirection vers la page de connexion

### **UC2 : Connexion utilisateur**

#### Étapes :
1. **Accéder à la page de connexion**
   - Allez à `http://localhost:8080/login`

2. **Saisir les identifiants**
   - **Email** : `demo@exemple.com`
   - **Mot de passe** : `Password123!`

3. **Se connecter**
   - Cliquez sur "Sign in"
   - **Résultat attendu** : 
     - Si OTP activé : redirection vers page OTP
     - Sinon : redirection directe vers le dashboard

4. **Vérification OTP (si applicable)**
   - Saisissez le code OTP reçu par email
   - **Résultat attendu** : Accès au dashboard

### **UC3 : Gestion du portefeuille (Wallet)**

Une fois connecté au dashboard (`http://localhost:8080/dashboard/home`) :

#### 3.1 Consulter le solde
- **Résultat attendu** : Affichage du solde actuel du portefeuille (initialement 0.00)

#### 3.2 Créditer le portefeuille
1. **Ajouter des fonds**
   - Dans la section "Wallet", trouvez le formulaire "Credit"
   - Saisissez un montant : `100.50`
   - Cliquez sur "Credit"
   - **Résultat attendu** : 
     - Message de succès "Amount added successfully"
     - Solde mis à jour : `100.50`

2. **Ajouter d'autres fonds**
   - Répétez l'opération avec `50.25`
   - **Résultat attendu** : Solde total : `150.75`

#### 3.3 Débiter le portefeuille
1. **Retirer des fonds**
   - Dans la section "Wallet", trouvez le formulaire "Debit"
   - Saisissez un montant : `25.00`
   - Cliquez sur "Debit"
   - **Résultat attendu** : 
     - Message de succès "Amount withdrawn successfully"
     - Solde mis à jour : `125.75`

2. **Tester une débit supérieur au solde** (optionnel)
   - Tentez de débiter `200.00`
   - **Résultat attendu** : Erreur ou validation côté application

### **UC4 : Déconnexion**

1. **Se déconnecter**
   - Dans le dashboard, cliquez sur "Log out"
   - **Résultat attendu** : Redirection vers la page de connexion
   - Le cookie d'authentification est supprimé

2. **Vérifier la déconnexion**
   - Tentez d'accéder directement à `http://localhost:8080/dashboard/home`
   - **Résultat attendu** : Redirection automatique vers la page de connexion


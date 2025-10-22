# Guide d'exploitation


## Micro-service d'authentification

### Déploiement via Docker

#### Présentation

Auth_Service est un micro-service d'authentification pour l'application BrokerX. L’application est packagée sous forme d’image Docker, et peut être déployée avec un docker-compose.yml qui lance : - Une base de données PosgreSQL 17 - Le micro-service Spring Boot.

#### Prérequis
	- Docker d'installer sur la machine. (Windows utilisé docker desktop)

#### Configuration
Certaines variables doivent être définies avant le lancement (via un fichier .env) :

| Variable | Description    | Exemple |
|----------|---------------------|----------|
|SPRING_DATASOURCE_USERNAME|Utilisateur DB|postgres|
|SPRING_DATASOURCE_PASSWORD|Mot de passe DB|postgres|
|JWT_SECRET|Secret pour les tokens JWT|changeme-secret|
|GATEWAY_SECRET|Secret pour les requêtes venant du gateway|changeme-secret|
|SPRING_MAIL_USERNAME|Compte email pour envoi OTP|brokerx.noreply@gmail.com|
|SPRING_MAIL_PASSWORD|Mot de passe email|app-password-gmail|

Ces variables sont injectées automatiquement dans le conteneur app par docker-compose.yml

##### Lancement de l'appplication localement
docker compose up --build -d

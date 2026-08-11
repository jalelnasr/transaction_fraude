# Plateforme Intelligente de Détection de Fraude Bancaire en Temps Réel

Projet de Fin d'Année (PFA) — ESPRIT, réalisé dans le cadre d'un stage à la **Banque Centrale de Tunisie (BCT)**.

L'objectif est de concevoir et développer une plateforme événementielle capable de détecter des transactions bancaires frauduleuses **en temps réel**, en combinant un moteur de règles métier, un modèle de Machine Learning (Random Forest) et un moteur d'explicabilité (SHAP), avec une chaîne DevSecOps complète (conteneurisation, observabilité, authentification centralisée).

---

## Sommaire

- [Architecture](#architecture)
- [Stack technique](#stack-technique)
- [Structure du dépôt](#structure-du-dépôt)
- [Flux de traitement d'une transaction](#flux-de-traitement-dune-transaction)
- [Démarrage rapide](#démarrage-rapide)
- [Services et ports](#services-et-ports)
- [Modèle Machine Learning](#modèle-machine-learning)
- [Observabilité](#observabilité)
- [Authentification](#authentification)
- [Frontend](#frontend)
- [Roadmap](#roadmap)

---

## Architecture

L'architecture est **microservices, orientée événements (event-driven)**, communiquant via **Apache Kafka**. Chaque service a une responsabilité unique et publie/consomme des événements sur des topics dédiés.

```
                         ┌──────────────────┐
                         │   Angular SPA     │
                         │  (Dashboard BCT)  │
                         └────────┬──────────┘
                                  │ JWT (Keycloak)
                                  ▼
                         ┌──────────────────┐
                         │   API Gateway     │  (Spring Cloud Gateway)
                         └────────┬──────────┘
                                  │
        ┌─────────────┬──────────┼───────────┬─────────────────┐
        ▼             ▼          ▼           ▼                 ▼
 transaction-   rule-engine  ml-engine  fraud-decision-   notification-
   service                   (Python)      engine            service
        │             │          │           │                 │
        └─────────────┴──────────┴─────┬─────┴─────────────────┘
                                        │  Kafka topics
                     transactions · rule-scores · ml-scores
                     decisions · explanations · audit-events
                                        │
                         ┌──────────────┼──────────────┐
                         ▼              ▼               ▼
                explainability-    audit-service   (WebSocket →
                    engine                            frontend)
```

**Principe de scoring** : quand une transaction arrive sur le topic `transactions`, elle est évaluée **en parallèle** par le `rule-engine` (règles métier déterministes) et le `ml-engine` (modèle prédictif). Le `fraud-decision-engine` corrèle les deux scores dès qu'ils arrivent (avec timeout / mode dégradé si l'un des deux tarde) et calcule un score final :

```
score_final = 0.4 × score_règles + 0.6 × score_ML
```

- `score_final < 0.5` → transaction **ACCEPTED**
- `0.5 ≤ score_final < 0.85` → transaction **MONITORED** (mise sous surveillance, alerte créée)
- `score_final ≥ 0.85` → transaction **BLOCKED** (bloquée automatiquement)

Un analyste peut ensuite traiter les alertes générées (Valider / Lever / Escalader) depuis le dashboard, ce qui met à jour le statut réel de la transaction via un aller-retour synchronisé entre `notification-service` et `transaction-service`.

---

## Stack technique

| Domaine | Technologies |
|---|---|
| Backend | Java 17, Spring Boot 3.2.5, Spring Cloud Gateway (WebFlux) |
| ML | Python, FastAPI, scikit-learn (Random Forest), SHAP |
| Messagerie événementielle | Apache Kafka (KRaft mode) |
| Base de données | PostgreSQL 16 |
| Frontend | Angular 21 (standalone components, signals) |
| Authentification | Keycloak 24 (OAuth2 / JWT) |
| Observabilité | Prometheus, Grafana, Loki + Promtail |
| Conteneurisation | Docker, Docker Compose |
| Build | Maven (multi-module reactor) |

---

## Structure du dépôt

```
fraud-detection-platform/
├── backend/
│   ├── common-lib/               # DTOs, enums, AuditLogger partagés
│   ├── transaction-service/      # Réception & persistance des transactions (8081)
│   ├── rule-engine/              # Règles métier (montant, pays à risque, vélocité, horaire) (8082)
│   ├── fraud-decision-engine/    # Fusion des scores + décision finale (8084)
│   ├── explainability-engine/    # Génération d'explications textuelles (SHAP → texte FR) (8085)
│   ├── notification-service/     # Gestion des alertes + WebSocket (8086)
│   ├── audit-service/            # Journal d'audit (piste de conformité) (8087)
│   └── api-gateway/              # Point d'entrée unique, sécurité JWT (8080)
├── ml-engine/                    # Service Python FastAPI de scoring ML (8083)
│   ├── app/                      # API, consumer Kafka, feature engineering, SHAP
│   ├── training/                 # Script d'entraînement du modèle
│   └── data/models/              # Modèle entraîné (fraud_model_v1.pkl)
├── frontend/                     # Application Angular (dashboard analyste)
├── infra/monitoring/             # Config Prometheus, Grafana, Loki, Promtail
├── docker-compose.yml            # Orchestration complète (17 conteneurs)
└── pom.xml                       # POM parent (agrégateur Maven)
```

---

## Flux de traitement d'une transaction

1. **Création** — `POST /api/transactions` sur `transaction-service` → transaction persistée (statut `RECEIVED`) et publiée sur le topic Kafka `transactions`.
2. **Scoring parallèle** :
   - `rule-engine` consomme `transactions`, applique 4 règles (montant élevé, pays à risque, fréquence anormale, heure creuse), publie sur `rule-scores`.
   - `ml-engine` consomme `transactions`, calcule un score de fraude via le modèle Random Forest + génère les features influentes via SHAP, publie sur `ml-scores`.
3. **Corrélation & décision** — `fraud-decision-engine` écoute les deux topics, apparie les scores par `transactionId`, fusionne (40 % règles / 60 % ML), applique les seuils, publie la décision finale sur `decisions`.
4. **Synchronisation du statut** — `transaction-service` consomme `decisions` et met à jour le statut réel de la transaction (`ACCEPTED` / `MONITORED` / `BLOCKED`).
5. **Explication** — `explainability-engine` consomme `decisions` (sauf les `ACCEPTED`) et génère une justification textuelle en français à partir des règles déclenchées et des features ML influentes.
6. **Alerte** — `notification-service` crée une alerte pour les décisions `MONITORED`/`BLOCKED`, notifie le dashboard en temps réel via WebSocket. Un analyste peut ensuite **Valider**, **Lever** ou **Escalader** l'alerte, ce qui répercute le changement sur le statut réel de la transaction.
7. **Audit** — chaque étape clé publie un événement sur `audit-events`, consommé par `audit-service` pour constituer une piste d'audit complète (conformité réglementaire).

---

## Démarrage rapide

### Prérequis
- Docker Desktop
- (Optionnel, pour développement local hors conteneurs) Java 17, Maven, Python 3.11+, Node.js 20+

### Lancer toute la plateforme

```bash
docker compose up -d
```

Cela démarre 17 conteneurs : infrastructure (PostgreSQL, Kafka, Keycloak, Prometheus, Loki, Promtail, Grafana, Kafka UI) + les 8 microservices applicatifs.

> Les images `fraud/*:latest` doivent être construites au préalable pour chaque service :
> ```bash
> docker compose build
> ```

### Tester le pipeline de bout en bout

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
        "amount": 6500.00,
        "currency": "TND",
        "senderAccount": "ACC-001",
        "receiverAccount": "ACC-002",
        "channel": "CARTE",
        "country": "TN"
      }'
```

Suivre la décision :

```bash
curl http://localhost:8080/api/transactions/{transactionId}/decision
```

### Frontend

```bash
cd frontend
npm install
npm start
```

Application disponible sur `http://localhost:4200`.

---

## Services et ports

| Service | Port | Rôle |
|---|---|---|
| api-gateway | 8080 | Point d'entrée unique (routing + JWT) |
| transaction-service | 8081 | CRUD transactions |
| rule-engine | 8082 | Scoring par règles métier |
| ml-engine | 8083 | Scoring ML (FastAPI, Python) |
| fraud-decision-engine | 8084 | Fusion des scores, décision finale |
| explainability-engine | 8085 | Explications textuelles |
| notification-service | 8086 | Alertes + WebSocket |
| audit-service | 8087 | Journal d'audit |
| Kafka UI | 8090 | Inspection des topics Kafka |
| Keycloak | 8180 | Authentification (realm `fraud-detection`) |
| PostgreSQL | 5432 | Base de données |
| Kafka | 9092 | Broker de messages |
| Prometheus | 9090 | Métriques |
| Grafana | 3000 | Dashboards (admin/admin) |
| Loki | 3100 | Agrégation de logs |
| Frontend (dev) | 4200 | Dashboard analyste (Angular) |

---

## Modèle Machine Learning

- **Dataset** : PaySim (simulateur de transactions mobiles), échantillonné (toutes les fraudes + 100k transactions non-frauduleuses).
- **Modèle** : Random Forest (scikit-learn `Pipeline` + `ColumnTransformer`).
- **Feature engineering** : extraction de l'heure de la transaction (`step` → heure du jour → indicateur nuit), features de montant et de solde **avant** transaction uniquement.
- **Point d'attention méthodologique** : les features de solde **après** transaction ont été volontairement exclues du modèle. Elles créaient une fuite de données (data leakage) — artefact du simulateur PaySim qui met systématiquement le solde après transaction à zéro pour les transactions frauduleuses, ce qui aurait rendu le modèle trivialement précis (ROC AUC ≈ 0.9999) mais inutilisable en production, où ce solde n'est pas encore connu au moment du scoring.
- **Explicabilité** : SHAP (`TreeExplainer`) calcule les features les plus influentes pour chaque prédiction, restituées à l'utilisateur sous forme de justification en français.
- Réentraînement : `python ml-engine/training/train_model.py` (nécessite le dataset brut, non versionné — voir `.gitignore`).

---

## Observabilité

- **Prometheus** scrape les endpoints `/actuator/prometheus` des 8 microservices toutes les 10s.
- **Grafana** (`http://localhost:3000`, admin/admin) expose un dashboard provisionné automatiquement (« Fraud Detection Platform – Vue d'ensemble ») : services actifs, statut par service, requêtes HTTP/s, erreurs 5xx, mémoire JVM, latence moyenne.
- **Loki + Promtail** collectent les logs de **tous les conteneurs Docker** via `docker_sd_configs` (découverte automatique du socket Docker), consultables dans Grafana → Explore avec des requêtes LogQL, ex. `{container="fraud-transaction-service"}`.

---

## Authentification

- **Keycloak** (realm `fraud-detection`) gère les utilisateurs et émet des tokens JWT.
- L'`api-gateway` valide les tokens via `jwk-set-uri` (vérification de signature uniquement, sans contrainte stricte sur l'`issuer`, ce qui permet aux tokens émis via `localhost:8180` côté navigateur d'être validés par le gateway qui interroge Keycloak via son nom de conteneur interne `keycloak:8080`).
- Le frontend Angular effectue un login `password grant`, décode le JWT et propage le token (header `Authorization: Bearer`) sur chaque appel API.

---

## Frontend

Application Angular 21 (standalone components, signals) :
- **Dashboard** — vue d'ensemble (cartes cliquables, alertes récentes, recherche dynamique).
- **Transactions** — liste + détail (statut, score, décision, explication).
- **Alertes** — traitement des alertes par l'analyste (Valider / Lever / Escalader).
- **Règles** — consultation des règles métier actives.
- Identité visuelle BCT (logo, charte graphique dédiée après authentification).

---

## Roadmap

- [x] Architecture événementielle (Kafka) + microservices Spring Boot
- [x] Moteur de règles métier
- [x] Moteur ML (Random Forest + SHAP) avec correction de la fuite de données
- [x] Fusion des scores & moteur de décision
- [x] Explicabilité des décisions
- [x] Alerting temps réel + workflow analyste
- [x] Authentification centralisée (Keycloak)
- [x] Frontend Angular complet
- [x] Conteneurisation Docker de tous les services
- [x] Observabilité (Prometheus, Grafana, Loki)
- [ ] Gestion des secrets (Vault)
- [ ] CI/CD (Jenkins, SonarQube)
- [ ] Déploiement Kubernetes

---

## Auteur

**Jalel Nasr** — ESPRIT, PFA effectué à la Banque Centrale de Tunisie (BCT).

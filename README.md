# Quarkus K3s PoC

Una Proof of Concept di un servizio REST Quarkus con deployment automatico su K3s.

## 🚀 Features

- **REST API** completa con operazioni CRUD
- **OpenAPI/Swagger** documentation integrata
- **Health checks** per Kubernetes (liveness, readiness, startup)
- **Container image** build automatico con Jib (no Docker daemon)
- **Kubernetes manifests** generati automaticamente
- **K3s ready** con supporto Traefik Ingress

## 📁 Struttura del Progetto

```
quarkus-k3s-poc/
├── pom.xml                          # Maven config con estensioni Quarkus
├── deploy.sh                        # Script di deployment automatico
├── src/
│   └── main/
│       ├── java/com/example/
│       │   ├── model/
│       │   │   └── Item.java        # Modello dati
│       │   ├── service/
│       │   │   └── ItemService.java # Business logic
│       │   ├── resource/
│       │   │   ├── ItemResource.java    # REST endpoints CRUD
│       │   │   └── InfoResource.java    # Info endpoint
│       │   └── health/
│       │       └── ItemServiceHealthCheck.java
│       └── resources/
│           └── application.properties   # Configurazione Quarkus + K8s
└── k8s/
    ├── ingress.yaml                 # Traefik Ingress per K3s
    └── configmap.yaml               # ConfigMap e Secrets
```

## 🛠️ Prerequisites

- **Java 21+**
- **Maven 3.9+**
- **K3s** installato e configurato
- **kubectl** configurato per il cluster k3s

## ⚡ Quick Start

### 1. Sviluppo Locale

```bash
# Avvia in modalità dev (hot reload)
./mvnw quarkus:dev

# L'applicazione sarà disponibile su http://localhost:8080
```

### 2. Build dell'Applicazione

```bash
# Build con generazione manifesti Kubernetes e immagine container
./mvnw clean package

# I manifesti K8s saranno in: target/kubernetes/kubernetes.yml
```

### 3. Deploy su K3s

#### Opzione A: Script Automatico

```bash
# Rendi eseguibile lo script
chmod +x deploy.sh

# Deploy base
./deploy.sh

# Deploy con push dell'immagine
PUSH_IMAGE=true ./deploy.sh

# Deploy con Ingress
ENABLE_INGRESS=true ./deploy.sh

# Deploy completo
PUSH_IMAGE=true ENABLE_INGRESS=true ./deploy.sh
```

#### Opzione B: Manuale

```bash
# 1. Build
./mvnw clean package -DskipTests

# 2. (Opzionale) Push immagine al registry
./mvnw package -Dquarkus.container-image.push=true

# 3. Apply dei manifesti
kubectl apply -f target/kubernetes/kubernetes.yml

# 4. (Opzionale) Apply Ingress
kubectl apply -f k8s/ingress.yaml

# 5. Verifica
kubectl get pods -l app.kubernetes.io/name=quarkus-k3s-poc
```

## 📡 Endpoints API

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| GET | `/api/items` | Lista tutti gli items |
| GET | `/api/items/{id}` | Ottiene un item per ID |
| POST | `/api/items` | Crea un nuovo item |
| PUT | `/api/items/{id}` | Aggiorna un item |
| DELETE | `/api/items/{id}` | Elimina un item |
| GET | `/api/items/count` | Conta gli items |
| GET | `/api/info` | Info applicazione |
| GET | `/api/info/env` | Info ambiente |

### Health Endpoints (MicroProfile Health)

| Endpoint | Descrizione |
|----------|-------------|
| `/q/health` | Health check completo |
| `/q/health/live` | Liveness probe |
| `/q/health/ready` | Readiness probe |
| `/q/health/started` | Startup probe |

### Documentation

| Endpoint | Descrizione |
|----------|-------------|
| `/q/swagger-ui` | Swagger UI |
| `/q/openapi` | OpenAPI spec (JSON/YAML) |

## 🧪 Test Locale

```bash
# Port forward (se non usi Ingress)
kubectl port-forward svc/quarkus-k3s-poc 8080:8080

# Test endpoints
curl http://localhost:8080/api/items
curl http://localhost:8080/api/info
curl http://localhost:8080/q/health
```

## 🐳 Uso con K3s Local Registry

Se usi il registry locale di k3s:

```bash
# 1. Abilita il registry in k3s (k3d)
k3d cluster create mycluster --registry-create mycluster-registry:5000

# 2. Configura l'immagine per il registry locale
./mvnw package \
  -Dquarkus.container-image.registry=localhost:5000 \
  -Dquarkus.container-image.push=true

# 3. Aggiorna il manifest se necessario
# L'immagine sarà: localhost:5000/mycompany/quarkus-k3s-poc:1.0.0
```

## ⚙️ Configurazione

### Variabili d'Ambiente per il Deploy

| Variabile | Default | Descrizione |
|-----------|---------|-------------|
| `NAMESPACE` | default | Namespace Kubernetes |
| `REGISTRY` | docker.io | Container registry |
| `IMAGE_GROUP` | mycompany | Gruppo/organizzazione |
| `VERSION` | 1.0.0 | Versione dell'immagine |
| `PUSH_IMAGE` | false | Push dell'immagine |
| `ENABLE_INGRESS` | false | Abilita Ingress |

### Personalizzazione application.properties

```properties
# Cambia registry
quarkus.container-image.registry=ghcr.io
quarkus.container-image.group=myorg

# Cambia replicas
quarkus.kubernetes.replicas=3

# Abilita Ingress generato da Quarkus
quarkus.kubernetes.ingress.expose=true
quarkus.kubernetes.ingress.host=myapp.example.com
```

## 📦 Estensioni Quarkus Utilizzate

| Estensione | Scopo |
|------------|-------|
| `quarkus-rest` | REST endpoints (JAX-RS) |
| `quarkus-rest-jackson` | JSON serialization |
| `quarkus-kubernetes` | Genera manifesti K8s |
| `quarkus-container-image-jib` | Build immagini container |
| `quarkus-smallrye-health` | Health checks |
| `quarkus-smallrye-openapi` | OpenAPI/Swagger |

## 🔧 Comandi Utili

```bash
# Lista estensioni disponibili
./mvnw quarkus:list-extensions

# Aggiungi una nuova estensione
./mvnw quarkus:add-extension -Dextensions="hibernate-orm-panache"

# Build nativo (richiede GraalVM)
./mvnw package -Pnative

# Genera solo i manifesti K8s (senza build)
./mvnw package -Dquarkus.kubernetes.deploy=false
```

## 📝 Note

- L'immagine container viene creata con **Jib**, quindi non richiede Docker daemon
- I manifesti Kubernetes sono generati in `target/kubernetes/`
- K3s usa **Traefik** come Ingress controller di default
- Per la compilazione nativa, è necessario GraalVM 21+

## 🤝 Prossimi Passi

1. Aggiungere persistenza con database (PostgreSQL + Hibernate)
2. Implementare autenticazione (Keycloak/OIDC)
3. Aggiungere metriche (Micrometer + Prometheus)
4. Configurare CI/CD pipeline (GitHub Actions / GitLab CI)

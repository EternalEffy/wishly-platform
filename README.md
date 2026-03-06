# 🎁 Wishly Platform

Multi-service platform for Notes, Wishlists, and Authentication.

![Java](https://img.shields.io/badge/Java-21-blue?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-green?style=for-the-badge&logo=spring-boot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-7-red?style=for-the-badge&logo=redis)
![MinIO](https://img.shields.io/badge/MinIO-S3-red?style=for-the-badge&logo=amazon-s3)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

---

## 🏗 Architecture

```mermaid
graph LR
    G[🌐 Gateway<br/>Port 8080] --> NS[📝 Notes Service<br/>Port 8082]
    G --> AS[🔐 Auth Service<br/>Port 8083]
    G --> HS[🔑 Hash Generator<br/>Port 8081]
    G --> WS[🎁 Wishlist Service<br/>Port 8084]
    NS --> DB[(🗄️ PostgreSQL<br/>metadata)]
    NS --> MO[(📦 MinIO<br/>content)]
    AS --> AUTHDB[(🗄️ PostgreSQL<br/>authdb)]
    HS --> RD[(🔴 Redis<br/>hash pool)]
    WS --> WDB[(🗄️ PostgreSQL<br/>wishlistdb)]
    G --> RDG[(🔴 Redis<br/>rate limiting)]
    
    style G fill:#4CAF50,stroke:#333,stroke-width:2px,color:white
    style NS fill:#2196F3,stroke:#333,stroke-width:2px,color:white
    style HS fill:#FF9800,stroke:#333,stroke-width:2px,color:white
    style AS fill:#9C27B0,stroke:#333,stroke-width:2px,color:white
    style WS fill:#E91E63,stroke:#333,stroke-width:2px,color:white
    style AUTHDB fill:#9C27B0,stroke:#333,stroke-width:2px,color:white
    style DB fill:#2196F3,stroke:#333,stroke-width:2px,color:white
    style MO fill:#FF5722,stroke:#333,stroke-width:2px,color:white
    style RD fill:#F44336,stroke:#333,stroke-width:2px,color:white
    style WDB fill:#E91E63,stroke:#333,stroke-width:2px,color:white
    style RDG fill:#F44336,stroke:#333,stroke-width:2px,color:white
```
### Storage Strategy

| Data Type | Storage | Rationale |
|-----------|---------|-----------|
| **Metadata** (hash, blobKey, expiresAt) | PostgreSQL | Fast queries, indexes, TTL |
| **Content** (paste text) | MinIO (S3) | Scalable blob storage |
| **Rate Limiting** (pre-generated IDs) | Redis | ~1ms retrieval time |
| **Hash Pool** (Gateway) | Redis | Token bucket algorithm |
| **Wishlist Data** | PostgreSQL | Direct queries (low-traffic use case) |

---

## 🔐 Authentication

### Overview

The system uses JWT-based authentication with access and refresh tokens.

| Token | Lifetime | Storage | Purpose |
|-------|----------|---------|---------|
| Access Token | 15 minutes | Client memory | API authorization |
| Refresh Token | 7 days | HttpOnly cookie / DB | Get new access token |

### Security

- Passwords hashed with BCrypt (cost factor 12)
- Refresh tokens hashed with SHA-256 before DB storage
- Token rotation on refresh (old token revoked)
- Token revocation on logout

### Example Usage

```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"Password123"}'

# 2. Use access token
curl http://localhost:8080/api/notes/my \
  -H "Authorization: Bearer <ACCESS_TOKEN>"

# 3. Refresh token
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<REFRESH_TOKEN>"}'
```

## 👥 User Flow

### Authentication & Paste Operations

```mermaid
sequenceDiagram
    participant Client as "🖥️ Client"
    participant Gateway as "🌐 Gateway"
    participant Services as "⚙️ Services"
    participant DB as "💾 Databases"

    Client->>Gateway: 1. Login / API Request
    Gateway->>Gateway: Validate JWT
    Gateway->>Services: Add X-User-Id
    Services->>DB: Query
    DB-->>Services: Response
    Services-->>Gateway: Process Result
    Gateway-->>Client: 3. Return Response
```
### Share Note (Public)
```mermaid
flowchart LR
    subgraph Owner["👤 Owner"]
        A["Create Note<br/>(authenticated)"] --> B["Share Link"]
    end
    
    subgraph Viewer["👁️ Viewer"]
        C["View Note<br/>(no auth)"]
    end
    
    B -->|"https://.../abc123"| C
    
    style A fill:#4CAF50,color:white,stroke:#333,stroke-width:2px
    style B fill:#2196F3,color:white,stroke:#333,stroke-width:2px
    style C fill:#FF9800,color:white,stroke:#333,stroke-width:2px
```

## 📦 Modules

| Module | Port | Description |
|--------|------|-------------|
| **wishly-gateway** | 8080 | API Gateway - routing, rate limiting |
| **wishly-auth-service** | 8083 | JWT authentication (register, login, refresh, logout) |
| **wishly-notes-service** | 8082 | Notes CRUD operations with MinIO storage |
| **wishly-wishlist-service** | 8084 | Wishlist & Gift management (in development) |
| **wishly-hash-generator-service** | 8081 | Hash generation with Redis |
| **wishly-common** | — | Shared DTOs, utils, exceptions |

---

## 🔌 API Endpoints

### Auth Service (Port 8083)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/auth/register` | No | Register new user |
| `POST` | `/api/auth/login` | No | Login and get JWT tokens |
| `POST` | `/api/auth/refresh` | No | Refresh access token |
| `POST` | `/api/auth/logout` | Yes | Logout and revoke tokens |

### Gateway (Port 8080)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/notes` | Yes | Create new note |
| `GET` | `/api/notes/{hash}` | Optional | Get note by hash |
| `GET` | `/api/notes/my` | Yes | Get your notes |
| `DELETE` | `/api/notes/{hash}` | Yes | Delete note (owner only) |
| `GET` | `/api/hash?length=8` | Yes | Get unique hash from Redis |
| `POST` | `/api/wishlists` | Yes | Create wishlist |
| `GET` | `/api/wishlists/{id}` | Optional | Get wishlist |
| `GET` | `/api/wishlists/my` | Yes | Get your notes |
| `PUT` | `/api/wishlists/{id}` | Yes | Update wishlist |
| `DELETE` | `/api/wishlists/{id}` | Yes | Delete wishlist |
| `POST` | `/api/wishlists/{id}/items` | Yes | Add gift item |
| `GET` | `/api/wishlists/{id}/items` | Optional | Get wishlist items |
| `PUT` | `/api/wishlists/{id}/items/{itemId}` | Yes | Update gift item |
| `DELETE` | `/api/wishlists/{id}/items/{itemId}` | Yes | Delete gift item |
| `POST` | `/api/wishlists/{id}/items/{itemId}/reserve` | Yes | Reserve gift |
| `DELETE` | `/api/wishlists/{id}/items/{itemId}/reserve` | Yes | Cancel reservation |

### Notes Service (Port 8082)

| Method | Endpoint | Description 
|--------|----------|-------------
| `GET` | `/api/notes/my` | Get your pastes |
| `POST` | `/api/notes` | Create new paste |
| `GET` | `/api/notes/{hash}` | Get paste by hash |
| `DELETE` | `/api/notes/{hash}` | Delete paste |

### Hash Generator Service (Port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/hash?length=8` | Get unique hash from Redis pool |

**Example:**
``` bash
curl http://localhost:8081/api/hash?length=8
# Response: cHjj6PzH
```

### Wishlist Service (Port 8084)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/wishlists` | Create new wishlist |
| `GET` | `/api/wishlists/my` | Get your wishlists |
| `GET` | `/api/wishlists/{id}` | Get wishlist by ID |
| `PUT` | `/api/wishlists/{id}` | Update wishlist |
| `DELETE` | `/api/wishlists/{id}` | Delete wishlist |
| `POST` | `/api/wishlists/{id}/items` | Add gift item (URL optional) |
| `GET` | `/api/wishlists/{id}/items` | Get all gift items |
| `PUT` | `/api/wishlists/{id}/items/{itemId}` | Update gift item |
| `DELETE` | `/api/wishlists/{id}/items/{itemId}` | Delete gift item |
| `POST` | `/api/wishlists/{id}/items/{itemId}/reserve` | Reserve gift (7-day TTL) |
| `DELETE` | `/api/wishlists/{id}/items/{itemId}/reserve` | Cancel reservation |

### Health Check

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/health` | Service health status |


## 🚀 Quick Start

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker & Docker Compose

### 1. Clone Repository

```bash
git clone https://github.com/EternalEffy/wishly-platform.git
cd wishly-platform
```
### 2. Build Project
```bash
mvn clean install
```
### 3. Start Infrastructure
```bash
docker-compose up -d
```
### 4. Run E2E Tests
```bash
./test-wishlist.sh
```
## 🎁 Wishlist Service

### Features

- **Wishlist CRUD** - Create, read, update, delete, archive wishlists
- **GiftItem Management** - Add gifts with optional URL, name, description, priority
- **Reservation System** - Reserve gifts with 7-day automatic expiration
- **Privacy Settings** - PUBLIC, PRIVATE, SHARED visibility
- **URL Validation** - Format validation (http/https + host check)

### GiftItem Structure

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | String | ✅ Yes | Gift name (user enters manually) |
| `productUrl` | String | ❌ Optional | Product URL (max 2048 chars) |
| `description` | String | ❌ Optional | Gift description (max 1000 chars) |
| `priority` | Enum | ❌ Optional | LOW, MEDIUM, HIGH (default: MEDIUM) |
| `reserved` | Boolean | Auto | Reservation status |
| `reservedByName` | String | Auto | Guest name who reserved |
| `reservedByEmail` | String | Auto | Guest email who reserved |
| `reservedAt` | DateTime | Auto | Reservation timestamp |

### Reservation Flow

```mermaid
sequenceDiagram
    participant G as "👤 Guest"
    participant GW as "🌐 Gateway"
    participant WS as "🎁 Wishlist Service"
    participant DB as "💾 PostgreSQL"

    G->>GW: POST /reserve (name, email)
    GW->>WS: Forward with X-User-Id
    WS->>DB: Check if already reserved
    DB-->>WS: Available
    WS->>DB: Create reservation (7-day TTL)
    DB-->>WS: Success
    WS-->>GW: Reservation confirmed
    GW-->>G: 200 OK + expiresAt
```

## 🔴 Redis Usage

### Hash Pool (Hash Generator Service)

| Parameter | Value | Description |
|-----------|-------|-------------|
| **Pool Key** | `hash:pool` | Redis List storing hashes |
| **Threshold** | 100 | Refill when pool < 100 |
| **Batch Size** | 1000 | Hashes per refill |
| **Refill Interval** | 5s | Background job frequency |
| **Hash Length** | 8 | Characters per hash |

**Benefits:**
- ⚡ Fast hash generation (~1ms vs ~50ms for on-demand)
- 🔄 Automatic pool replenishment

### Rate Limiting (Gateway)

| Parameter | Value | Description |
|-----------|-------|-------------|
| **Algorithm** | Token Bucket | Smooth rate limiting |
| **Default Limit** | 100 req/min | Per IP address |
| **Storage** | Redis | Distributed rate limiting |

**Benefits:**
- 🛡️ API protection from abuse
- 📊 Fair usage across clients
---

## 📦 MinIO Blob Storage

Paste content is stored in MinIO (S3-compatible object storage):

| Parameter | Value | Description |
|-----------|-------|-------------|
| **Bucket** | `notes` | All paste content |
| **Key Format** | `notes/{hash}` | Unique key per paste |
| **Content Type** | `text/plain` | Plain text storage |
| **Console** | `http://localhost:9001` | Web UI for browsing |

**Benefits:**
- 📈 Scalable content storage (~1M+ pastes)
- 💾 Separation from metadata (PostgreSQL)
- 🔄 Easy migration to AWS S3 if needed

---

## 📦 Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.2.0 |
| **Database** | PostgreSQL 15 |
| **Cache** | Redis 7 |
| **Blob Storage** | MinIO (S3-compatible) |
| **Build Tool** | Maven |
| **Architecture** | Microservices (REST) |
| **Containerization** | Docker Compose |

---

## 📱 Mobile Application

Android application available in separate repository:
- 👉 [github.com/EternalEffy/wishly-android](https://github.com/EternalEffy/wishly-android)

**Features:**
- JWT Authentication (Login/Register)
- Create & Manage Notes
- Wishlist Management
- QR Code Sharing

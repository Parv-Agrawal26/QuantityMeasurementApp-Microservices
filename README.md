# Quantity Measurement App — Microservices

A Spring Boot microservices application for performing unit conversions and arithmetic operations on physical quantities (length, weight, volume, temperature), secured with JWT authentication and Google OAuth2.

---

## Architecture

```
Client
  │
  ▼
API Gateway (port 8080)
  ├──▶ Auth Service      (port 8081)  →  authdb    (MySQL)
  └──▶ Measurement Service (port 8082) →  measurementdb (MySQL)
```

| Service             | Port | Description                                      |
|---------------------|------|--------------------------------------------------|
| api-gateway         | 8080 | Routes all requests, handles CORS                |
| auth-service        | 8081 | Registration, login, logout, Google OAuth2, JWT  |
| measurement-service | 8082 | Unit conversion, comparison, arithmetic, history |

---

## Tech Stack

- Java 21
- Spring Boot 3.2.0
- Spring Cloud Gateway 2023.0.0
- Spring Security + JWT (Auth0 `java-jwt` 4.4.0)
- Spring OAuth2 Client (Google)
- Spring Data JPA + MySQL 8
- SpringDoc OpenAPI / Swagger UI 2.3.0
- Maven

---

## Prerequisites

- JDK 21
- Maven 3.8+
- MySQL 8 running locally on port `3306`

Create the two databases:

```sql
CREATE DATABASE authdb;
CREATE DATABASE measurementdb;
```

---

## Configuration

### Auth Service — `auth-service/src/main/resources/application.properties`

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/authdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
jwt.secret=MySuperSecretKeyForJwt
jwt.expiration-ms=86400000

# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=<your-client-id>
spring.security.oauth2.client.registration.google.client-secret=<your-client-secret>
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/login/oauth2/code/google
```

### Measurement Service — `measurement-service/src/main/resources/application.properties`

```properties
server.port=8082
spring.datasource.url=jdbc:mysql://localhost:3306/measurementdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
jwt.secret=MySuperSecretKeyForJwt   # must match auth-service
jwt.expiration-ms=86400000
```

> The `jwt.secret` must be identical in both services — the measurement service validates tokens issued by the auth service.

---

## Running the Application

Start each service in order:

```bash
# 1. API Gateway
cd api-gateway
mvn spring-boot:run

# 2. Auth Service
cd auth-service
mvn spring-boot:run

# 3. Measurement Service
cd measurement-service
mvn spring-boot:run
```

---

## API Reference

All requests go through the API Gateway at `http://localhost:8080`.

### Auth Service — `/api/v1/auth`

| Method | Endpoint              | Auth Required | Description              |
|--------|-----------------------|---------------|--------------------------|
| POST   | `/api/v1/auth/register` | No          | Register a new user      |
| POST   | `/api/v1/auth/login`    | No          | Login and receive JWT    |
| POST   | `/api/v1/auth/logout`   | Yes (Bearer)| Invalidate JWT token     |

**Register request body:**
```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Login request body:**
```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

**Login response:**
```json
{
  "token": "<jwt-token>",
  "username": "john"
}
```

**Google OAuth2 login:**  
Navigate to `http://localhost:8080/oauth2/authorization/google` — on success, a JWT is returned via the OAuth2 success handler.

---

### Measurement Service — `/api/v1/quantities`

All endpoints require `Authorization: Bearer <jwt-token>`.

| Method | Endpoint                          | Description                          |
|--------|-----------------------------------|--------------------------------------|
| POST   | `/api/v1/quantities/compare`      | Compare two quantities               |
| POST   | `/api/v1/quantities/convert`      | Convert a quantity to another unit   |
| POST   | `/api/v1/quantities/add`          | Add two quantities                   |
| POST   | `/api/v1/quantities/subtract`     | Subtract two quantities              |
| POST   | `/api/v1/quantities/divide`       | Divide two quantities                |
| GET    | `/api/v1/quantities/history`      | Get all operation history            |
| GET    | `/api/v1/quantities/history/{op}` | Get history filtered by operation    |
| GET    | `/api/v1/quantities/count/{op}`   | Count successful operations by type  |

**Request body (binary operations):**
```json
{
  "value1": 1.0,
  "unit1": "FEET",
  "value2": 12.0,
  "unit2": "INCHES"
}
```

**Request body (convert / single-value operations):**
```json
{
  "value1": 100.0,
  "unit1": "CELSIUS",
  "unit2": "FAHRENHEIT"
}
```

---

## Supported Units

| Category    | Units                                      | Arithmetic |
|-------------|--------------------------------------------|------------|
| Length      | `FEET`, `INCHES`, `YARDS`, `CENTIMETERS`   | ✅          |
| Weight      | `KILOGRAM`, `GRAM`, `POUND`                | ✅          |
| Volume      | `LITRE`, `MILLILITRE`, `GALLON`            | ✅          |
| Temperature | `CELSIUS`, `FAHRENHEIT`                    | ❌ (convert only) |

---

## Swagger UI

| Service             | URL                                              |
|---------------------|--------------------------------------------------|
| Auth Service        | http://localhost:8081/swagger-ui.html            |
| Measurement Service | http://localhost:8082/swagger-ui.html            |

---

## Project Structure

```
QuantityMeasurementApp-Microservices/
├── api-gateway/
│   └── src/main/resources/application.yml
├── auth-service/
│   └── src/main/java/com/apps/authservice/
│       ├── controller/    # AuthController
│       ├── dto/           # AuthRequestDTO, AuthResponseDTO, RegisterRequestDTO
│       ├── entity/        # UserEntity
│       ├── repository/    # UserRepository
│       ├── security/      # JwtUtil, JwtAuthenticationFilter, SecurityConfig, OAuth2SuccessHandler
│       └── service/       # IAuthService, AuthServiceImpl, CustomUserDetailsService
└── measurement-service/
    └── src/main/java/com/apps/measurementservice/
        ├── controller/    # QuantityMeasurementController
        ├── dto/           # QuantityInputDTO, QuantityDTO
        ├── entity/        # QuantityMeasurementEntity
        ├── repository/    # QuantityMeasurementRepository
        ├── security/      # JwtUtil, JwtAuthenticationFilter, SecurityConfig
        ├── service/       # IQuantityMeasurementService, QuantityMeasurementServiceImpl
        └── unit/          # IMeasurable, LengthUnit, WeightUnit, VolumeUnit, TemperatureUnit
```

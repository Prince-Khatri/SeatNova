# SeatNova

SeatNova is a movie ticket booking platform built as a set of Spring Boot microservices. It handles movie catalog management, theatre/screen/show management, seat booking with distributed locking, and payments via Razorpay — all wired together through service discovery and asynchronous messaging.

## Architecture

The system is split into five independently deployable Spring Boot services, registered with a central Eureka discovery server:

| Service | Port | Responsibility |
|---|---|---|
| **eureka** | `9000` | Service registry / discovery server (Netflix Eureka) |
| **movie-service** | `8081` | Movie catalog: create, update, search, and filter movies, genres, and languages |
| **theatre-service** | `8080` | Theatres, screens, seats, and shows |
| **booking-service** | `8082` | Seat reservation, seat locking, booking lifecycle |
| **payment-service** | `8085` | Payment orders, refunds, and Razorpay webhook handling |

### How the pieces fit together

- **Service discovery** — every service registers itself with **eureka** on startup and looks up peer services through it instead of hardcoded URLs.
- **Booking flow** — `booking-service` validates the theatre/seat/show via `theatre-service` and the user via a (currently stubbed) `UserValidationService`, then places a short-lived hold on the requested seats in **Redis** (via a Lua script, `hold-seat.lua`) before persisting the booking.
- **Async messaging** — `booking-service` and `payment-service` communicate over **RabbitMQ**. Booking events (created/expired/confirmed/released) and payment events (succeeded/failed/refunded) are published and consumed asynchronously to keep booking and payment state in sync.
- **Payments** — `payment-service` creates orders and processes refunds through **Razorpay**, and exposes a webhook endpoint to receive asynchronous payment confirmations.
- **Persistence** — each domain service (movie, theatre, booking, payment) owns its own **PostgreSQL** schema.

## Tech stack

- **Java 17**, **Spring Boot 4.1.0** (Spring Cloud `2025.1.2`)
- Spring Web (MVC), Spring Data JPA, Spring WebFlux (`WebClient` for service-to-service calls)
- Spring Cloud Netflix **Eureka** (service discovery)
- **PostgreSQL** (primary datastore, one schema per service)
- **Redis** (seat holds / distributed locking in `booking-service`)
- **RabbitMQ** (event-driven communication between `booking-service` and `payment-service`)
- **Razorpay** (payment gateway integration in `payment-service`)
- **Lombok**, **Jakarta Validation**
- **Maven** (each service has its own `mvnw` wrapper)
- **Bruno** API client collections for manual API testing (`/bruno`)

## Project structure

```
seatnova/
├── eureka/            # Service discovery server
├── movie-service/      # Movie catalog service
├── theatre-service/     # Theatre, screen, and show management
├── booking-service/     # Seat booking and locking
├── payment-service/     # Payments and Razorpay integration
└── bruno/              # Bruno API collections for testing each service
```

Each service follows a standard layered Spring Boot layout: `controller` → `service` (interface + `impl`) → `repository` → `entity`, with request/response objects under `dto`.

## Prerequisites

- Java 17+
- Maven (or use the bundled `./mvnw` wrapper in each service)
- PostgreSQL instance (one schema per service)
- Redis instance (used by `booking-service`)
- RabbitMQ instance (used by `booking-service` and `payment-service`)
- A Razorpay account/API keys (for `payment-service`)

## Configuration

Each service reads its configuration from environment variables via `application.properties`. Typical variables you'll need to set:

**Common (movie-service, theatre-service, booking-service, payment-service)**
```
DB_URL=jdbc:postgresql://<host>:5432/<database>
DB_SCHEMA=<schema_name>
DB_USERNAME=<username>
DB_PASSWORD=<password>
EUREKA_DEFAULT_ZONE=http://localhost:9000/eureka
```

**booking-service and payment-service (RabbitMQ)**
```
RABBIT_MQ_HOST=<host>
RABBIT_MQ_PORT=<port>
RABBIT_MQ_USERNAME=<username>
RABBIT_MQ_PASSWORD=<password>
RABBIT_MQ_VHOST=<vhost>
```

**booking-service (Redis)**
```
REDIS_HOST=<host>
REDIS_PORT=<port>
REDIS_USERNAME=<username>
REDIS_PASSWORD=<password>
```

**payment-service (Razorpay)**
```
RAZORPAY_KEY_ID=<key_id>
RAZORPAY_KEY_SECRET=<key_secret>
RAZORPAY_WEBHOOK_SECRET=<webhook_secret>
```

`theatre-service` and `payment-service` support loading these from a local `.env` file (see `theatre-service/.env.example` for the expected format); `theatre-service` and `payment-service` set `spring.config.import=optional:file:.env[.properties]` so a `.env` file dropped in the service root is picked up automatically.

## Running locally

Start services in this order so that dependent services can register and discover each other:

```bash
# 1. Start the discovery server
cd eureka && ./mvnw spring-boot:run

# 2. Start the domain services (in any order, once eureka is up)
cd movie-service && ./mvnw spring-boot:run
cd theatre-service && ./mvnw spring-boot:run
cd booking-service && ./mvnw spring-boot:run
cd payment-service && ./mvnw spring-boot:run
```

Each service needs its environment variables set (see [Configuration](#configuration)) before starting. Once `eureka` is running, you can view registered services at `http://localhost:9000`.

## API overview

### movie-service (`/movies`)
- `POST /movies` — create a movie
- `GET /movies` — list movies (filter by `status`, `language`, `genre`)
- `GET /movies/{id}` — get a movie
- `GET /movies/search?title=` — search movies by title
- `PUT /movies/{id}` — update a movie
- `PATCH /movies/{id}/status?status=` — update movie status
- `DELETE /movies/{id}` — delete a movie

### theatre-service
- `POST/GET/PUT/DELETE /api/v1/theatres` — theatre CRUD
- `POST /api/v1/screens`, `GET /api/v1/screens/{id}` — screen management
- `POST /api/v1/screens/{screenId}/seats`, `GET /api/v1/screens/{screenId}/seats` — seat management
- `GET /api/v1/screens/{seatId}/validate` — validate a seat
- `POST /api/v1/shows`, `GET /api/v1/shows`, `GET /api/v1/shows/{id}` — show management
- `GET /api/v1/shows/movie/{movieId}`, `GET /api/v1/shows/city/{city}` — filtered show lookups
- `GET /api/v1/shows/{id}/validate`, `PUT /api/v1/shows/{id}/cancel` — show validation / cancellation

### booking-service (`/bookings`)
- `POST /bookings` — reserve seats for a show
- `GET /bookings/{id}` — get a booking
- `GET /bookings/user/{userId}` — get a user's bookings
- `PATCH /bookings/{id}` — cancel a booking

### payment-service (`/payments`)
- `POST /payments/order` — create a payment order
- `GET /payments/{bookingId}` — get payment status for a booking
- `POST /payments/refund` — issue a refund
- `POST /payments/webhook` — Razorpay webhook receiver (verifies `X-Razorpay-Signature`)

Full request/response examples for every endpoint are available as ready-to-run [Bruno](https://www.usebruno.com/) collections under `/bruno`.

## Testing

Each service ships with its own Spring Boot test scaffold and can be run independently:

```bash
cd <service-name> && ./mvnw test
```

## License

This project is licensed under the MIT License — see [LICENSE](./LICENSE) for details.

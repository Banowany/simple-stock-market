# Simple Stock Market

## Overview

Simple Stock Market is a backend service that simulates a simplified stock exchange.

The system allows users to buy and sell stocks, while a central bank manages stock availability. All operations are executed immediately at a fixed price (equals 1), without order matching or price fluctuations. For simplicity, users are assumed to have unlimited funds, and transactions are constrained only by stock availability.

The application is designed as a distributed system with multiple stateless instances behind a reverse proxy and a shared database. It focuses on correctness, concurrency handling, and high availability under partial failures.

---

## Running the Application

### Requirements

* Docker
* Docker Compose

---

### Start

Run the following command:

```bash
APP_PORT=XXXX docker compose up --build
```

Replace `XXXX` with any available port on your machine.

---

### Access

After startup, the application will be available at:

```
http://localhost:XXXX
```

---

### Startup Time

Initial startup may take **1–2 minutes**, as it includes:

* building the application image
* starting the database cluster
* initializing the cluster
* starting application instances
* starting the reverse proxy

---

### Notes

* All services are started automatically using Docker Compose
* The system ensures proper startup order using health checks
* No additional setup or configuration is required

---

## Running Tests

The entire test environment is fully containerized using Docker Compose.

### Run tests

```bash
docker compose -f docker-compose-test.yml up --build --abort-on-container-exit
```

### How it works

* The command builds the application and test containers
* A dedicated test container executes all tests against a real database
* Other services (e.g. database) are started automatically

### Behavior

* When the test container finishes (success or failure), all other containers from this Compose setup are stopped automatically
* This applies only to containers defined in `docker-compose-test.yml`
* Containers from other Docker Compose files or projects are NOT affected

### Notes

* Tests run against a real distributed database environment (CockroachDB cluster)
* No additional setup is required
* The environment is fully reproducible via Docker

---

## Design Decisions

### Stateless Application Layer

The application is designed as a set of stateless instances. No state is stored in memory — all data is persisted in the database.

This allows:

* horizontal scaling
* resilience to instance failures
* simple recovery after crashes

---

### Database as Single Source of Truth

All system state (bank, wallets, audit log) is stored in a distributed database.

This ensures:

* consistency across instances
* durability of data
* application instances do not need to communicate with each other directly
---

### Distributed Database for High Availability

A multi-node database cluster is used to provide replication and fault tolerance.

The system remains operational even if one database node fails.

---

### No Inter-Instance Communication

Application instances do not communicate with each other.

Instead, coordination is achieved through the database, simplifying the architecture and avoiding synchronization complexity.

---

### Concurrency Control via Conditional Updates

To prevent race conditions (e.g. overselling stocks), operations are implemented using atomic database updates:

* stock is decremented only if `quantity > 0`
* if no rows are affected, the operation fails

This avoids the classic read-modify-write problem and ensures correctness under concurrent access.

---

### Transactional Consistency

Each business operation (buy/sell) is executed within a single transaction:

1. update bank state
2. update wallet state
3. insert audit log

If any step fails, the entire operation is rolled back.

---

### Wallet Model Simplification

Wallets are not stored as separate entities.

Instead, ownership is represented using a `(wallet_id, stock_name)` relation.

This reduces complexity while still fulfilling all functional requirements.

---

### Bank State Management

The `POST /stocks` endpoint replaces the entire system state.

This operation resets:

* bank stocks
* all wallets
* audit log

This approach:

* guarantees full consistency (no dangling references)
* avoids invalid states where wallets reference non-existing stocks
* simplifies reasoning about system state in a distributed environment
* ensures deterministic behavior after reset

This design choice trades partial updates for full state replacement, which is acceptable within the scope of the assignment.

---

### Audit Log Design

Audit log entries are:

* stored in the database (not in memory)
* recorded only for successful operations
* ordered deterministically using timestamp and unique identifier

This guarantees consistency across instances and correctness under concurrency.

---

### Reverse Proxy and Load Balancing

A reverse proxy is used as a single entry point to distribute traffic across instances.

Passive health checks are used to detect failing instances.

**Trade-off:**
The reverse proxy is a single point of failure, intentionally chosen to keep the setup simple within the scope of the assignment.

---

### Simplifications

The system intentionally omits:

* financial balance tracking (users have unlimited funds)
* price fluctuations (fixed price = 1)
* order matching or order books

These simplifications allow focus on concurrency, consistency, and system design.

---

## Architecture

The system is composed of three main layers:

### Reverse Proxy – NGINX

* Acts as a single entry point (`localhost:PORT`)
* Distributes incoming requests across application instances (load balancing)
* Uses passive health checks to avoid routing traffic to failing instances

---

### Application Layer – Spring Boot (3 instances)

* Stateless services handling all business logic
* Instances do not communicate with each other
* Each request is processed independently
* All state is stored in the database

---

### Database Layer – CockroachDB (3-node cluster)

* Stores all application data (bank, wallets, audit log)
* Provides replication and fault tolerance
* Ensures consistency using the Raft consensus algorithm

---

### Request Flow

1. Client sends request to `localhost:PORT`
2. NGINX forwards request to one of the application instances
3. Application processes request and interacts with the database
4. Response is returned to the client

---

### Startup Flow

The system is orchestrated using Docker Compose:

1. Database nodes start
2. Database cluster is initialized
3. Application instances start
4. Reverse proxy starts as the entry point

---

## API Overview

The application exposes a REST API for managing wallets, stocks, and audit logs.

---

### Wallet Operations

**Buy / Sell Stock**

```http
POST /wallets/{wallet_id}/stocks/{stock_name}
```

Body:

```json
{
  "type": "buy" | "sell"
}
```

* Creates wallet if it does not exist
* `buy` decreases stock in bank and increases in wallet
* `sell` increases stock in bank and decreases in wallet
* Returns:

    * `200` on success
    * `400` if operation is not possible (e.g. no stock available)
    * `404` if stock does not exist

---

**Get Wallet State**

```http
GET /wallets/{wallet_id}
```

Response:

```json
{
  "id": "wallet_id",
  "stocks": [
    { "name": "stock1", "quantity": 10 }
  ]
}
```

* Returns wallet with all owned stocks
* Returns empty list if wallet has no stocks

---

**Get Stock Quantity in Wallet**

```http
GET /wallets/{wallet_id}/stocks/{stock_name}
```

* Returns a number (e.g. `10`)
* Returns `0` if wallet does not own the stock

---

### Bank Operations

**Set Bank State**

```http
POST /stocks
```

Body:

```json
{
  "stocks": [
    { "name": "stock1", "quantity": 100 }
  ]
}
```

* Replaces entire system state (bank, wallets, audit log)
* Acts as a full system reset
* Returns `200` on success

---

**Get Bank State**

```http
GET /stocks
```

* Returns all available stocks in the bank

---

### Audit Log

```http
GET /log
```

* Returns all successful operations (buy/sell)
* Ordered deterministically
* Maximum size: 10,000 newest entries

Logs are returned in order of occurrence, interpreted as chronological order (from oldest to newest).

---

### Chaos Testing

```http
POST /chaos
```

* Terminates the instance handling the request
* Used to verify system resilience and high availability

---

## Limitations and Trade-offs

This project intentionally simplifies certain aspects to focus on concurrency, consistency, and system design.

---

### Reverse Proxy as Single Point of Failure

The system uses a single reverse proxy (NGINX) as an entry point.

This is a deliberate simplification. In a production environment, this component would typically be replicated or replaced with a managed load balancing solution.

---

### Full State Reset on Bank Update

The `POST /stocks` endpoint is designed as a full system reset operation.

It replaces the entire system state, including:

* bank stocks
* all wallets
* audit log

This approach prioritizes consistency and simplicity over partial updates.

As a consequence:

* users may lose their holdings when a stock is removed from the bank state
* audit history is cleared during reset
* incremental updates of the bank are not supported

The endpoint defines a new authoritative state of the system, which may differ completely from the previous one.

Without resetting dependent data, the system could enter invalid states such as:

* wallets referencing non-existing stocks
* inconsistent behavior (e.g. `404` when selling previously owned stocks)
* race conditions between state updates and concurrent trades

By enforcing a full reset, the system guarantees a clean, consistent, and deterministic state across all instances.

---

### Simplified Market Model

The system does not implement:

* financial balances (users have unlimited funds)
* price fluctuations (fixed price = 1)
* order matching (no order book)

These constraints allow focusing on correctness and concurrency rather than financial modeling.

---

### No Authentication

All endpoints are publicly accessible.

Authentication and authorization are intentionally omitted to keep the scope focused on core system behavior.

---

### Database-Centric Coordination

Application instances do not communicate with each other.

All coordination is handled through the database, which simplifies the architecture but makes the system dependent on database performance and correctness.

---

### Centralized Audit Log (Application-Level)

Audit logs are stored in the database and shared across all instances.

This provides a consistent global view of operations and ensures no data is lost when instances fail. However, the system does not include centralized infrastructure for system logs or monitoring.

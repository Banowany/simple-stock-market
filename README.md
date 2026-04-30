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

The `POST /stocks` endpoint replaces the entire bank state instead of modifying it incrementally.

This approach:

* simplifies initialization and testing
* ensures deterministic system state
* avoids hidden state accumulation

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

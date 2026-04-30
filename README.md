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

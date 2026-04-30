# Simple Stock Market

## Overview

Simple Stock Market is a backend service that simulates a simplified stock exchange.

The system allows users to buy and sell stocks, while a central bank manages stock availability. All operations are executed immediately at a fixed price (equals 1), without order matching or price fluctuations. For simplicity, users are assumed to have unlimited funds, and transactions are constrained only by stock availability.

The application is designed as a distributed system with multiple stateless instances behind a reverse proxy and a shared database. It focuses on correctness, concurrency handling, and high availability under partial failures.
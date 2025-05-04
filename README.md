# Disho

A distributed microservices-based platform for ordering home-made dishes.

## Table of Contents

1. [Introduction](#introduction)
2. [Architecture Overview](#architecture-overview)
3. [Microservices](#microservices)
4. [RabbitMQ Messaging Flow](#rabbitmq-messaging-flow)
5. [Tech Stack](#tech-stack)
6. [Prerequisites](#prerequisites)
7. [Getting Started](#getting-started)
8. [Configuration](#configuration)
9. [Running the Services](#running-the-services)
10. [Contributing](#contributing)
11. [License](#license)

## Introduction

Disho is a platform that connects customers with home-made dishes sellers, providing an end-to-end ordering, inventory check, and payment processing workflow. It leverages RabbitMQ as its messaging backbone to ensure asynchronous, decoupled communication between services.

## Architecture Overview

Disho is composed of three primary microservices, each with its own codebase, database, and deployment pipeline. The services communicate via RabbitMQ exchanges and queues, ensuring:

* **Decoupling** and **scalability** of each component
* **Fault isolation** and **reliability** through durable messaging
* **Clear separation** of business responsibilities

## Microservices

1. **User Management Service**

   * Responsibilities:

     * CRUD operations for Admins, Seller-Reps, and Customers
     * Auto-generate credentials for Seller-Reps
     * Authentication (login, token issuance)
   * Exposed REST endpoints:

     * `POST /users` (create user)
     * `GET /users?role=` (list users)
     * `POST /auth/login` (authenticate)

2. **Dish & Inventory Service**

   * Responsibilities:

     * Manage dishes (create, read, update stock & pricing)
     * Track current stock levels
     * Maintain sales history
   * Exposed REST endpoints:

     * `GET /dishes`
     * `POST /dishes`
     * `PUT /dishes/{id}`
     * `GET /dishes/sold`

3. **Order & Payment Service**

   * Responsibilities:

     * Accept and validate customer orders
     * Enforce minimum charge rule
     * Coordinate stock checks and payment processing
     * Notify customers of order status
   * Exposed REST endpoints:

     * `POST /orders`
     * `GET /orders?customerId=`

## RabbitMQ Messaging Flow

Disho uses a single RabbitMQ broker with one exchange (`orders.exchange`) and multiple queues to handle the ordering workflow:

1. **Order Requested**

   * Order Service publishes `order.requested` to `orders.exchange`.
   * Dish Service consumes from `dish.stock-check.queue`.

2. **Stock Verification**

   * Dish Service checks item availability.
   * Publishes `order.stock.verified` or `order.stock.failed` back to `orders.exchange`.

3. **Order Processing**

   * Order Service consumes from `order.processor.queue`:

     * On `order.stock.failed`: publishes `order.rejected`.
     * On `order.stock.verified`: checks minimum charge, processes payment, then publishes `order.confirmed` or `order.rejected`.

4. **Notification**

   * Notification component (could be part of Order Service or a separate consumer) listens on `notification.queue` for `order.confirmed` and `order.rejected`, then sends updates to customers.

## Tech Stack

> *This section is a placeholder—feel free to update with actual choices.*

* **Languages:** Java, JavaScript (Node.js), Python, ...
* **Frameworks:** Spring Boot, Express.js, EJB, Flask, ...
* **Databases:** PostgreSQL, MongoDB, MySQL, ...
* **Broker:** RabbitMQ
* **Authentication:** JWT, OAuth2, ...
* **UI:** React, Angular, Vue, ...

## Prerequisites

* Docker & Docker Compose (or local installations of each service’s runtime)
* RabbitMQ server (local or cloud-hosted)
* Node.js (v14+), Java (JDK 11+), Python (3.8+)
* PostgreSQL, MongoDB, or chosen databases

## Getting Started

1. Clone the repository:

   ```bash
   git clone https://github.com/your-org/disho.git
   cd disho
   ```
2. Configure environment variables for each service (see [Configuration](#configuration)).
3. Start RabbitMQ:

   ```bash
   docker-compose up -d rabbitmq
   ```
4. Launch each microservice (either via Docker Compose or locally):

   ```bash
   # User Management Service
   cd services/user-management
   ./run.sh  # or mvn spring-boot:run

   # Dish & Inventory Service
   cd ../dish-inventory
   npm install && npm start

   # Order & Payment Service
   cd ../order-payment
   mvn package && java -jar target/order-payment.jar
   ```

## Configuration

Each service reads its configuration (DB credentials, RabbitMQ URL, JWT secret, etc.) from environment variables. Copy the provided `.env.example` in each service directory to `.env` and update values.

| Variable           | Description                   |
| ------------------ | ----------------------------- |
| `RABBITMQ_URL`     | AMQP connection string        |
| `DB_HOST`          | Database host                 |
| `DB_USER`          | Database username             |
| `DB_PASS`          | Database password             |
| `JWT_SECRET`       | Secret for signing JWT tokens |
| `MIN_ORDER_CHARGE` | Minimum order amount          |

## Running the Services

After configuration, each service can be started independently. Use logs to confirm successful RabbitMQ connection and REST API readiness.

## Contributing

1. Fork the repo and create your feature branch: `git checkout -b feature/YourFeature`
2. Commit your changes: \`git commit -m "Add YourFeature"
3. Push to the branch: `git push origin feature/YourFeature`
4. Open a Pull Request and request reviews.

## License

MIT License © 2025 Your Name or Organization

# 💳 Midas Core – Real-Time Financial Transaction System

## 🚀 About this project

This project was built as part of the **JPMorgan Chase Software Engineering Virtual Experience (Forage)**.

It simulates a backend system that handles real-time financial transactions between users — similar to how money transfers work in banking systems.

The system validates transactions, processes them asynchronously, applies incentives via an external API, stores everything in a database, and exposes a REST API for users to check their balance.

---

## 🧩 What this system does

* Processes transactions between users
* Validates sender, recipient, and balance before execution
* Applies incentives using an external REST API
* Updates balances safely using database transactions
* Stores transaction history for auditing
* Allows users to check their balance via an API

---

## 🏗️ System Architecture

### 📡 Event-Driven Processing (Kafka)

Transactions are sent as messages through Kafka.
The application listens to these messages and processes them asynchronously.

---

### 🗄️ Database Layer (H2 + JPA)

* **UserRecord** → stores user details and balances
* **TransactionRecord** → stores transaction + incentive details

Spring Data JPA handles database operations.

---

### 🌐 External API Integration

* Integrated with an **Incentive API** using `RestTemplate`
* Incentives are added to recipients (not deducted from senders)
* Demonstrates service-to-service communication

---

### 🌍 REST API (User Balance)

```http
GET /balance?userId=1
```

Returns:

```json
{
  "amount": 1000.0
}
```

* Returns `0` if user does not exist
* Runs on port **33400**

---

## ⚙️ Key Features

* ✅ Real-time transaction processing using Kafka
* ✅ Validation logic to prevent invalid transactions
* ✅ Incentive calculation via external API
* ✅ Atomic balance updates using `@Transactional`
* ✅ Persistent transaction logging
* ✅ REST API for querying user balance

---

## 🧠 What I learned

* Building event-driven systems using Kafka
* Integrating external services using REST APIs
* Handling data consistency in financial systems
* Designing backend architecture with multiple components
* Debugging asynchronous workflows

---

## 🛠️ Tech Stack

* Java
* Spring Boot
* Apache Kafka
* Spring Data JPA (Hibernate)
* H2 Database
* REST APIs (RestTemplate)

---

## 📌 How to run

1. Start the Incentive API:

```bash
java -jar incentive-api.jar
```

2. Run the application:

```bash
./mvnw spring-boot:run
```

3. Run tests:

```bash
./mvnw test
```

---

## 🏁 Final Status

✅ Completed all tasks (Kafka + DB + API + REST Controller)
✅ Fully functional backend system

---

## 💡 Final thoughts

This project gave me hands-on experience with how real backend systems are designed — especially in fintech environments where correctness and reliability are critical.

It also helped me understand how different components (Kafka, APIs, databases) work together in a real-world architecture.

---

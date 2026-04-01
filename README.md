# 🚀 FlexSpace — Coworking Desk Booking Platform

FlexSpace is a backend system designed to manage coworking space desk bookings with a **subscription-based access model**. It ensures real-time availability, prevents double bookings, and enforces business rules like daily booking limits.

---

## 🧠 Problem Statement

Traditional coworking systems lack:

* Real-time desk availability tracking
* Conflict-free booking mechanisms
* Subscription-based access control

FlexSpace solves this with a **transaction-safe booking engine** and structured backend architecture.

---

## 🏗️ System Architecture

```
Client → Controller → Service → Repository → Database (MySQL)
```

### Layer Responsibilities

| Layer      | Responsibility                 |
| ---------- | ------------------------------ |
| Controller | Handles API requests/responses |
| Service    | Business logic & validations   |
| Repository | SQL queries via JDBC           |
| DTO        | API request/response objects   |
| Model      | Database entity mapping        |

---

## ⚙️ Tech Stack

* **Language:** Java 17
* **Framework:** Spring Boot
* **Database:** MySQL
* **Data Access:** JDBC Template
* **Build Tool:** Maven
* **Testing:** Postman / Curl

---

## 🔥 Core Features

### ✅ Booking Engine

* Multi-desk booking support
* Time-slot based reservations
* Conflict detection (prevents double booking)
* Desk unavailability handling

---

### ✅ Subscription System

* Active subscription required
* Daily booking hour limits enforced
* Supports multiple active subscriptions
* Aggregated daily limits calculation

---

### ✅ System Design

* Transaction-safe booking (`@Transactional`)
* Centralized exception handling (`@ControllerAdvice`)
* Standardized API response (`ApiResponse<T>`)
* Validation-driven service logic

---

## 📊 API Documentation

### 🔹 Booking APIs

| Method | Endpoint                | Description              |
| ------ | ----------------------- | ------------------------ |
| POST   | `/bookings`             | Create booking           |
| POST   | `/bookings/{id}/cancel` | Cancel booking           |
| GET    | `/bookings/{id}`        | Get booking by ID        |
| GET    | `/bookings/user`        | Get all bookings of user |

---

## 📥 Sample Request

### Create Booking

```json
{
  "deskIds": [1],
  "startTime": "2026-03-25T10:00:00",
  "endTime": "2026-03-25T12:00:00"
}
```

---

## 📤 Sample Response

```json
{
  "success": true,
  "message": "Booking created",
  "data": {
    "id": 1,
    "userId": 1,
    "startTime": "2026-03-25T10:00:00",
    "endTime": "2026-03-25T12:00:00",
    "status": "CONFIRMED"
  }
}
```

---

## ⚠️ Edge Cases Handled

* Booking in the past
* Invalid time range
* Cross-day booking restriction
* Desk already booked
* Desk unavailable (maintenance)
* Daily booking limit exceeded
* No active subscription

---

## 🧩 Key Business Logic

- Prevents overlapping bookings using time-based conflict detection
- Enforces daily booking limits based on active subscriptions
- Validates desk availability considering both bookings and maintenance blocks
- Ensures atomic operations using transactional boundaries

---

## ⚡ Challenges Addressed

- Handling concurrent booking conflicts
- Designing flexible subscription validation
- Maintaining data consistency with transactional operations
- Structuring scalable service-layer validationsres atomic operations using transactional boundaries
  
---

## 🧱 Database Entities

* User
* Subscription
* Plan
* Booking
* BookingDesk
* Desk
* DeskUnavailability
* CoworkingSpace

---

## 🔐 Upcoming Features (Roadmap)

### 🚀 Phase 3 — Authentication

* JWT-based authentication
* Secure endpoints
* Remove hardcoded userId
* Role-based access (USER / ADMIN)

---

### 🚀 Phase 4 — Advanced Features

#### 📅 Booking Enhancements

* Multi-day booking
* Recurring bookings (weekly/monthly)
* Calendar-based availability

#### 🏢 Owner Module

* Create/manage coworking spaces
* Add/remove desks
* Block desks for maintenance

#### 💳 Payments

* Subscription purchase (Razorpay integration)
* Transaction tracking

---

## 🤖 AI Integration (Planned)

* Smart desk/time recommendations
* Usage insights (booking patterns)
* Suggest optimal booking slots

---

## 🧠 Key Learning Highlights

* Designing a real-world booking system
* Transaction management in distributed systems
* SQL-based conflict detection
* Clean layered architecture
* Business rule enforcement at service layer
* Scalable backend design thinking

---

## 📌 Current Status

| Module             | Status     |
| ------------------ | ---------- |
| Booking Engine     | ✅ Complete |
| Subscription Logic | ✅ Complete |
| Exception Handling | ✅ Complete |
| Authentication     | ⏳ Pending  |
| Payments           | ⏳ Pending  |
| AI Features        | ⏳ Planned  |

---

## ⚠️ Known Limitations

* No authentication (uses dummy userId)
* Race condition not fully handled (DB-level locking pending)
* No caching implemented

---

## 🚀 Future Improvements

* Implement DB-level locking for concurrency
* Add Redis caching
* Optimize SQL queries with indexing
* Introduce event-driven architecture

---

## 👨‍💻 Author

**Nikhil Mishra**

---

## ⭐ Final Note

FlexSpace is designed as a practical backend system that reflects real-world requirements such as booking constraints, subscription validation, and transactional consistency.

The architecture emphasizes clarity, scalability, and maintainability, making it a solid foundation for further enhancements like authentication, payments, and intelligent features.

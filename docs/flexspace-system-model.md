
# FlexSpace — Finalized System Model (Actors, Entities, Tables, Relationships)

## Actors

Actors represent the types of users interacting with the FlexSpace platform.

### User
A registered platform member who can:
- Browse coworking spaces
- View desks and availability
- Purchase subscription plans
- Book desks
- Cancel bookings
- View booking history

### Space Owner
A user who owns coworking spaces and can:
- Register coworking spaces
- Manage desks inside their spaces
- View bookings for their spaces
- View revenue reports

### Administrator
A platform-level manager responsible for:
- Approving coworking spaces
- Managing platform users
- Managing subscription plans
- Monitoring platform activity

Actors are implemented through **roles in the User table**.

---

# Core Entities

### User
Represents a person using the platform.
Purpose:
- Authentication
- Booking desks
- Managing coworking spaces (if owner)
- Purchasing plans

### CoworkingSpace
Represents a physical coworking location listed on the platform.
Purpose:
- Contains desks
- Provides a location where bookings occur
- Managed by a space owner

### Desk
Represents a single workspace inside a coworking space.
Purpose:
- Allocatable booking resource
- Can be booked for specific time ranges
- Can be temporarily unavailable

### Booking
Represents a time-bound reservation created by a user.
Purpose:
- Allocates desks to users
- Tracks reservation lifecycle
- Maintains usage history

### DeskUnavailability
Represents a time period during which a desk cannot be booked.
Purpose:
- Maintenance
- Cleaning
- Administrative blocking

### Plan
Represents subscription products offered by the platform.
Purpose:
- Defines booking privileges
- Defines daily booking hour limits
- Defines pricing and duration

### Subscription
Represents the active plan purchased by a user.
Purpose:
- Grants booking rights
- Defines booking hour limits
- Defines subscription validity period

### Transaction
Represents payment records associated with subscriptions.
Purpose:
- Stores payment history
- Tracks payment provider transactions
- Supports Razorpay integration

---

# Database Tables

## User
User
-----
id
name
email
password
role
created_at

Role values:
USER
OWNER
ADMIN

## CoworkingSpace
CoworkingSpace
--------------
id
name
owner_id
address
city
state
created_at

## Desk
Desk
----
id
space_id
type
created_at

## Booking
Booking
-------
id
user_id
start_time
end_time
status
created_at

Status values:
CONFIRMED
CANCELLED

## BookingDesk
BookingDesk
-----------
id
booking_id
desk_id

## DeskUnavailability
DeskUnavailability
------------------
id
desk_id
start_time
end_time
reason

Example reasons:
maintenance
cleaning
blocked

## Plan
Plan
----
id
name
duration_type
duration_value
daily_booking_hours
price
created_at

## Subscription
Subscription
------------
id
user_id
plan_id
start_date
end_date
status

Status values:
ACTIVE
EXPIRED
CANCELLED

## Transaction
Transaction
-----------
id
user_id
plan_id
amount
payment_provider
provider_transaction_id
status
created_at

Status values:
PENDING
SUCCESS
FAILED
REFUNDED

---

# Entity Relationships

User → Booking
User 1 ----- * Booking

CoworkingSpace → Desk
CoworkingSpace 1 ----- * Desk

Desk → Booking
Desk 1 ----- * BookingDesk

Booking → BookingDesk
Booking 1 ----- * BookingDesk

Desk → DeskUnavailability
Desk 1 ----- * DeskUnavailability

Plan → Subscription
Plan 1 ----- * Subscription

User → Subscription
User 1 ----- 1 Subscription

User → Transaction
User 1 ----- * Transaction

---

# Booking Validation Rules

Active Subscription:
SELECT * FROM subscription
WHERE user_id = ?
AND status = 'ACTIVE'
AND end_date > NOW()

Daily Booking Limit:
SELECT SUM(TIMESTAMPDIFF(HOUR,start_time,end_time))
FROM booking
WHERE user_id = ?
AND DATE(start_time) = CURRENT_DATE

Desk Conflict Check:
SELECT *
FROM booking_desk bd
JOIN booking b ON b.id = bd.booking_id
WHERE bd.desk_id = ?
AND b.start_time < new_end
AND b.end_time > new_start

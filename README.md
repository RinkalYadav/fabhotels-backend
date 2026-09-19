# FabHotels Backend

Backend application for a hotel/property booking system.

## Technology Stack

- Java 17
- Spring Boot
- Maven
- JUnit

## Current Features

- Initial Spring Boot project setup
- Project health endpoint

## FAB-101 Project Setup

Initialized the FabHotels backend project using Spring Boot and Maven.


## API Documentation

The FabHotels Backend API is documented using Swagger/OpenAPI.

### Swagger UI

After starting the application, open:

http://localhost:8080/swagger-ui/index.html

Swagger UI provides an interactive interface to:

- View all available REST APIs
- View API request parameters
- View request and response DTO schemas
- View HTTP response codes
- Execute APIs directly from the browser

### OpenAPI Specification

The OpenAPI JSON specification is available at:

http://localhost:8080/v3/api-docs

### API Documentation Structure

The APIs are organized into the following groups:

- Hotels
- Rooms
- Room Availability
- Pricing
- Bookings
- Payments
- Booking Cancellation

### Running the Application

Start the Spring Boot application using:

```bash
mvn spring-boot:run


## FAB-118 Database Optimization

Database performance was optimized to improve application behavior as the data volume grows.

### Database Indexes

- Composite index on `hotel(city, state)` for hotel search.
- Composite index on `booking(room_id, status, check_in, check_out)` for booking overlap checks.
- Unique composite constraint on `room(hotel_id, room_number)`.
- Unique composite constraint on `room_availability(room_id, availability_date)`.
- Unique constraint on `payment(booking_id)`.
- Composite index on `pricing(room_id, start_date, end_date)`.

### JPA Fetch Optimization

- `@ManyToOne` relationships use `FetchType.LAZY`.
- `@OneToMany` relationships use `FetchType.LAZY`.
- This prevents unnecessary collection loading and reduces the risk of N+1 queries.

### Query Optimization

- Hotel search uses database-level pagination.
- Room availability checks use a single date-range query.
- Booking availability updates use `saveAll()`.
- Cancellation availability updates use a single date-range query and `saveAll()`.
- Pricing records are loaded using a single range query instead of executing one query per booking night.
- Booking overlap validation uses an indexed derived query.

### Performance Verification

MySQL `EXPLAIN` can be used to verify important queries and confirm index usage.

Example:

```sql
EXPLAIN
SELECT 1
FROM booking
WHERE room_id = 1
  AND status = 'CONFIRMED'
  AND check_in < '2035-01-12'
  AND check_out > '2035-01-10';
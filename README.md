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

## FAB-120 — Code Quality & Static Analysis

Code quality and static analysis were reviewed using SonarQube to improve reliability, maintainability, security, and test quality.

### Static Analysis

- SonarQube configured for the FabHotels Backend.
- Reviewed reliability issues and code smells.
- Reviewed security vulnerabilities and security hotspots.
- Reviewed duplicate code.
- Reviewed test-code maintainability issues.
- Refactored important code-quality findings without blindly suppressing warnings.

### Security Review

- JWT authentication and authorization reviewed.
- Password handling reviewed.
- Sensitive information is not logged.
- JWT secrets are provided through environment configuration.
- CORS and security configuration reviewed.
- CSRF configuration reviewed for the stateless JWT architecture.
- Security hotspot related to CSRF was reviewed and accepted with documented justification.

### Code Quality Improvements

- Removed unnecessary declarations and imports.
- Reduced duplicated test code using parameterized tests.
- Improved exception-test structure.
- Improved timezone handling in cancellation logic.
- Removed unnecessary checked exceptions.
- Replaced duplicated security configuration literals with constants.
- Reviewed DTO and service-layer maintainability issues.
- Reviewed logging and exception handling.

### Test Coverage

SonarQube analysis was used to review test coverage across the application.

Current analysis:

- Quality Gate: **Passed**
- Security Rating: **A**
- Reliability Rating: **A**
- Maintainability Rating: **A**
- Test Coverage: **78.2%**
- Duplications: **1.7%**

### Verification

The project was verified using:

```bash
mvn clean test
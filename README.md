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
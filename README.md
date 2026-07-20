# Gym Management System

A comprehensive Gym Management System built with Java, Spring Boot, and MySQL. This application provides dual portals: a User Portal for gym members and an Admin Portal for staff. It includes features like JWT-based authentication, real-time email notifications, class scheduling, and membership tracking.

## Features

* **Role-Based Authentication (JWT)**: Secure login for both `USER` and `ADMIN` roles.
* **User Portal**: Members can view their dashboard, enroll in classes, track attendance, make payments, and reset their password.
* **Admin Portal**: Staff can manage members, schedule gym classes, track trainer assignments, and monitor overall gym statistics.
* **Automated Email Notifications**: Integrates with Gmail SMTP to send welcome emails upon registration and secure temporary passwords for password resets.
* **MySQL Database**: Persistent storage for all entities including Users, Classes, Enrollments, Payments, and Audit Logs.

## Tech Stack

* **Backend**: Java 17, Spring Boot 3
* **Security**: Spring Security, JWT (JSON Web Tokens)
* **Database**: MySQL 8, Spring Data JPA / Hibernate
* **Frontend**: HTML5, Vanilla CSS, JavaScript (Fetch API)
* **Build Tool**: Maven

## Getting Started

### Prerequisites
* Java 17 or higher
* Maven (or use the provided `mvnw` wrapper)
* MySQL Server (running locally on port 3306)

### Setup
1. Build and run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
2. Access the application in your browser at `http://localhost:8080` (or the port specified in your console logs).

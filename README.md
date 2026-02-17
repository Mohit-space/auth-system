# 🚀 Authentication System - Spring Boot

A complete, production-ready authentication module built with Spring Boot for user registration, login, and account recovery.

## 👨‍💻 Author
**Mohit** - Backend Task

## 📋 Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Database Configuration](#database-configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Testing with Postman](#testing-with-postman)
- [Security Features](#security-features)
- [Assumptions & Design Decisions](#assumptions--design-decisions)

## ✨ Features

### Implemented Features
- ✅ **User Registration (Signup)** - Create new user accounts with validation
- ✅ **User Login** - Authenticate users with email and password
- ✅ **JWT Token Generation** - Secure token-based authentication
- ✅ **Password Encryption** - BCrypt password hashing
- ✅ **Forgot Password** - Generate OTP for password recovery
- ✅ **Reset Password** - Reset password using OTP verification
- ✅ **Global Exception Handling** - Centralized error management
- ✅ **Input Validation** - Comprehensive request validation
- ✅ **Clean Architecture** - Layered design (Controller → Service → Repository)

## 🛠 Tech Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Security**: 6.x
- **Spring Data JPA**: 3.x
- **MySQL**: 8.x (or PostgreSQL)
- **JWT**: 0.12.3
- **Lombok**: Latest
- **Maven**: 3.x

## 📁 Project Structure

```
auth-system/
├── src/
│   ├── main/
│   │   ├── java/com/intern/task/authsystem/
│   │   │   ├── config/            # Security & App configuration
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/        # REST API endpoints
│   │   │   │   └── AuthController.java
│   │   │   ├── dto/               # Data Transfer Objects
│   │   │   │   ├── SignupRequest.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── UserResponse.java
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── ForgotPasswordRequest.java
│   │   │   │   ├── ResetPasswordRequest.java
│   │   │   │   └── ApiResponse.java
│   │   │   ├── entity/            # Database entities
│   │   │   │   ├── User.java
│   │   │   │   └── UserOtp.java
│   │   │   ├── enums/             # Enumerations
│   │   │   │   └── OtpPurpose.java
│   │   │   ├── exception/         # Custom exceptions
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── UserAlreadyExistsException.java
│   │   │   │   ├── UserNotFoundException.java
│   │   │   │   ├── InvalidCredentialsException.java
│   │   │   │   └── InvalidOtpException.java
│   │   │   ├── repository/        # Database repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── UserOtpRepository.java
│   │   │   ├── security/          # Security implementations
│   │   │   │   ├── CustomUserDetails.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── service/           # Business logic
│   │   │   │   ├── AuthService.java
│   │   │   │   └── AuthServiceImpl.java
│   │   │   ├── util/              # Utility classes
│   │   │   │   ├── JwtUtil.java
│   │   │   │   └── OtpGenerator.java
│   │   │   └── AuthSystemApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── schema.sql                     # Database schema
├── postman_collection.json        # Postman API collection
├── pom.xml                        # Maven dependencies
└── README.md                      # This file
```

## 📋 Prerequisites

Before running this project, ensure you have:

1. **Java 17** or higher installed
   ```bash
   java -version
   ```

2. **Maven 3.6+** installed
   ```bash
   mvn -version
   ```

3. **MySQL 8.0+** or **PostgreSQL 12+** installed and running
   ```bash
   mysql --version
   # OR
   psql --version
   ```

4. **Postman** (optional, for API testing)

## 🔧 Installation & Setup

### 1. Clone/Download the Project
```bash
cd auth-system
```

### 2. Configure Database
Choose between MySQL or PostgreSQL:

#### Option A: MySQL (Default)
```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE auth_db;

# Exit MySQL
exit;
```

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auth_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password
```

#### Option B: PostgreSQL
```bash
# Login to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE auth_db;

# Exit PostgreSQL
\q
```

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### 3. (Optional) Run SQL Schema
If you prefer manual table creation:
```bash
mysql -u root -p auth_db < schema.sql
# OR for PostgreSQL
psql -U postgres -d auth_db -f schema.sql
```

**Note**: Spring Boot will auto-create tables with `spring.jpa.hibernate.ddl-auto=update`

## 🚀 Running the Application

### Method 1: Using Maven
```bash
mvn clean install
mvn spring-boot:run
```

### Method 2: Using Java
```bash
mvn clean package
java -jar target/auth-system-1.0.0.jar
```

### Method 3: Using IDE
- Import project as Maven project in IntelliJ IDEA / Eclipse
- Run `AuthSystemApplication.java`

The application will start on: `http://localhost:8080`

## 📡 API Documentation

### Base URL
```
http://localhost:8080/api/auth
```

### 1. Health Check
**Endpoint**: `GET /health`

**Response**:
```json
{
  "success": true,
  "message": "Auth service is running",
  "data": "OK",
  "timestamp": "2024-02-17 15:30:45"
}
```

---

### 2. User Signup
**Endpoint**: `POST /signup`

**Request Body**:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password@123",
  "phone": "9876543210"
}
```

**Validation Rules**:
- Name: 2-100 characters
- Email: Valid email format
- Password: 8-50 characters, must contain:
  - At least 1 uppercase letter
  - At least 1 lowercase letter
  - At least 1 digit
  - At least 1 special character (@#$%^&+=)
- Phone: 10 digits (optional)

**Success Response** (201 Created):
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "message": "User registered successfully",
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "phone": "9876543210",
      "isActive": true,
      "createdAt": "2024-02-17 15:30:45",
      "updatedAt": "2024-02-17 15:30:45"
    },
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "timestamp": "2024-02-17 15:30:45"
}
```

**Error Response** (409 Conflict):
```json
{
  "success": false,
  "message": "User with email john@example.com already exists",
  "data": null,
  "timestamp": "2024-02-17 15:30:45"
}
```

---

### 3. User Login
**Endpoint**: `POST /login`

**Request Body**:
```json
{
  "email": "john@example.com",
  "password": "Password@123"
}
```

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "message": "Login successful",
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "phone": "9876543210",
      "isActive": true,
      "createdAt": "2024-02-17 15:30:45",
      "updatedAt": "2024-02-17 15:30:45"
    },
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "timestamp": "2024-02-17 15:35:20"
}
```

**Error Response** (401 Unauthorized):
```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null,
  "timestamp": "2024-02-17 15:35:20"
}
```

---

### 4. Forgot Password
**Endpoint**: `POST /forgot-password`

**Request Body**:
```json
{
  "email": "john@example.com"
}
```

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "Password reset OTP has been sent to your email. OTP: 123456 (Valid for 10 minutes)",
  "data": null,
  "timestamp": "2024-02-17 15:40:10"
}
```

**Note**: In production, OTP should be sent via email/SMS. Currently logged for testing.

---

### 5. Reset Password
**Endpoint**: `POST /reset-password`

**Request Body**:
```json
{
  "email": "john@example.com",
  "otp": "123456",
  "newPassword": "NewPassword@123"
}
```

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "Password has been reset successfully",
  "data": null,
  "timestamp": "2024-02-17 15:45:30"
}
```

**Error Response** (400 Bad Request):
```json
{
  "success": false,
  "message": "Invalid or expired OTP",
  "data": null,
  "timestamp": "2024-02-17 15:45:30"
}
```

---

## 🧪 Testing with Postman

### Import Collection
1. Open Postman
2. Click **Import** → **Upload Files**
3. Select `postman_collection.json`
4. All endpoints will be ready to test

### Test Flow
1. **Health Check** - Verify service is running
2. **Signup** - Create a new user
3. **Login** - Get JWT token (save it for authenticated requests)
4. **Forgot Password** - Generate OTP (check logs for OTP)
5. **Reset Password** - Use OTP to reset password
6. **Login Again** - Verify new password works

### Sample Test Data
```json
// User 1
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password@123",
  "phone": "9876543210"
}

// User 2
{
  "name": "Jane Smith",
  "email": "jane@example.com",
  "password": "SecurePass@456",
  "phone": "9876543211"
}
```

## 🔒 Security Features

### 1. Password Security
- **BCrypt Encryption**: All passwords hashed with BCrypt (cost factor 10)
- **Never Stored Plain Text**: Passwords never logged or exposed
- **Strong Password Policy**: Enforced via validation

### 2. JWT Authentication
- **Stateless**: No server-side session storage
- **24-hour Expiry**: Tokens expire after 1 day
- **HS256 Algorithm**: HMAC with SHA-256
- **Secret Key**: Configurable in properties

### 3. OTP Security
- **6-Digit Random**: Generated using SecureRandom
- **10-Minute Expiry**: OTPs automatically expire
- **Single Use**: Marked as used after verification
- **Purpose-Specific**: Separate OTPs for different operations

### 4. API Security
- **CSRF Disabled**: For stateless REST API
- **CORS Configurable**: Can be configured as needed
- **Rate Limiting**: Can be added using Spring Cloud Gateway
- **Input Validation**: Jakarta Bean Validation

### 5. Database Security
- **Prepared Statements**: JPA prevents SQL injection
- **Indexed Queries**: Optimized for performance
- **Cascade Delete**: OTPs deleted with user

## 📝 Assumptions & Design Decisions

### Assumptions
1. **Email as Username**: Email is used as the unique identifier for login
2. **Single Device Login**: No concurrent session management
3. **OTP Delivery**: Currently logged (production would use email/SMS service)
4. **Account Activation**: Users are active by default (no email verification)
5. **Phone Optional**: Phone number is not mandatory

### Design Decisions

#### 1. **Layered Architecture**
- **Controller**: Handles HTTP requests/responses
- **Service**: Contains business logic
- **Repository**: Database operations
- **Benefits**: Separation of concerns, testability, maintainability

#### 2. **DTO Pattern**
- Separate request/response objects
- Never expose entity directly
- Validation at DTO level
- Clean API contracts

#### 3. **Global Exception Handling**
- Single point of error handling
- Consistent error responses
- HTTP status codes follow REST standards
- User-friendly error messages

#### 4. **JWT for Authentication**
- Chosen over session-based for scalability
- Stateless architecture
- Easy to implement microservices later
- Mobile app friendly

#### 5. **OTP for Password Reset**
- More secure than email links
- Time-bound expiry
- Single-use verification
- Prevents brute force attacks

#### 6. **Database Choice**
- MySQL recommended for production
- PostgreSQL also supported
- Auto-schema generation for development
- Manual schema provided for production

## 🚨 Error Codes & Messages

| HTTP Status | Error | Message |
|-------------|-------|---------|
| 400 | Bad Request | Validation failed / Invalid OTP |
| 401 | Unauthorized | Invalid email or password |
| 404 | Not Found | User not found |
| 409 | Conflict | User already exists |
| 500 | Internal Server Error | Unexpected error occurred |

## 🔍 Troubleshooting

### Issue: Application fails to start
**Solution**: Check database connection in `application.properties`

### Issue: "Access denied for user"
**Solution**: Verify MySQL username/password

### Issue: "Table doesn't exist"
**Solution**: Ensure `spring.jpa.hibernate.ddl-auto=update` or run schema.sql

### Issue: OTP not working
**Solution**: Check server logs for generated OTP and expiry time

### Issue: JWT token invalid
**Solution**: Verify JWT secret is consistent and token hasn't expired

## 🎯 Future Enhancements

- [ ] Email/SMS integration for OTP delivery
- [ ] Rate limiting for APIs
- [ ] Refresh token implementation
- [ ] Social login (Google, Facebook)
- [ ] Role-based access control (RBAC)
- [ ] Email verification on signup
- [ ] Account lockout after failed attempts
- [ ] Password history tracking
- [ ] Audit logging
- [ ] API documentation with Swagger/OpenAPI

## 📞 Support

For any questions or issues:
- Check the logs: `logs/application.log`
- Review error messages in Postman
- Verify database connection
- Ensure all dependencies are installed



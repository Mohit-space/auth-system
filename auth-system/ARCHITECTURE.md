# 🏗️ Architecture & Design Documentation

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                          │
│                  (Postman / Mobile / Web)                    │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTP/REST
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER                          │
│  ┌──────────────────────────────────────────────────────┐   │
│  │           AuthController.java                        │   │
│  │  - signup()     - forgotPassword()                   │   │
│  │  - login()      - resetPassword()                    │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────┬───────────────────────────────────────┘
                      │ DTOs
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                     SERVICE LAYER                            │
│  ┌──────────────────────────────────────────────────────┐   │
│  │          AuthServiceImpl.java                        │   │
│  │  - Business Logic                                    │   │
│  │  - Validation                                        │   │
│  │  - Transaction Management                            │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────┬───────────────────────────────────────┘
                      │ Entities
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                  REPOSITORY LAYER                            │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  UserRepository        UserOtpRepository             │   │
│  │  (Spring Data JPA)                                   │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────┬───────────────────────────────────────┘
                      │ JDBC
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                    DATABASE LAYER                            │
│                   MySQL / PostgreSQL                         │
│  ┌──────────────┐          ┌──────────────┐                 │
│  │    users     │          │   user_otp   │                 │
│  └──────────────┘          └──────────────┘                 │
└─────────────────────────────────────────────────────────────┘
```

## Layer Responsibilities

### 1. Controller Layer
**Purpose**: Handle HTTP requests and responses

**Responsibilities**:
- Receive HTTP requests
- Validate request format
- Call appropriate service methods
- Return formatted responses
- Handle HTTP status codes

**Files**:
- `AuthController.java`

**Example**:
```java
@PostMapping("/signup")
public ResponseEntity<ApiResponse<AuthResponse>> signup(@Valid @RequestBody SignupRequest request) {
    AuthResponse response = authService.signup(request);
    return new ResponseEntity<>(ApiResponse.success("...", response), HttpStatus.CREATED);
}
```

### 2. Service Layer
**Purpose**: Implement business logic

**Responsibilities**:
- Process business rules
- Coordinate between repositories
- Handle transactions
- Throw business exceptions
- Generate tokens/OTPs

**Files**:
- `AuthService.java` (Interface)
- `AuthServiceImpl.java` (Implementation)

**Example**:
```java
@Transactional
public AuthResponse signup(SignupRequest request) {
    // Check if user exists
    // Hash password
    // Save user
    // Generate token
    // Return response
}
```

### 3. Repository Layer
**Purpose**: Database operations

**Responsibilities**:
- CRUD operations
- Custom queries
- Database abstraction

**Files**:
- `UserRepository.java`
- `UserOtpRepository.java`

**Example**:
```java
Optional<User> findByEmail(String email);
boolean existsByEmail(String email);
```

### 4. Security Layer
**Purpose**: Authentication and authorization

**Components**:
- `SecurityConfig.java` - Spring Security configuration
- `CustomUserDetailsService.java` - Load user for authentication
- `CustomUserDetails.java` - User details implementation
- `JwtUtil.java` - JWT token operations

## Data Flow

### Signup Flow
```
1. Client sends POST /api/auth/signup
2. Controller receives SignupRequest DTO
3. @Valid triggers validation
4. Controller calls authService.signup()
5. Service checks if email exists
6. Service hashes password with BCrypt
7. Service saves user to database
8. Service generates JWT token
9. Service returns AuthResponse
10. Controller wraps in ApiResponse
11. Client receives 201 Created with token
```

### Login Flow
```
1. Client sends POST /api/auth/login
2. Controller receives LoginRequest DTO
3. Controller calls authService.login()
4. Service calls authenticationManager.authenticate()
5. Spring Security loads user via CustomUserDetailsService
6. Spring Security verifies password
7. If valid, service generates JWT token
8. Service returns AuthResponse with token
9. Client receives 200 OK with token
```

### Forgot Password Flow
```
1. Client sends POST /api/auth/forgot-password
2. Service finds user by email
3. Service deletes old OTPs for this user
4. Service generates 6-digit OTP
5. Service saves OTP with 10-min expiry
6. Service returns success (OTP logged for testing)
7. (Production: Send OTP via email/SMS)
```

### Reset Password Flow
```
1. Client sends POST /api/auth/reset-password
2. Service finds user by email
3. Service verifies OTP:
   - Correct OTP
   - Not used
   - Not expired
4. Service hashes new password
5. Service updates user password
6. Service marks OTP as used
7. Client receives success message
```

## Security Architecture

### Password Security
```
Plain Password → BCryptPasswordEncoder → Hashed Password (Stored)
                 (Cost Factor: 10)

Example:
"Password@123" → "$2a$10$..." (60 characters)
```

### JWT Token Structure
```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "john@example.com",
  "iat": 1708177845,
  "exp": 1708264245
}

Signature:
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret
)
```

### OTP Generation
```java
SecureRandom random = new SecureRandom();
int otp = 100000 + random.nextInt(900000);
// Generates: 100000 to 999999
```

## Database Design

### Entity Relationships
```
┌─────────────────┐          ┌─────────────────┐
│     users       │          │    user_otp     │
├─────────────────┤          ├─────────────────┤
│ id (PK)         │◄─────────┤ id (PK)         │
│ name            │          │ user_id (FK)    │
│ email (UNIQUE)  │          │ otp             │
│ password        │          │ purpose (ENUM)  │
│ phone           │          │ expires_at      │
│ is_active       │          │ is_used         │
│ created_at      │          │ created_at      │
│ updated_at      │          └─────────────────┘
└─────────────────┘
   ONE-TO-MANY
```

### Indexes
```sql
-- For fast email lookups
CREATE INDEX idx_email ON users(email);

-- For OTP verification queries
CREATE INDEX idx_user_otp ON user_otp(user_id, otp, purpose);

-- For OTP cleanup queries
CREATE INDEX idx_expires_at ON user_otp(expires_at);
```

## Exception Handling Architecture

### Exception Hierarchy
```
RuntimeException
    │
    ├── UserAlreadyExistsException (409 Conflict)
    ├── UserNotFoundException (404 Not Found)
    ├── InvalidCredentialsException (401 Unauthorized)
    └── InvalidOtpException (400 Bad Request)
```

### Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Catches all exceptions from controllers
    // Returns consistent ApiResponse format
    // Maps exceptions to HTTP status codes
}
```

### Validation Exception Flow
```
1. @Valid triggers validation
2. MethodArgumentNotValidException thrown
3. GlobalExceptionHandler catches it
4. Extracts field errors
5. Returns 400 Bad Request with field-level errors
```

## Configuration Architecture

### application.properties Structure
```
Server Configuration
    ├── Port
    └── Application Name

Database Configuration
    ├── URL
    ├── Username
    ├── Password
    └── Driver Class

JPA Configuration
    ├── DDL Auto (update/create/validate)
    ├── Show SQL
    └── Hibernate Dialect

JWT Configuration
    ├── Secret Key
    └── Expiration Time

Logging Configuration
    ├── Log Levels
    └── Log Pattern
```

## Design Patterns Used

### 1. Layered Architecture Pattern
- Clear separation of concerns
- Each layer has single responsibility
- Easy to test and maintain

### 2. Repository Pattern
- Abstracts data access
- Provides clean API for database operations
- Easy to switch databases

### 3. DTO Pattern
- Separates internal models from API contracts
- Prevents over-posting/under-posting
- Clean validation

### 4. Builder Pattern (via Lombok)
- Fluent object creation
- Immutable objects
- Readable code

### 5. Singleton Pattern
- Spring beans are singletons by default
- Services, Repositories, Utilities

### 6. Strategy Pattern
- Spring Security's AuthenticationProvider
- Pluggable authentication strategies

## Transaction Management

### @Transactional Usage
```java
@Transactional
public AuthResponse signup(SignupRequest request) {
    // All database operations are atomic
    // If any operation fails, all rollback
    // Ensures data consistency
}
```

### Transaction Scope
- Service layer methods
- Ensures ACID properties
- Rollback on RuntimeException
- Commit on successful completion

## Performance Considerations

### Database Optimization
1. **Indexes**: On email, user_id, otp fields
2. **Connection Pooling**: HikariCP (default)
3. **Lazy Loading**: FetchType.LAZY for associations
4. **Batch Processing**: For bulk operations

### Security Performance
1. **BCrypt Cost Factor**: Balanced at 10
2. **JWT Stateless**: No session storage
3. **OTP Cleanup**: Regular deletion of expired OTPs
4. **Password Validation**: Early rejection of invalid formats

## Scalability Features

### Horizontal Scaling Ready
- **Stateless Architecture**: JWT tokens, no sessions
- **Database-Independent**: Works with replicated DBs
- **No File System Dependency**: All data in DB

### Future Enhancements
1. **Redis Caching**: For OTPs and tokens
2. **Message Queue**: For email/SMS sending
3. **Load Balancer**: Multiple instances
4. **Database Sharding**: For millions of users

## Testing Strategy

### Unit Testing
- Service layer methods
- Utility classes
- Exception scenarios

### Integration Testing
- Controller endpoints
- Database operations
- Security configuration

### Test Coverage Areas
1. ✅ Valid inputs
2. ✅ Invalid inputs
3. ✅ Edge cases
4. ✅ Security scenarios
5. ✅ Error handling

## Monitoring & Logging

### Log Levels
- **INFO**: Business operations (signup, login)
- **DEBUG**: Spring Security details
- **ERROR**: Exceptions and failures

### What to Monitor
1. Login success/failure rates
2. OTP generation frequency
3. Password reset attempts
4. Database connection pool
5. API response times

## Deployment Architecture

### Development Environment
```
Local Machine
    └── Embedded Tomcat (8080)
    └── Local MySQL
    └── Application Logs
```

### Production Environment (Recommended)
```
Load Balancer
    │
    ├── App Server 1 (Docker Container)
    ├── App Server 2 (Docker Container)
    └── App Server 3 (Docker Container)
            │
            └── MySQL Master
                    └── MySQL Replicas
```

## Security Best Practices Implemented

✅ Passwords never stored in plain text
✅ BCrypt with salt
✅ JWT tokens for stateless auth
✅ OTP expiry mechanism
✅ Single-use OTPs
✅ Email uniqueness enforced
✅ Input validation at all layers
✅ SQL injection prevention (JPA)
✅ No sensitive data in logs
✅ CSRF disabled (stateless REST API)
✅ Exception messages don't leak sensitive info

---

**This architecture ensures**: Scalability, Security, Maintainability, and Testability

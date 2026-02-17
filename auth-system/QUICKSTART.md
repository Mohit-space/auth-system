# ⚡ Quick Start Guide

Get the authentication system running in 5 minutes!

## Step 1: Prerequisites Check ✅
```bash
# Check Java (needs 17+)
java -version

# Check Maven
mvn -version

# Check MySQL
mysql --version
```

## Step 2: Database Setup 🗄️
```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE auth_db;
exit;
```

## Step 3: Configure Application ⚙️
Edit `src/main/resources/application.properties`:
```properties
# Update these lines with your MySQL credentials
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

## Step 4: Run Application 🚀
```bash
# Build and run
mvn clean install
mvn spring-boot:run
```

Wait for: `Started AuthSystemApplication in X seconds`

## Step 5: Test with Postman 🧪

### Import Collection
1. Open Postman
2. Import → Upload Files → Select `postman_collection.json`

### Test APIs (in order)
1. **Health Check** - `GET /api/auth/health`
   - Should return "OK"

2. **Signup** - `POST /api/auth/signup`
   ```json
   {
     "name": "John Doe",
     "email": "john@example.com",
     "password": "Password@123",
     "phone": "9876543210"
   }
   ```
   - Save the JWT token from response

3. **Login** - `POST /api/auth/login`
   ```json
   {
     "email": "john@example.com",
     "password": "Password@123"
   }
   ```

4. **Forgot Password** - `POST /api/auth/forgot-password`
   ```json
   {
     "email": "john@example.com"
   }
   ```
   - Check console logs for OTP (6-digit number)

5. **Reset Password** - `POST /api/auth/reset-password`
   ```json
   {
     "email": "john@example.com",
     "otp": "123456",
     "newPassword": "NewPassword@123"
   }
   ```

6. **Login Again** with new password ✅

## Common Issues 🔧

**Port 8080 already in use?**
```bash
# Change port in application.properties
server.port=8081
```

**Can't connect to MySQL?**
```bash
# Start MySQL service
sudo service mysql start
# OR on Windows
net start MySQL
```

**Tables not created?**
```bash
# Run the schema manually
mysql -u root -p auth_db < schema.sql
```

## Success Indicators ✨
- ✅ Application starts on port 8080
- ✅ Database connection successful
- ✅ Tables created automatically
- ✅ Health check returns 200 OK
- ✅ Signup creates user and returns token
- ✅ Login authenticates successfully

## Next Steps 📚
- Review the full [README.md](README.md) for complete documentation
- Explore the code structure
- Test error scenarios
- Customize configurations

## Need Help? 💬
- Check application logs
- Verify database credentials
- Ensure Java 17+ is used
- Review error messages in Postman

---
**Ready to code! 🚀** Happy Testing!

package com.intern.task.authsystem.service;

import com.intern.task.authsystem.dto.*;
import com.intern.task.authsystem.entity.User;
import com.intern.task.authsystem.entity.UserOtp;
import com.intern.task.authsystem.enums.OtpPurpose;
import com.intern.task.authsystem.exception.InvalidCredentialsException;
import com.intern.task.authsystem.exception.InvalidOtpException;
import com.intern.task.authsystem.exception.UserAlreadyExistsException;
import com.intern.task.authsystem.exception.UserNotFoundException;
import com.intern.task.authsystem.repository.UserOtpRepository;
import com.intern.task.authsystem.repository.UserRepository;
import com.intern.task.authsystem.util.JwtUtil;
import com.intern.task.authsystem.util.OtpGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final UserOtpRepository userOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final OtpGenerator otpGenerator;
    
    @Override
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        log.info("Processing signup request for email: {}", request.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }
        
        // Create new user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getEmail());
        
        // Convert to response
        UserResponse userResponse = convertToUserResponse(savedUser);
        
        return AuthResponse.builder()
                .message("User registered successfully")
                .user(userResponse)
                .token(token)
                .build();
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Processing login request for email: {}", request.getEmail());
        
        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        // Fetch user details
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());
        
        log.info("User logged in successfully: {}", user.getEmail());
        
        // Convert to response
        UserResponse userResponse = convertToUserResponse(user);
        
        return AuthResponse.builder()
                .message("Login successful")
                .user(userResponse)
                .token(token)
                .build();
    }
    
    @Override
    @Transactional
    public ApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
        log.info("Processing forgot password request for email: {}", request.getEmail());
        
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));
        
        // Delete any existing OTPs for this user and purpose
        userOtpRepository.deleteByUserIdAndPurpose(user.getId(), OtpPurpose.PASSWORD_RESET);
        
        // Generate OTP
        String otp = otpGenerator.generateOtp();
        
        // Create OTP entity
        UserOtp userOtp = UserOtp.builder()
                .userId(user.getId())
                .otp(otp)
                .purpose(OtpPurpose.PASSWORD_RESET)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .isUsed(false)
                .build();
        
        userOtpRepository.save(userOtp);
        
        log.info("Password reset OTP generated for user: {}", user.getEmail());
        log.info("OTP for testing: {}", otp); // In production, send this via email/SMS
        
        return ApiResponse.success(
                "Password reset OTP has been sent to your email. OTP: " + otp + " (Valid for 10 minutes)",
                null
        );
    }
    
    @Override
    @Transactional
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        log.info("Processing reset password request for email: {}", request.getEmail());
        
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));
        
        // Verify OTP
        UserOtp userOtp = userOtpRepository.findByUserIdAndOtpAndPurposeAndIsUsedFalseAndExpiresAtAfter(
                        user.getId(),
                        request.getOtp(),
                        OtpPurpose.PASSWORD_RESET,
                        LocalDateTime.now()
                )
                .orElseThrow(() -> new InvalidOtpException("Invalid or expired OTP"));
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Mark OTP as used
        userOtp.setIsUsed(true);
        userOtpRepository.save(userOtp);
        
        log.info("Password reset successfully for user: {}", user.getEmail());
        
        return ApiResponse.success("Password has been reset successfully", null);
    }
    
    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

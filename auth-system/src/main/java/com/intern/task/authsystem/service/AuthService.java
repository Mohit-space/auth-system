package com.intern.task.authsystem.service;

import com.intern.task.authsystem.dto.*;

public interface AuthService {
    
    AuthResponse signup(SignupRequest request);
    
    AuthResponse login(LoginRequest request);
    
    ApiResponse<String> forgotPassword(ForgotPasswordRequest request);
    
    ApiResponse<String> resetPassword(ResetPasswordRequest request);
}

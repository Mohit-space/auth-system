package com.intern.task.authsystem.repository;

import com.intern.task.authsystem.entity.UserOtp;
import com.intern.task.authsystem.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {
    
    Optional<UserOtp> findByUserIdAndOtpAndPurposeAndIsUsedFalseAndExpiresAtAfter(
            Long userId, 
            String otp, 
            OtpPurpose purpose, 
            LocalDateTime currentTime
    );
    
    void deleteByUserIdAndPurpose(Long userId, OtpPurpose purpose);
}

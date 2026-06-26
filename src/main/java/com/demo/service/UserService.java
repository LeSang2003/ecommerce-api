package com.demo.service;

import com.demo.model.EmailVerificationToken;
import com.demo.model.PasswordResetToken;
import com.demo.model.User;

import com.demo.repository.EmailVerificationTokenRepository;
import com.demo.repository.PasswordResetTokenRepository;
import com.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final MailService mailService;

    private final EmailVerificationTokenRepository tokenRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;

    // =========================
    // FIND USER
    // =========================

    public User findByUsername(
            String username
    ) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }

    // =========================
    // SEND VERIFY EMAIL
    // =========================

    public void sendVerificationEmail(
            User user
    ) {

        String token =
                UUID.randomUUID().toString();

        EmailVerificationToken verifyToken =
                new EmailVerificationToken();

        verifyToken.setToken(token);

        verifyToken.setUser(user);

        verifyToken.setExpiryDate(
                LocalDateTime.now().plusHours(24)
        );

        tokenRepository.save(verifyToken);

        mailService.sendVerificationEmail(
                user.getEmail(),
                token
        );
    }

    // =========================
    // VERIFY EMAIL
    // =========================

    public void verifyEmail(
            String token
    ) {

        EmailVerificationToken verifyToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid token"
                                )
                        );

        // TOKEN EXPIRED
        if (
                verifyToken.getExpiryDate()
                        .isBefore(LocalDateTime.now())
        ) {

            throw new RuntimeException(
                    "Token expired"
            );
        }

        User user =
                verifyToken.getUser();

        user.setEnabled(true);

        userRepository.save(user);

        tokenRepository.delete(verifyToken);
    }

    // =========================
    // FORGOT PASSWORD
    // =========================

    public void forgotPassword(
            String email
    ) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Email not found"
                                )
                        );

        // DELETE OLD TOKEN
        PasswordResetToken oldToken =
                passwordResetTokenRepository
                        .findByUser(user)
                        .orElse(null);

        if (oldToken != null) {

            passwordResetTokenRepository
                    .delete(oldToken);
        }

        String token =
                UUID.randomUUID().toString();

        PasswordResetToken resetToken =
                new PasswordResetToken();

        resetToken.setToken(token);

        resetToken.setUser(user);

        resetToken.setExpiryDate(
                LocalDateTime.now().plusHours(1)
        );

        passwordResetTokenRepository
                .save(resetToken);

        mailService.sendResetPasswordEmail(
                user.getEmail(),
                token
        );
    }

    // =========================
    // RESET PASSWORD
    // =========================

    public void resetPassword(
            String token,
            String newPassword
    ) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid token"
                                )
                        );

        // TOKEN EXPIRED
        if (
                resetToken.getExpiryDate()
                        .isBefore(LocalDateTime.now())
        ) {

            throw new RuntimeException(
                    "Token expired"
            );
        }

        User user =
                resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        passwordResetTokenRepository
                .delete(resetToken);
    }
}
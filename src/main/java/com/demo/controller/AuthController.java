package com.demo.controller;

import com.demo.dto.ForgotPasswordRequest;
import com.demo.dto.LoginRequest;
import com.demo.dto.RegisterRequest;
import com.demo.dto.ResetPasswordRequest;
import com.demo.model.Role;
import com.demo.model.User;
import com.demo.repository.UserRepository;
import com.demo.security.JwtService;
import com.demo.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserService userService;

    // =========================
    // REGISTER
    // =========================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {

        // USERNAME EXISTS
        if (
                userRepository
                        .findByUsername(
                                request.getUsername()
                        )
                        .isPresent()
        ) {

            return ResponseEntity.badRequest()
                    .body("Username already exists");
        }

        // EMAIL EXISTS
        if (
                userRepository
                        .findByEmail(
                                request.getEmail()
                        )
                        .isPresent()
        ) {

            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        // CREATE USER
        User user = new User();

        user.setUsername(
                request.getUsername()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        // DEFAULT ROLE
        user.setRole(Role.USER);

        // EMAIL NOT VERIFIED
        user.setEnabled(false);

        userRepository.save(user);

        // SEND VERIFY MAIL
        userService.sendVerificationEmail(
                user
        );

        return ResponseEntity.ok(
                "Register success. Please verify your email."
        );
    }

    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
    public String login(
            @RequestBody LoginRequest request
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername().trim(),
                        request.getPassword().trim()
                )
        );

        User user =
                userRepository
                        .findByUsername(
                                request.getUsername()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        return jwtService.generateToken(user);
    }

    // =========================
    // VERIFY EMAIL
    // =========================

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(
            @RequestParam String token
    ) {

        userService.verifyEmail(token);

        return ResponseEntity.ok(
                "Email verified successfully"
        );
    }
         @PostMapping("/forgot-password")
        public ResponseEntity<?> forgotPassword(
        @RequestBody ForgotPasswordRequest request
        ) {

        userService.forgotPassword(
            request.getEmail()
        );

        return ResponseEntity.ok(
            "Reset password email sent"
        );
    }
    @PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(
        @RequestBody ResetPasswordRequest request
) {

    userService.resetPassword(
            request.getToken(),
            request.getPassword()
    );

    return ResponseEntity.ok(
            "Password reset successfully"
    );
}
}
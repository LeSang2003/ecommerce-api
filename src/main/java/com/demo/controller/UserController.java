package com.demo.controller;

import com.demo.dto.UpdateProfileRequest;
import com.demo.model.User;
import com.demo.repository.UserRepository;
import com.demo.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.demo.dto.ChangePasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    // =========================
    // GET PROFILE
    // =========================

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(
            Authentication authentication
    ) {

        User user =
                userService.findByUsername(
                        authentication.getName()
                );

        return ResponseEntity.ok(user);
    }

    // =========================
    // UPDATE PROFILE
    // =========================

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request
    ) {

        User user =
                userService.findByUsername(
                        authentication.getName()
                );

        user.setFullName(request.getFullName());

        user.setPhone(request.getPhone());

        user.setAddress(request.getAddress());

        user.setBirthday(request.getBirthday());

        user.setGender(request.getGender());

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    // =========================
    // UPDATE AVATAR
    // =========================

    @PutMapping("/avatar")
        public ResponseEntity<?> updateAvatar(
        Authentication authentication,
        @RequestBody Map<String, String> body
        ) {

    System.out.println("BODY: " + body);

    User user =
            userService.findByUsername(
                    authentication.getName()
            );

    String avatar = body.get("avatar");

    System.out.println("AVATAR: " + avatar);

    user.setAvatar(avatar);

    userRepository.save(user);

    System.out.println("SAVED");

    return ResponseEntity.ok(user);
  }
        // =========================
        // CHANGE PASSWORD
        // =========================
  // =========================
// CHANGE PASSWORD
// =========================

@PutMapping("/change-password")
public ResponseEntity<?> changePassword(
        Authentication authentication,
        @RequestBody ChangePasswordRequest request
) {

    User user =
            userService.findByUsername(
                    authentication.getName()
            );

    // CHECK OLD PASSWORD

    if (
            !passwordEncoder.matches(
                    request.getOldPassword(),
                    user.getPassword()
            )
    ) {

        return ResponseEntity.badRequest()
                .body("Old password incorrect");
    }

    // CHECK CONFIRM PASSWORD

    if (
            !request.getNewPassword()
                    .equals(request.getConfirmPassword())
    ) {

        return ResponseEntity.badRequest()
                .body("Confirm password not match");
    }

    // SAVE NEW PASSWORD

    user.setPassword(
            passwordEncoder.encode(
                    request.getNewPassword()
            )
    );

    userRepository.save(user);

    return ResponseEntity.ok(
            "Password changed successfully"
    );
}

}
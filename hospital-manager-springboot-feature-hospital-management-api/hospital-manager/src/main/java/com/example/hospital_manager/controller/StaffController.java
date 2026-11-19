package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.UserResponse;
import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.UserRepository;
import com.example.hospital_manager.service.AuthService;
import com.example.hospital_manager.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StaffController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/admin/doctors")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse addDoctor(
            @Valid @RequestBody RegisterRequest request) {

        if (request.role() != Role.DOCTOR) {
            throw new IllegalArgumentException(
                    "Only DOCTOR accounts can be created here");
        }

        return authService.register(request);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {

        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole().name()
                ))
                .toList();
    }

    @GetMapping("/doctors")
    @PreAuthorize("isAuthenticated()")
    public List<UserResponse> getDoctors() {

        return userRepository.findByRole(Role.DOCTOR)
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole().name()
                ))
                .toList();
    }
}
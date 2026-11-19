package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.UserRepository;
import com.example.hospital_manager.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new ApiException(
                    HttpStatus.CONFLICT, "Username already exists");
        }

        AppUser user = AppUser.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .enabled(true)
                .build();

        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        AppUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "User not found"));

        return new AuthResponse(
                jwtService.generateToken(user),
                "Bearer",
                jwtService.getExpiration(),
                user.getRole().name()
        );
    }

    public UserResponse me(String username) {

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "User not found"));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}
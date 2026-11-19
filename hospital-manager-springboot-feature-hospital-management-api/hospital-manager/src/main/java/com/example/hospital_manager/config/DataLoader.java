
package com.example.hospital_manager.config;

import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner loadData() {

        return args -> {

            if (userRepository.count() == 0) {

                AppUser admin = createUser(
                        "admin", "Admin@123", Role.ADMIN);

                AppUser doctor = createUser(
                        "doctor", "Doctor@123", Role.DOCTOR);

                AppUser patientUser = createUser(
                        "patient", "Patient@123", Role.PATIENT);

                AppUser nurse = createUser(
                        "nurse", "Nurse@123", Role.NURSE);

                AppUser receptionist = createUser(
                        "receptionist", "Reception@123",
                        Role.RECEPTIONIST);

                userRepository.save(admin);
                userRepository.save(doctor);
                userRepository.save(patientUser);
                userRepository.save(nurse);
                userRepository.save(receptionist);

                Patient patient = Patient.builder()
                        .name("John Smith")
                        .email("john@example.com")
                        .phone("0771234567")
                        .user(patientUser)
                        .build();

                patientRepository.save(patient);

                System.out.println("Sample accounts created.");
            }
        };
    }

    private AppUser createUser(
            String username,
            String password,
            Role role) {

        return AppUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .enabled(true)
                .build();
    }
}

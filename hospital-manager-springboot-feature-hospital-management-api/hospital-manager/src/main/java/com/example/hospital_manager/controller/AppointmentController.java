package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/patients/appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentResponse book(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {

        return appointmentService.bookAppointment(
                request, authentication.getName());
    }

    @GetMapping("/doctors/appointments")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public List<AppointmentResponse> doctorAppointments(
            Authentication authentication) {

        return appointmentService.getDoctorAppointments(
                authentication.getName());
    }

    @GetMapping("/patients/appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public List<AppointmentResponse> patientAppointments(
            Authentication authentication) {

        return appointmentService.getPatientAppointments(
                authentication.getName());
    }

    @PatchMapping("/appointments/{id}/status")
    @PreAuthorize("hasRole('DOCTOR')")
    public AppointmentResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request,
            Authentication authentication) {

        return appointmentService.updateStatus(
                id, request, authentication.getName());
    }
}
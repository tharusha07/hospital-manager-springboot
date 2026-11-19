package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public AppointmentResponse bookAppointment(
            AppointmentRequest request,
            String username) {

        AppUser currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "User not found"));

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Patient not found"));

        // Patients can only book for themselves.
        if (currentUser.getRole() == Role.PATIENT &&
                (patient.getUser() == null ||
                 !patient.getUser().getId().equals(currentUser.getId()))) {

            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "You can only book appointments for yourself"
            );
        }

        AppUser doctor = userRepository.findById(request.doctorId())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Doctor not found"));

        if (doctor.getRole() != Role.DOCTOR) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST, "Selected user is not a doctor");
        }

        if (request.appointmentDate().isBefore(LocalDateTime.now())) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Appointment date must be in the future");
        }

        boolean exists = appointmentRepository
                .existsByDoctorIdAndAppointmentDate(
                        doctor.getId(),
                        request.appointmentDate()
                );

        if (exists) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Doctor already has an appointment at this time");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(request.appointmentDate())
                .reason(request.reason())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        return toResponse(appointmentRepository.save(appointment));
    }

    public List<AppointmentResponse> getDoctorAppointments(
            String username) {

        AppUser doctor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Doctor not found"));

        return appointmentRepository.findByDoctorId(doctor.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AppointmentResponse> getPatientAppointments(
            String username) {

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "User not found"));

        Patient patient = patientRepository.findAll().stream()
                .filter(p -> p.getUser() != null &&
                        p.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Patient profile not found"));

        return appointmentRepository.findByPatientId(patient.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AppointmentResponse updateStatus(
            Long id, StatusRequest request, String username) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Appointment not found"));

        if (!appointment.getDoctor().getUsername().equals(username)) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "You cannot update another doctor's appointment");
        }

        appointment.setStatus(request.status());

        return toResponse(appointmentRepository.save(appointment));
    }

    private AppointmentResponse toResponse(Appointment a) {

        return new AppointmentResponse(
                a.getId(),
                a.getPatient().getId(),
                a.getPatient().getName(),
                a.getDoctor().getId(),
                a.getDoctor().getUsername(),
                a.getAppointmentDate(),
                a.getReason(),
                a.getStatus().name()
        );
    }
}